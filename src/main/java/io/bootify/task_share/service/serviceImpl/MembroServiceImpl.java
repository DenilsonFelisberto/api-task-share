package io.bootify.task_share.service.serviceImpl;

import io.bootify.task_share.model.Membro;
import io.bootify.task_share.repository.MembroRepository;
import io.bootify.task_share.service.MembroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MembroServiceImpl implements MembroService {

    @Autowired
    MembroRepository repository;

    @Override
    public List<Membro> findAll() {
        return repository.findAll();
    }

    @Override
    public Membro findById(Long id) {
        return repository.findById(id).get();
    }

    @Override
    public Membro save(Membro membro) {
        return repository.save(membro);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
