package io.bootify.task_share.service.serviceImpl;

import io.bootify.task_share.model.Meta;
import io.bootify.task_share.repository.MetaRepository;
import io.bootify.task_share.service.MetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MetaServiceImpl implements MetaService {

    @Autowired
    MetaRepository repository;

    @Override
    public List<Meta> findAll() {
        return repository.findAll();
    }

    @Override
    public Meta findById(Long id) {
        return repository.findById(id).get();
    }

    @Override
    public Meta save(Meta meta) {
        return repository.save(meta);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
