package io.bootify.task_share.controller;

import com.google.gson.Gson;
import io.bootify.task_share.model.Meta;
import io.bootify.task_share.model.Tarefa;
import io.bootify.task_share.model.Usuario;
import io.bootify.task_share.service.MetaService;
import io.bootify.task_share.service.TarefaService;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class MetaController {

    @Autowired
    MetaService service_meta;

    @Autowired
    UsuarioService service_usuario;

    @Autowired
    TarefaService service_tarefa;

    @RequestMapping(value = "/criar-meta", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ResponseEntity<Map<String, String>> criarMeta(@RequestBody Map<String, String> json) throws NoSuchAlgorithmException {

        System.out.println(json);

        Map<String, String> mapRetorno = new HashMap<>();

        Gson gson = new Gson();

        Usuario usuario_autenticado = service_usuario.findByAuthToken(json.get("authToken"));

        if (usuario_autenticado != null) {

            Tarefa tarefa_busca = service_tarefa.findAll().stream()
                    .filter(t -> {
                        try {
                            return json.get("id_tarefa").equals(Util.md5(String.valueOf(t.getId())));
                        } catch (NoSuchAlgorithmException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .findAny()
                    .orElse(null);

            assert tarefa_busca != null;
            int maxPrioridade = tarefa_busca.getMetas()
                    .stream()
                    .mapToInt(Meta::getPrioridade)
                    .max()
                    .orElse(0); // Caso não exista nenhuma meta, começa com 0

            Meta meta = new Meta();

            meta.setDescricao(json.get("descricao"));
            meta.setDataHoraCriacao(LocalDateTime.now());
            meta.setAlcancada(false);
            meta.setDataHoraAlcanceMeta(null);
            meta.setTarefa(tarefa_busca);
            meta.setPrioridade(maxPrioridade + 1);

            service_meta.save(meta);

            mapRetorno.put("type", "success");

        } else {
            mapRetorno.put("type", "error");
            mapRetorno.put("message", "Usuário não encontrado!");
        }

        return ResponseEntity.ok(mapRetorno);
    }

    @RequestMapping(value = "/metas-tarefa", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ResponseEntity<Map<String, String>> metasTarefa(@RequestBody Map<String, String> json, Meta mt) throws NoSuchAlgorithmException {

        System.out.println(json);

        Map<String, String> mapRetorno = new HashMap<>();

        Gson gson = new Gson();

        Usuario usuario_autenticado = service_usuario.findByAuthToken(json.get("authToken"));

        if (usuario_autenticado != null) {

            ArrayList<String> arrayMetasTarefa = new ArrayList<>();

            Tarefa tarefa_busca = service_tarefa.findAll().stream()
                    .filter(t -> {
                        try {
                            return json.get("id_tarefa").equals(Util.md5(String.valueOf(t.getId())));
                        } catch (NoSuchAlgorithmException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .findAny()
                    .orElse(null);

            if (tarefa_busca != null) {

                for (Meta mtt : mt.ordenaMetas(tarefa_busca.getMetas())) {

                    arrayMetasTarefa.add(mtt.toString());
                }

                mapRetorno.put("type", "success");
                mapRetorno.put("metas_tarefa", gson.toJson(arrayMetasTarefa));
                System.out.println(arrayMetasTarefa);
            } else {
                mapRetorno.put("type", "error");
                mapRetorno.put("message", "Tarefa não encontrada!");
            }

        } else {
            mapRetorno.put("type", "error");
            mapRetorno.put("message", "Usuário não encontrado!");
        }

        return ResponseEntity.ok(mapRetorno);
    }

    @RequestMapping(value = "/marcar-desmarcar-meta", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, String>> marcarDesmarcarMeta(@RequestBody Map<String, String> json) {

        Map<String, String> mapRetorno = new HashMap<>();

        Usuario usuario_autenticado = service_usuario.findByAuthToken(json.get("authToken"));

        if (usuario_autenticado != null) {

            Meta meta = service_meta.findAll().stream()
                    .filter(m -> {
                        try {
                            return json.get("id_meta").equals(Util.md5(String.valueOf(m.getId())));
                        } catch (NoSuchAlgorithmException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .findAny()
                    .orElse(null);

            if (meta != null) {
                meta.setAlcancada(!meta.isAlcancada()); // Alterna entre true e false
                service_meta.save(meta);

                mapRetorno.put("type", "success");
                mapRetorno.put("message", "Meta atualizada com sucesso!");
            } else {
                mapRetorno.put("type", "error");
                mapRetorno.put("message", "Meta não encontrada!");
            }
        } else {
            mapRetorno.put("type", "error");
            mapRetorno.put("message", "Usuário não encontrado!");
        }

        return ResponseEntity.ok(mapRetorno);
    }

    @RequestMapping(value = "/editar-meta", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, String>> editarMeta(@RequestBody Map<String, String> json) {

        Map<String, String> mapRetorno = new HashMap<>();

        // Busca o usuário autenticado pelo token
        Usuario usuario_autenticado = service_usuario.findByAuthToken(json.get("authToken"));

        if (usuario_autenticado != null) {

            // Busca a meta pelo ID fornecido
            Meta meta = service_meta.findAll().stream()
                    .filter(m -> {
                        try {
                            return json.get("id_meta").equals(Util.md5(String.valueOf(m.getId())));
                        } catch (NoSuchAlgorithmException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .findAny()
                    .orElse(null);

            if (meta != null) {
                // Atualiza a descrição da meta
                meta.setDescricao(json.get("descricao"));
                service_meta.save(meta);

                mapRetorno.put("type", "success");
                mapRetorno.put("message", "Dados atualizados com sucesso!");
            } else {
                mapRetorno.put("type", "error");
                mapRetorno.put("message", "Meta não encontrada!");
            }
        } else {
            mapRetorno.put("type", "error");
            mapRetorno.put("message", "Usuário não autenticado!");
        }

        return ResponseEntity.ok(mapRetorno);
    }

    @RequestMapping(value = "/excluir-meta", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, String>> excluirMeta(@RequestBody Map<String, String> json) {

        Map<String, String> mapRetorno = new HashMap<>();

        // Busca o usuário autenticado pelo token
        Usuario usuario_autenticado = service_usuario.findByAuthToken(json.get("authToken"));

        if (usuario_autenticado != null) {

            // Busca a meta pelo ID fornecido
            Meta meta = service_meta.findAll().stream()
                    .filter(m -> {
                        try {
                            return json.get("id_meta").equals(Util.md5(String.valueOf(m.getId())));
                        } catch (NoSuchAlgorithmException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .findAny()
                    .orElse(null);

            if (meta != null) {
                int prioridadeExcluida = meta.getPrioridade();
                Long idTarefa = meta.getTarefa().getId(); // Supondo que a meta pertence a uma tarefa

                // Exclui meta
                service_meta.deleteById(meta.getId());

                // Atualiza a prioridade das outras metas da mesma tarefa
                List<Meta> metasDaTarefa = service_tarefa.findById(idTarefa).getMetas();

                for (Meta m : metasDaTarefa) {
                    if (m.getPrioridade() > prioridadeExcluida) {
                        m.setPrioridade(m.getPrioridade() - 1);
                        service_meta.save(m); // Atualiza no banco
                    }
                }

                mapRetorno.put("type", "success");
                mapRetorno.put("message", "Meta excluída com sucesso!");
            } else {
                mapRetorno.put("type", "error");
                mapRetorno.put("message", "Meta não encontrada!");
            }
        } else {
            mapRetorno.put("type", "error");
            mapRetorno.put("message", "Usuário não autenticado!");
        }

        return ResponseEntity.ok(mapRetorno);
    }
}
