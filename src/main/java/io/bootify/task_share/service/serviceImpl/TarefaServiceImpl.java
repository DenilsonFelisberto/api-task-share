package io.bootify.task_share.service.serviceImpl;

import io.bootify.task_share.model.Tarefa;
import io.bootify.task_share.model.Usuario;
import io.bootify.task_share.repository.TarefaRepository;
import io.bootify.task_share.service.TarefaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TarefaServiceImpl implements TarefaService {

    @Autowired
    TarefaRepository repository;

    @Override
    public List<Tarefa> findAll() {
        return repository.findAll();
    }

    @Override
    public Tarefa findById(Long id) {
        return repository.findById(id).get();
    }

    @Override
    public Tarefa save(Tarefa tarefa) {
        return repository.save(tarefa);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<Tarefa> findByUsuario(Usuario usuario) {
        return repository.findByUsuario(usuario);
    }
}
