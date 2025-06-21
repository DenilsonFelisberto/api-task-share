package io.bootify.task_share.controller;

import com.google.gson.Gson;
import io.bootify.task_share.model.Usuario;
import io.bootify.task_share.service.UsuarioService;
import io.bootify.task_share.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
public class UsuarioController {

    @Autowired
    UsuarioService service_usuario;

    @RequestMapping(value = "/login", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ResponseEntity<Map<String, String>> fazerLogin(@RequestBody Map<String, String> json) throws NoSuchAlgorithmException {

        Map<String, String> mapRetorno = new HashMap<>();

        // Validação de campos obrigatórios
        String email = json.get("email");
        String senha = json.get("senha");

        if (email.isEmpty() || senha.isEmpty()) {
            mapRetorno.put("type", "error");
            mapRetorno.put("message", "E-mail e senha são obrigatórios.");
            return ResponseEntity.badRequest().body(mapRetorno);
        }

        // Busca o usuário pelo e-mail
        Usuario usuario_busca = service_usuario.findByEmail(email);

        if (usuario_busca != null && usuario_busca.getSenha().equals(Util.md5(senha))) {
            Map<String, String> usuario = new HashMap<>();
            usuario.put("id", Util.md5(String.valueOf(usuario_busca.getId())));
            usuario.put("authToken", usuario_busca.getAuthToken());
            usuario.put("nome", usuario_busca.getNome());
            usuario.put("email", usuario_busca.getEmail());
            usuario.put("dataNascimento", String.valueOf(usuario_busca.getDataNascimento()));
            usuario.put("fotoPerfil", usuario_busca.getFotoPerfil());

            mapRetorno.put("type", "success");
            mapRetorno.put("usuario", new Gson().toJson(usuario));
        } else {
            mapRetorno.put("type", "error");
            mapRetorno.put("message", "E-mail ou senha inválidos.");
        }

        return ResponseEntity.ok(mapRetorno);
    }

    @RequestMapping(value = "/cadastro", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ResponseEntity<Map<String, String>> cadastrarUsuario(@RequestBody Map<String, String> json) throws NoSuchAlgorithmException {

        System.out.println(json);

        Map<String, String> mapRetorno = new HashMap<>();
        Gson gson = new Gson();

        // Verificar se os dados obrigatórios estão presentes
        if (json.get("nome").isEmpty() || json.get("email").isEmpty() || json.get("senha").isEmpty() || json.get("data_nascimento").isEmpty()) {
            mapRetorno.put("type", "error");
            mapRetorno.put("message", "Todos os campos são obrigatórios!");
            return ResponseEntity.ok(mapRetorno);
        }

        // Verificar se o e-mail já está cadastrado
        boolean emailExistente = service_usuario.findAll().stream()
                .anyMatch(u -> u.getEmail().equals(json.get("email")));

        if (emailExistente) {
            mapRetorno.put("type", "error");
            mapRetorno.put("message", "E-mail já cadastrado!");
            return ResponseEntity.ok(mapRetorno);
        }

        // Criar novo usuário
        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(json.get("nome"));
        novoUsuario.setDataNascimento(LocalDate.parse(json.get("data_nascimento")));
        novoUsuario.setEmail(json.get("email"));
        novoUsuario.setSenha(Util.md5(json.get("senha"))); // Criptografar a senha

        // Gerar authToken único
        String authToken = "";
        String finalAuthToken = authToken;

        do {
            authToken = gerarAuthToken(novoUsuario.getId());
        } while (service_usuario.findAll().stream().anyMatch(u -> u.getAuthToken().equals(finalAuthToken)));

        novoUsuario.setAuthToken(authToken);

        // Salvar o usuário no banco
        service_usuario.save(novoUsuario);

        // Preparar resposta de sucesso
        Map<String, String> usuario = new HashMap<>();
        usuario.put("id", Util.md5(String.valueOf(novoUsuario.getId())));
        usuario.put("authToken", novoUsuario.getAuthToken());
        usuario.put("nome", novoUsuario.getNome());
        usuario.put("email", novoUsuario.getEmail());
        usuario.put("dataNascimento", String.valueOf(novoUsuario.getDataNascimento()));

        mapRetorno.put("type", "success");
        mapRetorno.put("usuario", gson.toJson(usuario));
        return ResponseEntity.ok(mapRetorno);
    }

    private String gerarAuthToken(Long userId) throws NoSuchAlgorithmException {
        // Combinação do ID do usuário com um UUID para garantir unicidade
        String tokenBase = userId + "-" + UUID.randomUUID().toString();
        return Util.md5(tokenBase); // Criptografa o token gerado
    }

    @RequestMapping(value = "/usuario/atualizar", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ResponseEntity<Map<String, String>> atualizarUsuario(@RequestBody Map<String, String> json) throws NoSuchAlgorithmException {

//        System.out.println(json);

        Map<String, String> mapRetorno = new HashMap<>();

        Gson gson = new Gson();

        Usuario usuario_autenticado = service_usuario.findByAuthToken(json.get("authToken"));

        if (usuario_autenticado != null) {

            usuario_autenticado.setNome(json.get("nome"));
            usuario_autenticado.setEmail(json.get("email"));
            usuario_autenticado.setDataNascimento(LocalDate.parse(json.get("dataNascimento")));
            usuario_autenticado.setFotoPerfil(json.get("fotoPerfil"));

            service_usuario.save(usuario_autenticado);

            // Preparar resposta de sucesso
            Map<String, String> usuario = new HashMap<>();
            usuario.put("id", Util.md5(String.valueOf(usuario_autenticado.getId())));
            usuario.put("authToken", usuario_autenticado.getAuthToken());
            usuario.put("nome", usuario_autenticado.getNome());
            usuario.put("email", usuario_autenticado.getEmail());
            usuario.put("dataNascimento", String.valueOf(usuario_autenticado.getDataNascimento()));
            usuario.put("fotoPerfil", usuario_autenticado.getFotoPerfil());

            mapRetorno.put("type", "success");
            mapRetorno.put("usuario", gson.toJson(usuario));

        } else {
            mapRetorno.put("type", "error");
            mapRetorno.put("message", "Usuário não encontrado!");
        }

        return ResponseEntity.ok(mapRetorno);
    }

}
