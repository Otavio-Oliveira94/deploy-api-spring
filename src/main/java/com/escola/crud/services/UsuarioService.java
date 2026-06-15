package com.escola.crud.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.escola.crud.dtos.UsuarioDTO;
import com.escola.crud.entities.Usuario;
import com.escola.crud.repositories.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public UsuarioDTO criar(UsuarioDTO dto) {

        if (repository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email ja cadastrado.");
        }

        Usuario usuario = new Usuario(null, dto.getNome(), dto.getEmail());
        Usuario salvo = repository.save(usuario);
        return new UsuarioDTO(salvo.getId(), salvo.getNome(), salvo.getEmail());
    }

    public List<UsuarioDTO> listarTodos() {
        return repository.findAll().stream()
                .map(u -> new UsuarioDTO(u.getId(), u.getNome(), u.getEmail()))
                .toList();
    }

    public UsuarioDTO buscarPorId(Long id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado."));
        return new UsuarioDTO(usuario.getId(), usuario.getNome(), usuario.getEmail());
    }

    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Impossivel deletar. ID inexistente.");
        }
        repository.deleteById(id);
    }

}
