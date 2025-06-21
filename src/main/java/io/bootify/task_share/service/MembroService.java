package io.bootify.task_share.service;

import io.bootify.task_share.model.Membro;

import java.util.List;

public interface MembroService {

    List<Membro> findAll();

    Membro findById(Long id);

    Membro save(Membro membro);

    void deleteById(Long id);
}
