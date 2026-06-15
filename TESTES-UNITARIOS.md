# Guia de Testes Unitários para o Projeto CRUD Testes

Este documento descreve o passo a passo para configurar e ensinar testes unitários no projeto `crud-testes`.

## 1. Pré-requisitos

- Java 21 instalado
- Maven instalado ou uso do wrapper `./mvnw`
- IDE ou editor de código como VS Code ou IntelliJ
- Projeto aberto em `/Users/wesleybruno/Downloads/crud-testes - cópia`

## 2. Estrutura atual do projeto

Arquivos principais:
- `src/main/java/com/escola/crud/controllers/UsuarioController.java`
- `src/main/java/com/escola/crud/services/UsuarioService.java`
- `src/main/java/com/escola/crud/repositories/UsuarioRepository.java`
- `src/main/java/com/escola/crud/entities/Usuario.java`
- `src/main/java/com/escola/crud/dtos/UsuarioDTO.java`
- `src/test/java/com/escola/crud/CrudTestesApplicationTests.java`

O foco inicial para testes unitários é a camada de serviço (`UsuarioService`) e, em seguida, a camada de controller (`UsuarioController`).

## 3. Dependências de teste necessárias

No `pom.xml`, adicione ou verifique a presença das seguintes dependências de teste:

- `org.springframework.boot:spring-boot-starter-test`
  - inclui JUnit 5, Spring Test, MockMvc, AssertJ, Hamcrest e Mockito básicos.
  - fornece a infraestrutura para escrever testes de unidade e testes de camada Spring.
- `org.mockito:mockito-junit-jupiter`
  - integra o Mockito ao JUnit 5.
  - permite usar `@ExtendWith(MockitoExtension.class)`, `@Mock` e `@InjectMocks`.
- `org.assertj:assertj-core`
  - fornece asserções fluentas e mais legíveis do que asserções JUnit padrão.
  - útil para verificar objetos, coleções e mensagens de erro com clareza.

Observação: o `spring-boot-starter-test` já traz o Mockito e o AssertJ, mas explicitar as dependências ajuda na aula para mostrar o que cada biblioteca faz.

## 4. Como testar cada camada

### 4.1 Testar `UsuarioService`

Esta camada é a melhor para começar porque é lógica de negócio pura e pode ser isolada com mocks.

Use:
- `@ExtendWith(MockitoExtension.class)`
- `@Mock` para `UsuarioRepository`
- `@InjectMocks` para `UsuarioService`

Casos recomendados:
1. `criar()` deve criar usuário quando o email não existe
2. `criar()` deve lançar `IllegalArgumentException` quando email já cadastrado
3. `listarTodos()` deve retornar a lista de usuários mapeados para DTO
4. `buscarPorId()` deve retornar o usuário quando existir
5. `buscarPorId()` deve lançar erro quando o id não existir
6. `deletar()` deve remover o usuário quando o id existir
7. `deletar()` deve lançar erro quando o id não existir

Use `when(...).thenReturn(...)` para simular `repository` e `assertThatThrownBy(...)` para verificar exceções.

### 4.2 Testar `UsuarioController`

O controller é a camada de API. Os testes devem focar em roteamento, status HTTP e conversão JSON.

Use:
- `@WebMvcTest(UsuarioController.class)`
- `@MockBean` para `UsuarioService`
- `MockMvc` para simular requisições HTTP e receber respostas

Casos sugeridos:
- POST `/api/usuarios` deve retornar `201 CREATED`
- GET `/api/usuarios` deve retornar lista de usuários
- GET `/api/usuarios/{id}` deve retornar o usuário correto
- DELETE `/api/usuarios/{id}` deve retornar `204 NO_CONTENT`
- validar que a controller lida com as falhas do service corretamente

### 4.3 Testar `UsuarioRepository`

O repositório é a camada de persistência. O objetivo é testar consultas JPA e comportamento do banco em memória.

Use:
- `@DataJpaTest` para inicializar apenas a camada JPA
- um banco de dados em memória H2 automático

Casos sugeridos:
- salvar um usuário e verificar que o `id` é gerado
- buscar por email com `findByEmail()` e garantir que retorna o usuário correto
- buscar por `findById()` e validar retorno `Optional` correto
- verificar `existsById()` e `deleteById()` se quiser demonstrar exclusão

### 4.4 Testar `UsuarioDTO`

DTOs são objetos simples de transporte de dados. Os testes aqui são pequenos, mas importantes para garantir que construtores, getters e setters funcionam e que o mapeamento está correto.

Use um teste simples para:
- criar um `UsuarioDTO` com valores
- verificar que `getId()`, `getNome()` e `getEmail()` retornam os valores esperados
- se quiser, testar `equals()` e `hashCode()` se a classe usar Lombok para isso

## 5. Criar os arquivos de teste

Crie ou atualize os seguintes arquivos:
- `src/test/java/com/escola/crud/services/UsuarioServiceTest.java`
- `src/test/java/com/escola/crud/controllers/UsuarioControllerTest.java`
- `src/test/java/com/escola/crud/repositories/UsuarioRepositoryTest.java`
- `src/test/java/com/escola/crud/dtos/UsuarioDTOTest.java`

A seguir há sugestões de conteúdo para cada um.

### 5.1 Exemplo de `UsuarioServiceTest`

```java
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {
    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private UsuarioService service;

    @Test
    void deveCriarUsuarioQuandoEmailNaoExiste() {
        when(repository.findByEmail("maria@escola.com")).thenReturn(Optional.empty());
        when(repository.save(any(Usuario.class)))
                .thenReturn(new Usuario(1L, "Maria", "maria@escola.com"));

        UsuarioDTO resultado = service.criar(new UsuarioDTO(null, "Maria", "maria@escola.com"));

        assertThat(resultado.getId()).isEqualTo(1L);
    }
}
```

### 5.2 Exemplo de `UsuarioControllerTest`

```java
@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService service;

    @Test
    void deveCriarUsuarioRetornar201() throws Exception {
        when(service.criar(any(UsuarioDTO.class)))
                .thenReturn(new UsuarioDTO(1L, "Maria", "maria@escola.com"));

        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Maria\",\"email\":\"maria@escola.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }
}
```

### 5.3 Exemplo de `UsuarioRepositoryTest`

```java
@DataJpaTest
class UsuarioRepositoryTest {
    @Autowired
    private UsuarioRepository repository;

    @Test
    void deveEncontrarUsuarioPorEmail() {
        Usuario usuario = repository.save(new Usuario(null, "Maria", "maria@escola.com"));

        Optional<Usuario> resultado = repository.findByEmail("maria@escola.com");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(usuario.getId());
    }
}
```

### 5.4 Exemplo de `UsuarioDTOTest`

```java
class UsuarioDTOTest {
    @Test
    void deveTerGettersESettersCorretos() {
        UsuarioDTO dto = new UsuarioDTO(1L, "Maria", "maria@escola.com");

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getNome()).isEqualTo("Maria");
        assertThat(dto.getEmail()).isEqualTo("maria@escola.com");
    }
}
```

## 6. Executar os testes

Use Maven para rodar todos os testes:

```bash
./mvnw test
```

ou:

```bash
mvn test
```

Verifique se:
- todos os testes passam
- há cobertura para fluxos de sucesso e erro
- o repositório funciona no banco H2 em memória

## 7. Como usar na aula

"`Procure sempre falar sobre:
- o que cada dependência de teste faz
- quando usar mocks e quando testar com banco em memória
- porque `UsuarioService` não deve acessar a internet ou o banco real em testes unitários
- a diferença entre `@MockBean` e `@Mock`
- o papel do DTO como contrato de dados

## 8. Sequência de ensino recomendada

1. Escrever primeiro os testes de `UsuarioService`
2. Depois, fazer os testes de `UsuarioDTO`
3. Em seguida, os testes de `UsuarioRepository`
4. Por fim, os testes de `UsuarioController`

## 9. Resultado esperado

O projeto ficará com uma base clara de testes unitários que demonstra:
- isolamento de dependências
- validação de resultados e erros
- uso de mocks e asserções legíveis
- boas práticas para iniciar uma disciplina sobre testes unitários

Crie o arquivo:
- `src/test/java/com/escola/crud/services/UsuarioServiceTest.java`

Casos recomendados:
1. `criar()` deve criar usuário quando o email não existe
2. `criar()` deve lançar `IllegalArgumentException` quando email já cadastrado
3. `listarTodos()` deve retornar a lista de usuários mapeados para DTO
4. `buscarPorId()` deve retornar o usuário quando existir
5. `buscarPorId()` deve lançar erro quando o id não existir
6. `deletar()` deve remover o usuário quando o id existir
7. `deletar()` deve lançar erro quando o id não existir

Princípios didáticos:
- use `@ExtendWith(MockitoExtension.class)`
- use `@Mock` para `UsuarioRepository`
- use `@InjectMocks` para `UsuarioService`
- simule comportamentos com `when(...).thenReturn(...)`
- verifique exceções com `assertThatThrownBy(...)`
- confirme chamadas com `verify(...)`

## 5. Criar testes para `UsuarioController`

Crie testes de controller usando `MockMvc`:
- `@WebMvcTest(UsuarioController.class)`
- `@MockBean` para `UsuarioService`
- `MockMvc` para simular requisições HTTP

Casos sugeridos:
- POST `/api/usuarios` deve retornar `201 CREATED`
- GET `/api/usuarios` deve retornar lista de usuários
- GET `/api/usuarios/{id}` deve retornar o usuário correto
- DELETE `/api/usuarios/{id}` deve retornar `204 NO_CONTENT`
- validar que a controller lida com as falhas do service corretamente

## 6. Executar os testes

Use Maven para rodar os testes:

```bash
./mvnw test
```

ou, se preferir:

```bash
mvn test
```

Verifique se:
- todos os testes passam
- há cobertura para fluxos de sucesso e erro
- não há dependências faltando

## 7. Como usar na aula

Sugestões de didática:
- explique a diferença entre teste unitário e teste de integração
- mostre por que `UsuarioService` é ideal para testes isolados
- demonstre como mockar `UsuarioRepository`
- discuta a importância de testar casos de sucesso e de erro
- use os testes como documentação do comportamento esperado

## 8. Sequência de ensino recomendada

1. Escrever primeiro os testes de `UsuarioService`
2. Em seguida, escrever os testes de `UsuarioController`
3. Pedir aos alunos para adicionar casos de erro (email duplicado, id inexistente)
4. Mostrar os efeitos das mudanças no código de produção

## 9. Resultado esperado

O projeto ficará com uma base clara de testes unitários que demonstra:
- isolamento de dependências
- validação de resultados e erros
- uso de mocks e asserções legíveis
- boas práticas para iniciar uma disciplina sobre testes unitários
