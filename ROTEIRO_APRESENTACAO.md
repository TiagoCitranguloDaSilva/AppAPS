# Roteiro de Apresentacao

## 1. Introducao

Este projeto e um prototipo de sistema de chamados para suporte tecnico de TI, desenvolvido em Java no Android Studio.

O objetivo principal e permitir que um usuario:

- faca login
- abra um chamado
- acompanhe chamados cadastrados
- visualize detalhes
- adicione comentarios
- acompanhe o historico
- altere o status do atendimento

## 2. Objetivo Academico

O projeto foi pensado como primeiro contato com desenvolvimento Android em Java, por isso a interface foi mantida simples e a implementacao priorizou:

- organizacao em classes
- separacao de responsabilidades
- fluxo funcional basico
- persistencia local no aparelho

## 3. Estrutura do Projeto

O projeto foi dividido em:

- `model`: classes de dominio
- `data/repository`: persistencia local e acesso aos dados
- `ui`: apoio de formatacao para exibicao
- `activities`: telas principais do aplicativo

## 4. Persistencia Local

Os dados nao sao armazenados em banco de dados.

Para simplificar o projeto, as informacoes sao salvas localmente no aparelho usando:

- `SharedPreferences`
- serializacao em JSON

Dessa forma, os chamados continuam salvos mesmo apos fechar o aplicativo.

## 5. Fluxo de Demonstracao

### 5.1 Login

Ao abrir o app, o usuario encontra a tela de login.

Credenciais demo:

- `ana@quickcall.local / 123456`
- `carlos@quickcall.local / 123456`

### 5.2 Tela principal

Depois do login, o sistema mostra:

- usuario logado
- quantidade de usuarios locais
- quantidade de chamados salvos
- lista de chamados cadastrados

### 5.3 Novo chamado

Na tela de novo chamado, o usuario pode:

- informar titulo
- informar descricao
- escolher categoria
- escolher prioridade
- salvar o chamado

### 5.4 Detalhe do chamado

Ao tocar em um chamado da lista, o sistema mostra:

- titulo
- descricao
- categoria
- prioridade
- status
- data de abertura
- data de fechamento

Tambem permite:

- alterar status
- adicionar comentario
- visualizar comentarios
- visualizar historico

## 6. Regras Simples Aplicadas

- o app cria dados demo automaticamente na primeira execucao
- o usuario logado pode abrir chamados
- alteracoes de status geram historico
- comentarios tambem geram historico
- ao encerrar um chamado, a data de fechamento e registrada

## 7. Classes Principais

### Entidades

- `Usuario`
- `Chamado`
- `Categoria`
- `Prioridade`
- `StatusChamado`
- `Comentario`
- `HistoricoChamado`

### Repositorios

- `UsuarioRepository`
- `ChamadoRepository`
- `ComentarioRepository`
- `HistoricoRepository`
- `CatalogoRepository`

### Apoio

- `JsonStorageHelper`
- `JsonMapper`
- `SessionManager`
- `DemoDataSeeder`

## 8. Pontos Fortes Para Destacar

- projeto orientado a objetos
- armazenamento local no aparelho
- fluxo funcional completo basico
- separacao entre modelo, persistencia e interface
- uso de Java no Android Studio

## 9. Limitacoes Atuais

Por ser um prototipo academico inicial, o sistema ainda nao possui:

- banco de dados formal
- notificacoes reais por e-mail
- anexos
- controle avancado de perfis
- design sofisticado

## 10. Melhorias Futuras

Como proximos passos, o projeto poderia evoluir para:

- uso de Room ou SQLite
- tela de cadastro de usuarios
- filtros por status e prioridade
- notificacoes
- anexos
- relatorios

## 11. Fechamento

Este projeto demonstra a construcao de um aplicativo Android simples, funcional e organizado, com foco em praticar conceitos de Java, orientacao a objetos, navegacao entre telas e armazenamento local.

