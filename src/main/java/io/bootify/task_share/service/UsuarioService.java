package io.bootify.task_share.service;

import io.bootify.task_share.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {

    List<Usuario> findAll();

    Usuario findById(Long id);

    Usuario save(Usuario usuario);

    void deleteById(Long id);

    Usuario findByEmail(String email);

    Usuario findByAuthToken(String authToken);
}
