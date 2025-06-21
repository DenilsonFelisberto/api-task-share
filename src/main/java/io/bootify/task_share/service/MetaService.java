package io.bootify.task_share.service;

import io.bootify.task_share.model.Meta;

import java.util.List;

public interface MetaService {

    List<Meta> findAll();

    Meta findById(Long id);

    Meta save(Meta meta);

    void deleteById(Long id);
}
