package io.bootify.task_share.repository;

import io.bootify.task_share.model.Tarefa;
import io.bootify.task_share.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa, Long> {

    // Método para buscar todas as tarefas de um usuário específico
    List<Tarefa> findByUsuario(Usuario usuario);
}
