package io.bootify.task_share.service;

import io.bootify.task_share.model.Tarefa;
import io.bootify.task_share.model.Usuario;

import java.util.List;

public interface TarefaService {

    List<Tarefa> findAll();

    Tarefa findById(Long id);

    Tarefa save(Tarefa tarefa);

    void deleteById(Long id);

    List<Tarefa> findByUsuario(Usuario usuario);
}
