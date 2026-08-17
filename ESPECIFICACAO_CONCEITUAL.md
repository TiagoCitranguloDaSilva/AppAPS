# Especificacao Conceitual do Projeto

## 1. Visao Geral

Este projeto consiste em um sistema conceitual de gestao de chamados e suporte tecnico de TI, pensado para execucao em ambiente Android com armazenamento local. O foco desta especificacao e apresentar a estrutura do sistema para fins academicos e de apresentacao, destacando modelagem orientada a objetos, organizacao arquitetural, entidades do dominio e relacoes de negocio.

O sistema foi planejado para controlar a abertura, acompanhamento, atendimento e encerramento de chamados tecnicos, considerando diferentes perfis de usuario, classificacao por categorias, niveis de prioridade, fluxo por status, historico de alteracoes e notificacoes.

## 2. Objetivo do Sistema

O objetivo principal e permitir o registro e gerenciamento de demandas de suporte tecnico de TI de forma organizada, rastreavel e expansivel.

O sistema deve possibilitar:

- cadastro e autenticacao de usuarios
- abertura de chamados tecnicos
- classificacao por categoria e prioridade
- acompanhamento do status do chamado
- atribuicao de tecnico responsavel
- registro de comentarios e historico
- preparacao para notificacoes por e-mail
- expansao futura para anexos, avaliacao e relatorios

## 3. Escopo da Apresentacao

Esta documentacao considera apenas o modelo conceitual do projeto. Portanto, o foco esta em:

- arquitetura conceitual
- entidades do sistema
- atributos e metodos principais
- heranca e associacoes
- regras de negocio em alto nivel
- fluxo funcional esperado

Nao faz parte deste documento:

- implementacao completa em Java
- codigo Android final
- detalhes de interface grafica
- integracao real com Gmail
- scripts de banco ou consultas SQL

## 4. Proposta de Arquitetura

Para apresentacao, a arquitetura proposta segue o padrao MVVM com persistencia local e separacao por responsabilidades.

### 4.1 Camadas conceituais

#### Camada de Apresentacao

Responsavel pelas telas e interacao com o usuario.

Exemplos:

- login
- abertura de chamado
- listagem de chamados
- detalhes do chamado
- gerenciamento administrativo

#### Camada de Controle de Estado

Responsavel por intermediar a interface e as regras do sistema.

Exemplos:

- controle de formularios
- atualizacao da lista de chamados
- validacao inicial de dados de tela

#### Camada de Regras de Negocio

Responsavel pelo comportamento do sistema.

Exemplos:

- abrir chamado
- alterar status
- atribuir tecnico
- registrar historico
- gerar notificacao

#### Camada de Persistencia

Responsavel pelo armazenamento local das informacoes.

Pensando em Android, esta camada pode futuramente ser mapeada para:

- Room
- SQLite

## 5. Estrutura Orientada a Objetos

O sistema foi pensado com uso de heranca para os perfis de usuario e composicao para os elementos ligados ao chamado.

### 5.1 Heranca principal

Classe abstrata base:

- `Usuario`

Classes derivadas:

- `Solicitante`
- `Tecnico`
- `Administrador`

### 5.2 Associacoes principais

- um solicitante pode abrir varios chamados
- um tecnico pode atender varios chamados
- um chamado pertence a uma categoria
- um chamado possui uma prioridade
- um chamado possui um status atual
- um chamado pode possuir comentarios
- um chamado pode possuir anexos
- um chamado gera registros de historico
- um chamado pode gerar notificacoes
- um chamado pode estar vinculado a um setor
- um chamado pode estar vinculado a um equipamento

## 6. Entidades do Projeto

O projeto foi estruturado com 15 entidades conceituais, permitindo um escopo robusto para apresentacao e expansao futura.

### 6.1 Usuario

Classe abstrata que representa qualquer pessoa autenticada no sistema.

#### Atributos

- id: Long
- nome: String
- email: String
- senhaHash: String
- telefone: String
- ativo: Boolean
- dataCriacao: LocalDateTime

#### Metodos

- login()
- logout()
- alterarSenha()
- atualizarDados()
- visualizarChamados()

### 6.2 Solicitante

Representa o usuario que registra o problema.

#### Herda de

- Usuario

#### Atributos

- setorId: Long
- matriculaOuRegistro: String

#### Metodos

- abrirChamado()
- editarChamadoAntesDaAnalise()
- acompanharChamado()
- avaliarAtendimento()

### 6.3 Tecnico

Representa o profissional responsavel pelo atendimento.

#### Herda de

- Usuario

#### Atributos

- especialidade: String
- nivelTecnico: String
- disponivel: Boolean

#### Metodos

- aceitarChamado()
- atualizarStatusChamado()
- registrarSolucao()
- adicionarComentarioTecnico()
- encerrarChamado()

### 6.4 Administrador

Representa o perfil com maior controle sobre o sistema.

#### Herda de

- Usuario

#### Atributos

- nivelAcesso: Int

#### Metodos

- cadastrarUsuario()
- desativarUsuario()
- criarCategoria()
- alterarPrioridadePadrao()
- gerarRelatorio()
- redistribuirChamado()

### 6.5 Chamado

Entidade central do sistema. Representa a solicitacao de suporte tecnico.

#### Atributos

- id: Long
- titulo: String
- descricao: String
- dataAbertura: LocalDateTime
- dataFechamento: LocalDateTime
- solicitanteId: Long
- tecnicoResponsavelId: Long
- categoriaId: Long
- prioridadeId: Long
- statusId: Long
- equipamentoId: Long
- setorId: Long
- solucaoFinal: String

#### Metodos

- abrir()
- atribuirTecnico()
- alterarStatus()
- alterarPrioridade()
- registrarSolucao()
- fechar()
- reabrir()
- calcularTempoAtendimento()

### 6.6 Categoria

Permite organizar os tipos de chamados.

#### Atributos

- id: Long
- nome: String
- descricao: String
- ativa: Boolean

#### Metodos

- ativar()
- desativar()
- editarDescricao()

#### Exemplos

- Internet
- Hardware
- Software
- Impressora
- Acesso a sistema

### 6.7 Prioridade

Padroniza o grau de urgencia do chamado.

#### Atributos

- id: Long
- nome: String
- tempoLimiteHoras: Int
- corIndicadora: String

#### Metodos

- alterarTempoLimite()
- definirCor()

#### Exemplos

- Baixa
- Media
- Alta
- Critica

### 6.8 StatusChamado

Controla o andamento do chamado no fluxo operacional.

#### Atributos

- id: Long
- nome: String
- descricao: String
- ordemFluxo: Int

#### Metodos

- podeIrPara(proximoStatus)

#### Exemplos

- Aberto
- Em analise
- Em atendimento
- Aguardando usuario
- Resolvido
- Encerrado
- Cancelado

### 6.9 Comentario

Armazena interacoes textuais sobre o chamado.

#### Atributos

- id: Long
- chamadoId: Long
- autorId: Long
- texto: String
- dataHora: LocalDateTime
- interno: Boolean

#### Metodos

- editarTexto()
- marcarComoInterno()

### 6.10 Anexo

Representa arquivos vinculados ao chamado.

#### Atributos

- id: Long
- chamadoId: Long
- nomeArquivo: String
- caminhoLocal: String
- tipoArquivo: String
- dataUpload: LocalDateTime

#### Metodos

- anexarArquivo()
- removerArquivo()
- abrirArquivo()

### 6.11 Notificacao

Representa o registro conceitual de avisos gerados pelo sistema.

#### Atributos

- id: Long
- usuarioDestinoId: Long
- chamadoId: Long
- mensagem: String
- tipo: String
- dataEnvio: LocalDateTime
- statusEnvio: String
- canal: String

#### Metodos

- gerarMensagem()
- marcarComoEnviada()
- marcarComoPendente()
- reenviar()

#### Canais futuros

- e-mail
- push notification
- mensageria corporativa

### 6.12 HistoricoChamado

Permite auditoria e rastreabilidade das acoes realizadas no chamado.

#### Atributos

- id: Long
- chamadoId: Long
- usuarioId: Long
- acao: String
- valorAnterior: String
- valorNovo: String
- dataHora: LocalDateTime

#### Metodos

- registrarAlteracao()

### 6.13 Setor

Representa a area ou departamento vinculado ao solicitante e ao chamado.

#### Atributos

- id: Long
- nome: String
- localizacao: String
- responsavel: String

#### Metodos

- atualizarDados()

#### Exemplos

- Financeiro
- RH
- Secretaria
- Laboratorio

### 6.14 Equipamento

Representa o recurso de TI possivelmente afetado.

#### Atributos

- id: Long
- patrimonio: String
- nomeEquipamento: String
- tipo: String
- marca: String
- modelo: String
- numeroSerie: String
- setorId: Long

#### Metodos

- atualizarLocalizacao()
- registrarManutencao()

### 6.15 AvaliacaoAtendimento

Permite medir a satisfacao do usuario ao final do atendimento.

#### Atributos

- id: Long
- chamadoId: Long
- nota: Int
- comentario: String
- dataAvaliacao: LocalDateTime

#### Metodos

- registrarNota()

## 7. Relacionamentos Entre Entidades

### 7.1 Relacoes principais

- um `Solicitante` abre muitos `Chamados`
- um `Tecnico` atende muitos `Chamados`
- um `Chamado` pertence a uma `Categoria`
- um `Chamado` possui uma `Prioridade`
- um `Chamado` possui um `StatusChamado`
- um `Chamado` pode conter muitos `Comentarios`
- um `Chamado` pode conter muitos `Anexos`
- um `Chamado` pode gerar muitos registros em `HistoricoChamado`
- um `Chamado` pode gerar varias `Notificacoes`
- um `Solicitante` pertence a um `Setor`
- um `Chamado` pode estar vinculado a um `Setor`
- um `Chamado` pode estar vinculado a um `Equipamento`
- um `Chamado` pode receber uma `AvaliacaoAtendimento`

### 7.2 Visao hierarquica simplificada

```text
Usuario
├── Solicitante
├── Tecnico
└── Administrador

Chamado
├── Categoria
├── Prioridade
├── StatusChamado
├── Comentario
├── Anexo
├── HistoricoChamado
├── Notificacao
├── Setor
├── Equipamento
└── AvaliacaoAtendimento
```

## 8. Regras de Negocio Conceituais

Para a apresentacao do projeto, as principais regras de negocio previstas sao:

- somente usuarios autenticados podem acessar o sistema
- somente solicitantes podem abrir chamados
- todo chamado deve possuir categoria, prioridade e status inicial
- o status inicial padrao do chamado e `Aberto`
- um tecnico pode assumir um chamado para atendimento
- alteracoes relevantes devem gerar registros no historico
- o chamado pode evoluir por etapas de status predefinidas
- ao ser encerrado, o chamado pode armazenar uma solucao final
- o solicitante pode acompanhar o andamento do seu chamado
- o sistema deve estar preparado para gerar notificacoes automaticas

## 9. Fluxo Conceitual do Sistema

### 9.1 Fluxo principal

1. O solicitante realiza login no sistema.
2. O solicitante abre um novo chamado.
3. O sistema registra categoria, prioridade e status inicial.
4. O sistema salva a data de abertura.
5. O sistema registra a acao no historico.
6. O tecnico visualiza e assume o chamado.
7. O tecnico atualiza o status e adiciona comentarios.
8. O tecnico registra a solucao.
9. O chamado e encerrado.
10. O solicitante pode avaliar o atendimento.

### 9.2 Fluxo administrativo

1. O administrador cadastra usuarios.
2. O administrador gerencia categorias e prioridades.
3. O administrador acompanha indicadores e relatorios.
4. O administrador redistribui chamados quando necessario.

## 10. Justificativa da Arquitetura Escolhida

A arquitetura proposta foi escolhida por equilibrar organizacao, clareza e possibilidade de crescimento futuro.

### Pontos fortes da proposta

- separacao entre interface, logica e dados
- boa aderencia a projetos Android
- facilidade de manutencao
- modelagem orientada a objetos coerente com o dominio
- preparo para armazenamento local com Room
- facilidade de futura expansao para servicos externos

## 11. Estrutura Conceitual de Pacotes

Uma organizacao conceitual recomendada para apresentacao do projeto seria:

```text
model/
  Usuario
  Solicitante
  Tecnico
  Administrador
  Chamado
  Categoria
  Prioridade
  StatusChamado
  Comentario
  Anexo
  HistoricoChamado
  Notificacao
  Setor
  Equipamento
  AvaliacaoAtendimento

service/
  ChamadoService
  NotificacaoService
  RelatorioService
  AutenticacaoService

data/
  dao/
  database/
  repository/

ui/
  login/
  chamado/
  admin/
  usuario/
```

## 12. Escopo Minimo Para Apresentacao

Caso seja necessario apresentar uma versao mais enxuta do modelo conceitual, as 10 entidades principais sao:

1. Usuario
2. Solicitante
3. Tecnico
4. Administrador
5. Chamado
6. Categoria
7. Prioridade
8. StatusChamado
9. Comentario
10. HistoricoChamado

As entidades a seguir podem ser apresentadas como expansao futura:

1. Anexo
2. Notificacao
3. Setor
4. Equipamento
5. AvaliacaoAtendimento

## 13. Fases de Evolucao do Projeto

Mesmo sendo um modelo conceitual, a proposta permite apresentar um plano de crescimento gradual.

### Fase 1

- cadastro local de usuarios
- autenticacao
- abertura e consulta de chamados

### Fase 2

- atribuicao de tecnico
- comentarios
- historico do chamado

### Fase 3

- anexos
- avaliacao de atendimento
- relatorios

### Fase 4

- notificacoes por e-mail
- integracao externa
- sincronizacao online

## 14. Resumo Executivo

O sistema proposto e um gestor conceitual de chamados de suporte tecnico de TI, estruturado com base em orientacao a objetos e organizado para futura implementacao em Android com Java. A modelagem utiliza heranca para especializacao de usuarios e associacoes para os elementos relacionados ao chamado. A arquitetura conceitual segue a separacao entre interface, regras de negocio e persistencia, favorecendo manutencao, clareza e expansao futura. O projeto atende ao contexto de abertura, acompanhamento e resolucao de chamados, com potencial evolutivo para anexos, notificacoes, relatorios e integracoes externas.

