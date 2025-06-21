package io.bootify.task_share.service.serviceImpl;

import io.bootify.task_share.model.Usuario;
import io.bootify.task_share.repository.UsuarioRepository;
import io.bootify.task_share.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    UsuarioRepository repository;

    @Override
    public List<Usuario> findAll() {
        return repository.findAll();
    }

    @Override
    public Usuario findById(Long id) {
        return repository.findById(id).get();
    }

    @Override
    public Usuario save(Usuario usuario) {
        return repository.save(usuario);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Usuario findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    public Usuario findByAuthToken(String authToken) {
        return repository.findByAuthToken(authToken);
    }
}
