package com.escola.crud.services;

import com.escola.crud.dtos.UsuarioDTO;
import com.escola.crud.entities.Usuario;
import com.escola.crud.repositories.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) //RESPONSAVEL POR MOCKAR OS DADOS SIMILUANDO AS CHAMADAS
public class UsuarioServiceTest {
    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private UsuarioService service;

    @Test
    void deveCriarUmUsuarioComSucesso() {
        //ARRANGE -> organizar o cenario (Instaciar um usuario e mockar ele)
        UsuarioDTO usuarioDTO = new UsuarioDTO(null, "Otavio", "otavio@gmail.com");
        Usuario usuarioDB= new Usuario(1L, "Otavio", "otavio@gmail.com");

        //ACT -> Criar a acao (Chama o DTO e automaticamente substitui pelo usuario mockado)
        when(this.repository.save(any(Usuario.class))).thenReturn(usuarioDB);
        UsuarioDTO usuarioSalvo = this.service.criar(usuarioDTO);

        //ASSERT -> verificar se a acao esta correta (Procurar se um usuario foi cadastrado)
        assertNotNull(usuarioSalvo.getId());
        assertEquals("Otavio", usuarioSalvo.getNome());
    }

    @Test
    void deveListarTodosOsUsuarios() {
        //ARRANGE -> organizar o cenario (Instaciar dois usuario)
        Usuario u1 = new Usuario(1L, "Otavio", "otavio@gmail.com");
        Usuario u2 = new Usuario(2L, "Ana", "ana@gmail.com");

        //ACT -> Criar a acao (listar todos usuarios)
        when(this.repository.findAll()).thenReturn(List.of(u1, u2));
        List<UsuarioDTO> usuarios = this.service.listarTodos();

        //ASSERT -> verificar se a acao esta correta (Procurar quantos usaurios estao cadastrados)
        assertEquals(2, usuarios.size());
        assertEquals("Otavio", usuarios.get(0).getNome());
        assertEquals("otavio@gmail.com", usuarios.get(0).getEmail());

        assertEquals("Ana", usuarios.get(1).getNome());
        assertEquals("ana@gmail.com", usuarios.get(1).getEmail());
    }
}
