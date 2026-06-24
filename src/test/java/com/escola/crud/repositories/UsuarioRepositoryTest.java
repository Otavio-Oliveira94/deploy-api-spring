package com.escola.crud.repositories;

import com.escola.crud.entities.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UsuarioRepositoryTest {
    //TESTE DE REPOSITORIO NAO EH NECESSARIO MOCKAR DADOS, APENAS EM OUTRAS CLASSES
    private final UsuarioRepository repository;

    @Autowired
    public UsuarioRepositoryTest(UsuarioRepository repository) {
        this.repository = repository;
    }

    //TESTAR SE EH POSSIVEL SALVAR UM USUARIO NO BANCO DE DADOS
    @Test
    void deveSalvarUsuarioNoBancoComSucesso() {
        //ARRANGE -> organizar o cenario (Instaciar um usuario)
        Usuario usuario = new Usuario(null, "Otavio", "otavio@gmail.com");

        //ACT -> Criar a acao (Salvar um usuario)
        Usuario usuarioSalvo = this.repository.save(usuario);

        //ASSERT -> verificar se a acao esta correta (Procurar se um usuario foi cadastrado)
        assertNotNull(usuarioSalvo.getId());
    }

    //TESTAR SE EH POSSIVEL BUSCAR UM USUARIO POR ID
    @Test
    void deveSalvarUsuarioPorId() {
        //ARRANGE -> organizar o cenario (SALVAR UM USUARIO)
        Usuario usuario = new Usuario(null, "Otavio", "otavio@gmail.com");
        Usuario usuarioSalvo = this.repository.save(usuario);

        //ACT -> Criar a acao (Busca o usuario por ID)
        Optional<Usuario> usuarioRetornado = this.repository.findById(usuarioSalvo.getId());

        //ASSERT -> verificar se a acao esta correta (verificar se o usuario existe)
        assertTrue(usuarioRetornado.isPresent());
        assertEquals("Otavio", usuarioRetornado.get().getNome());
    }

    //TESTAR SE EH POSSIVEL DELETAR UM USUARIO
    @Test
    void deveDeletarUmUsuario() {
        //ARRANGE -> organizar o cenario (SALVAR UM USUARIO)
        Usuario usuario = new Usuario(null, "Otavio", "otavio@gmail.com");
        Usuario usuarioSalvo = this.repository.save(usuario);

        //ACT -> Criar a acao (APAGAR usuario criado)
        this.repository.deleteById(usuarioSalvo.getId());

        //ASSERT -> verificar se a acao esta correta (verificar se o usuario foi deletado)
        Optional<Usuario> usuarioEncontrado = this.repository.findById(usuarioSalvo.getId());
        assertFalse(usuarioEncontrado.isPresent());
    }
}
