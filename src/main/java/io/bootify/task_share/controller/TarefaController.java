package io.bootify.task_share.controller;

import com.google.gson.Gson;
import io.bootify.task_share.model.Tarefa;
import io.bootify.task_share.model.Usuario;
import io.bootify.task_share.service.TarefaService;
import io.bootify.task_share.service.UsuarioService;
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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class TarefaController {

    @Autowired
    TarefaService service_tarefa;

    @Autowired
    UsuarioService service_usuario;

    @RequestMapping(value = "/criar-tarefa", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ResponseEntity<Map<String, String>> criarTarefa(@RequestBody Map<String, String> json) throws NoSuchAlgorithmException {

//        System.out.println(json);

        Map<String, String> mapRetorno = new HashMap<>();

        Gson gson = new Gson();

        Usuario usuario_autenticado = service_usuario.findByAuthToken(json.get("authToken"));

        if (usuario_autenticado != null) {

            Tarefa tarefa = new Tarefa();

            tarefa.setTitulo(json.get("titulo"));
            tarefa.setDescricao(json.get("descricao"));
            tarefa.setDataHoraCriacao(LocalDateTime.now());
            tarefa.setDataInicioTarefa(LocalDate.parse(json.get("data")));
            if (json.get("alertar").equals("true")){
                tarefa.setAlertar(Boolean.parseBoolean(json.get("alertar")));
                tarefa.setHoraInicioTarefa(LocalTime.parse(json.get("horarioAlerta")));
            }else{
                tarefa.setAlertar(Boolean.parseBoolean(json.get("alertar")));
            }
            tarefa.setUsuario(usuario_autenticado);
            tarefa.setFinalizada(false);

            service_tarefa.save(tarefa);

            mapRetorno.put("type", "success");

        } else {
            mapRetorno.put("type", "error");
            mapRetorno.put("message", "Usuário não encontrado!");
        }

        return ResponseEntity.ok(mapRetorno);
    }

    @RequestMapping(value = "/tarefas-usuario", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ResponseEntity<Map<String, String>> tarefasUsuario(@RequestBody Map<String, String> json, Tarefa trf) throws NoSuchAlgorithmException {

        System.out.println(json);

        Map<String, String> mapRetorno = new HashMap<>();

        Gson gson = new Gson();

        Usuario usuario_autenticado = service_usuario.findByAuthToken(json.get("authToken"));

        if (usuario_autenticado != null) {

            ArrayList<String> arrayTarefasUsuario = new ArrayList<>();

            // Buscar todas as tarefas do usuário autenticado
            List<Tarefa> tarefasUsuario = service_tarefa.findByUsuario(usuario_autenticado);

            for(Tarefa t: trf.ordenaTarefas(tarefasUsuario)){

                arrayTarefasUsuario.add(t.toString());
            }

            mapRetorno.put("type", "success");
            mapRetorno.put("tarefas_usuario", gson.toJson(arrayTarefasUsuario));
            System.out.println(arrayTarefasUsuario);

        } else {
            mapRetorno.put("type", "error");
            mapRetorno.put("message", "Usuário não encontrado!");
        }

        return ResponseEntity.ok(mapRetorno);
    }
}
