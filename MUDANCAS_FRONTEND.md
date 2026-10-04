# O que mudou no app (front-end)

Oi, pessoal! Aqui eu explico tudo o que mexi no app Android e por quê. Os arquivos originais, do jeito que estavam antes, ficaram guardados na pasta `backup/`, caso alguém queira comparar ou voltar atrás.

---

## Dia 1 — Fazendo o login funcionar com o servidor de verdade

### O problema

Quando liguei o back-end do Tiago e tentei entrar no app, não funcionava de jeito nenhum. Fui investigar e descobri que o app e o servidor estavam "falando línguas diferentes".

O servidor funciona assim: primeiro você faz login em `/auth/login` mandando email e senha. Se estiver certo, ele te devolve um **token**, que é tipo um crachá. Depois disso, em todo pedido que o app faz (listar chamados, criar chamado etc.), ele precisa mostrar esse crachá. Se não mostrar, o servidor recusa e responde com erro 403 (acesso negado).

O app não sabia nada disso. Ele tentava fazer login em `/login` (que não existe) e nunca mandava o crachá.

### O que eu fiz

No **HttpJsonClient** (o arquivo que faz todas as conversas com o servidor), coloquei um lugar para guardar o token. A partir de agora, todo pedido que sai do app leva junto o cabeçalho `Authorization: Bearer <token>`. É assim que o servidor sabe que a gente está logado.

No **LoginApiService**, reescrevi o login. Agora ele faz três coisas em sequência: manda email e senha para `/auth/login`, guarda o token que voltou e, por último, busca o nome, o id e o perfil da pessoa em `/usuario/email/{email}`. Esse último passo foi necessário porque o login do servidor só devolve o token, sem nenhum dado do usuário.

No **UsuarioApiService**, troquei os endereços de `/usuarios` para `/usuario`, porque no servidor eles estão no singular. Também fiz o app aceitar os nomes de perfil do banco (ADMIN, TÉCNICO etc.) sem travar. Antes, se viesse um nome diferente do esperado, o app simplesmente fechava.

No **SessionManager**, que é quem lembra quem está logado, adicionei o token. E quando a pessoa clica em Sair, o token é apagado.

Na **LoginActivity** (a tela de login), o app agora pede o login toda vez que abre. Fiz isso porque o token expira depois de algumas horas, e se o app tentasse entrar sozinho com um token velho, ia dar erro. Também coloquei na tela um lembrete com o usuário de teste e deixei a mensagem de erro mais clara.

Na **MainActivity**, o botão Sair agora também apaga o token.

### Um bug que já estava no projeto

O app nem compilava: dava erro dizendo que não achava o `button_excluir_chamado`. O código Java da tela de detalhe usava esse botão, mas ele nunca tinha sido desenhado no XML da tela (`activity_chamado_detalhe.xml`). Eu adicionei o botão "Excluir chamado" no XML e o erro sumiu. Conferi o projeto inteiro e era o único caso assim.

---

## Dia 2 — Lista de chamados mostrando as informações certas

### O problema

Depois do login, a lista de chamados aparecia, mas meio bagunçada: o texto vinha repetido duas vezes, a prioridade aparecia como "-" e os status não batiam com o banco.

O motivo é que o app esperava os dados num formato antigo. Por exemplo, ele esperava o status como um texto simples, mas o servidor manda assim: `"status": {"id": 2, "nome": "Em Atendimento"}`, um "pacotinho" com id e nome. O mesmo acontece com categoria e prioridade.

### O que eu fiz

No **ChamadoApiService**, reescrevi a parte que transforma a resposta do servidor em um chamado dentro do app. Agora ele lê o status, a categoria e a prioridade do jeito que o servidor manda de verdade.

Sobre o texto repetido: o servidor ainda não devolve o título do chamado, só a descrição. Então, por enquanto, o app usa a descrição como título. Quando o servidor passar a mandar o título, o app já vai mostrar sozinho, sem precisar mudar nada.

Aproveitei e já deixei pronta a parte de **criar e editar chamado**. Agora o app manda os campos com os nomes que o servidor espera (`titulo`, `descricao`, `idPrioridade`, `idStatus`, `idCategoria`, `idUsuario`).

No **CatalogoRepository**, as listas de status e de prioridade agora vêm do servidor. Antes elas estavam escritas direto no código do app, com nomes diferentes dos do banco (o app tinha "Concluído", o banco tem "Resolvido", por exemplo). Se o servidor não responder, o app usa uma lista reserva para não quebrar.

Um detalhe: o servidor manda as prioridades só com o nome, sem número de id. Então o app usa a posição na lista: Baixa é 1, Média é 2, Alta é 3 e Urgente é 4, que é a mesma ordem do banco.

Na **MainActivity**, mudei como cada chamado aparece na lista. Agora fica assim:

```
#1  O Wi-Fi do setor de vendas desconecta a cada 10 minutos.
Em Atendimento | Alta | Redes | 28/09/2026 09:00
```

E deixei a tela mais rápida. Antes, o app perguntava ao servidor a lista de status e prioridades **uma vez para cada chamado** da lista. Com 50 chamados, seriam 100 perguntas. Agora ele pergunta uma vez só e reaproveita.

---

## Dia 3 — Criar chamado e botão Voltar

### Criar chamado

A tela de novo chamado já funcionou sem mexer em nada, porque no Dia 2 eu tinha deixado o app mandando os dados no formato que o servidor espera, e as listas de categoria e prioridade passaram a vir do servidor.

### O problema do Voltar

Testando, percebi que não dava para sair da tela de novo chamado (nem da tela de detalhe) sem salvar. O tema do app não tem aquela barra de cima com a setinha de voltar (ele usa `NoActionBar`), então a única saída era o botão de voltar do próprio celular, que nem todo mundo lembra de usar.

### O que eu fiz

Coloquei um botão **Voltar** no topo das duas telas (`activity_novo_chamado.xml` e `activity_chamado_detalhe.xml`). No Java (`NovoChamadoActivity` e `ChamadoDetalheActivity`), o botão só chama `finish()`, que fecha a tela atual e volta para a lista. Como a lista recarrega sozinha quando volta a aparecer, qualquer chamado novo já aparece lá.

Os arquivos originais ficaram em `backup/antes-dia3/`.

---

## Dia 4 — Tela de detalhe: status, comentários e histórico

### O problema

A tela de detalhe (a que abre quando você toca num chamado) ainda estava no "jeito antigo". Ela buscava comentários em `/comentarios` e histórico em `/historicos`, que não existem no servidor. Além disso, o app tentava gravar o histórico por conta própria, mas quem faz isso no nosso sistema é o servidor.

### O que eu fiz

No **ComentarioApiService**, troquei os endereços para `/comentario/chamado/{id}` (listar) e `/comentario` (criar). Também mudei os nomes dos campos: o servidor chama o texto de `mensagem` e o autor de `usuarioId`. Antes o app mandava `texto` e `autorId`, e o servidor não entendia.

No **HistoricoApiService**, o histórico agora vem de `/relatorio/chamado/{id}`. Cada item mostra o que mudou, o valor antigo e o novo. Na tela fica assim, por exemplo: `02/10/2026 01:30 - STATUS: Aberto -> Resolvido`. Tirei a parte que tentava gravar histórico pelo app, já que o servidor faz isso sozinho quando o chamado muda.

Na **ChamadoDetalheActivity**:

- Mudar o status agora reconhece os nomes reais do banco. O app procurava "Concluído", mas no banco o status final se chama "Resolvido" ou "Fechado".
- Quando a pessoa comenta, aparece uma mensagem dizendo se deu certo ou não. Antes, se desse erro, não aparecia nada.
- Se a descrição for igual ao título, ela não aparece duas vezes na tela.
- O botão **Excluir chamado** só deveria aparecer para técnico e administrador. Como o servidor ainda não manda o perfil (vem vazio), deixei o botão visível nesse caso. Assim que o perfil vier certo, a regra volta a funcionar sozinha.

Os arquivos originais ficaram em `backup/antes-dia4/`.

### Um cuidado

Quando o app muda o status, ele manda o chamado inteiro de volta para o servidor (`PUT /chamado`), incluindo o título. Como o servidor ainda não devolve o título, o app manda a descrição no lugar. Ou seja, depois de mudar o status, o título salvo no banco vira a descrição. Isso se resolve quando o `GET /chamado` passar a devolver o `titulo`.

### Mudar status: o servidor exige um técnico

Testando, o status não salvava, mas o app dizia "Status atualizado". Fui ver o que o servidor respondia e era isto: **"O id do técnico é obrigatório"**. Ou seja, para mudar o status, o chamado precisa ter um técnico responsável, o que faz sentido num sistema de chamados.

Então coloquei na tela de detalhe um campo **Técnico responsável** (um `Spinner`, aquela listinha que abre). Ele mostra os técnicos que vêm de `GET /tecnico`, com as categorias que cada um atende, tipo "Tecnico1 (Redes, Hardware)". O app já deixa selecionado um técnico que atende a categoria do chamado. Na hora de salvar o status, o id do técnico vai junto.

Arquivos novos: `model/Tecnico.java` (a "ficha" do técnico) e `data/service/TecnicoApiService.java` (que busca a lista no servidor). Detalhe: a API não devolve o id do técnico, mas `GET /tecnico/1` é o primeiro da lista, `/tecnico/2` o segundo, então o app usa a posição como id (mesmo truque das prioridades).

### O app agora entende quando o servidor diz "não"

Esse foi um bug que afetava o app inteiro. O **HttpJsonClient** tratava qualquer resposta como sucesso, até as de erro. Se o servidor respondia "400 - O id do técnico é obrigatório", o app achava que tinha dado certo.

Agora, quando o servidor responde com erro (400, 403, 500...), o app trata como falha e guarda a mensagem que o servidor mandou. Na tela de detalhe, por exemplo, aparece: "Não foi possível atualizar o status: O id do técnico é obrigatório". Fica muito mais fácil descobrir o que deu errado.

---

## Dia 5 — Histórico aparecendo na tela

### O problema

O histórico já estava sendo buscado no servidor, mas não dava para ver. A tela de detalhe inteira rola (ela é um `ScrollView`), e dentro dela os comentários e o histórico eram `ListView`, que é uma lista com rolagem própria. Uma rolagem dentro da outra deixa a tela travada: o dedo rola a listinha e não a página, e o histórico ficava escondido lá embaixo, sem como chegar.

### O que eu fiz

No XML da tela (`activity_chamado_detalhe.xml`), troquei os dois `ListView` por `LinearLayout`, que é uma caixa simples que cresce conforme o conteúdo. Mantive os mesmos ids (`list_comentarios` e `list_historico`).

Na **ChamadoDetalheActivity**, criei o método `preencherLista()`, que pega cada linha (cada comentário ou cada item do histórico) e coloca um texto embaixo do outro dentro da caixa. Agora a página inteira rola de uma vez só e dá para ver tudo até o final.

Os arquivos originais ficaram em `backup/antes-dia5/`.

### Deixando o histórico legível

Quando o histórico apareceu, as mudanças de status vinham com números, tipo `status: 1 -> 2`, porque é assim que o servidor grava. Fiz o app trocar o número pelo nome do status (`status: Aberto -> Em Atendimento`), usando a mesma lista de status que já vem do servidor. Também tirei a setinha vazia da linha de "Criação", que vem sem valor antigo e sem valor novo.

O histórico também mostrou, na prática, o problema do título: aparece uma linha `titulo: TA PEGANDO FOGO -> FOGO`. O título original era "TA PEGANDO FOGO", mas como o servidor não devolve o título, o app reenviou a descrição ("FOGO") no lugar. Está no item 5 da lista do Tiago.

---

## Dia 6 — Técnico do jeito da especificação (herança)

### Por que

Conversei com o Tiago e decidimos seguir a especificação (`ESPECIFICACAO_CONCEITUAL.md`, item 6.3): o **Tecnico herda de Usuario**. No Dia 4 eu tinha criado um `Tecnico` simples, só para o status funcionar, sem herança. Agora ficou do jeito que o projeto pede.

### O que é herança, em uma frase

`Tecnico extends Usuario` quer dizer que **todo técnico é um usuário**. Ele já ganha tudo o que o usuário tem (id, nome, email, telefone, login...) e só acrescenta o que é dele.

### O que eu fiz

No **Tecnico.java**: agora é `public class Tecnico extends Usuario`. Tirei os campos repetidos (id, nome, telefone), porque eles vêm do Usuario. Ficaram só os campos que são de técnico, como na especificação: `especialidade`, `nivelTecnico` e `disponivel`, além das categorias que ele atende. Quando um técnico é criado, ele já recebe o perfil `TECNICO` automaticamente.

Também coloquei os métodos que a especificação lista para o técnico: `aceitarChamado()`, `atualizarStatusChamado()`, `registrarSolucao()`, `adicionarComentarioTecnico()` e `encerrarChamado()`.

No **Usuario.java**: tirei `especialidade`, `nivelTecnico` e `disponivel`, porque são coisas só de técnico e agora moram na classe certa. Também tirei esses campos do construtor grande e das conversões de JSON (`JsonMapper` e `UsuarioApiService`). O servidor não manda esses dados para usuário comum (vinham sempre vazios), então nada foi perdido.

Na **ChamadoDetalheActivity**: quando você salva o status, quem faz o trabalho agora é o próprio técnico escolhido, com `tecnico.aceitarChamado(chamado)` e `tecnico.atualizarStatusChamado(chamado, novoStatus)`. Ou seja, os métodos da especificação estão sendo usados de verdade, não só existindo no código.

### O que ficou de fora (de propósito)

A especificação também diz que o `Usuario` é uma classe **abstrata** e que existem `Solicitante` e `Administrador`. Não fiz essa parte agora: o app cria um `Usuario` comum na hora do login (porque o servidor não diz o perfil), e deixar a classe abstrata quebraria isso. Dá para fazer depois, se o grupo quiser.

Os arquivos originais ficaram em `backup/antes-dia6/`.

### O erro 403 na primeira vez que muda o status

Testando, a primeira mudança de status de um chamado novo dava "Erro 403" e a segunda, igualzinha, funcionava. Fui olhar os logs do servidor e o motivo é um bug no back-end: quando o chamado ainda não tem técnico, o `RelatorioChamadoService.detectarMudancas()` (linha 46) faz `chamado.getTecnico().equals(...)` com o técnico vazio, e dá `NullPointerException`. O Spring transforma esse erro interno em 403, por isso parecia "acesso negado".

Enquanto o Tiago não corrige, coloquei um contorno na **ChamadoDetalheActivity**: se o salvamento falhar, o app tenta de novo uma vez sozinho. Está comentado no código como "CONTORNO TEMPORÁRIO", para tirar depois.

---

## Coisas que mudei no back-end (só no meu computador)

Não mexi no código do servidor, só em como eu rodo ele aqui.

No `docker-compose.yaml`, adicionei a linha `platform: linux/amd64`. Meu Mac tem chip Apple, e a imagem do servidor só existe na versão para Intel/AMD. Essa linha faz o Docker rodar a versão Intel usando emulação.

Também criei um script chamado `ligar.sh`, que apaga o banco e liga o servidor do zero. Precisei disso por causa do problema do `data.sql` que explico logo abaixo.

---

## Coisas para o Tiago olhar no back-end

Tiago, achei algumas coisas no servidor que atrapalham o app. Nada urgente, mas quando der:

1. **O servidor cai na segunda vez que liga.** O `data.sql` tenta inserir o admin e os outros usuários toda vez que o servidor sobe. Na segunda vez dá erro de email duplicado e o servidor desliga sozinho. Dá para resolver usando `INSERT IGNORE` ou colocando `spring.sql.init.mode=never` depois da primeira carga.

2. **O `GET /chamado` não devolve o título** (nem o id do técnico). Por isso o app está usando a descrição no lugar do título.

3. **O `GET /prioridade` e o `GET /perfil` não devolvem o id**, só o nome. O app precisa do id para criar chamados, então por enquanto estou usando a posição na lista.

4. **O `GET /usuario/email/{email}` devolve o `tipoPerfil` vazio** (null). Com isso o app não sabe se a pessoa é admin, técnico ou usuário comum, e não consegue liberar as ações certas para cada perfil (excluir chamado, mudar status etc.).

5. **Mudar o status apaga o título original.** Como o `GET /chamado` não devolve o título, o app não tem como reenviar o título certo no `PUT /chamado`. Resolvendo o item 2, isso some também.

6. **O `GET /tecnico` e o `GET /chamado` não devolvem o id do técnico.** O app usa a posição na lista de técnicos como id, e não consegue mostrar qual técnico já está atendendo cada chamado.

7. **O `PUT /chamado` exige `idTecnico`.** Tudo bem, faz sentido, mas vale documentar no Swagger, porque não dá para saber isso só olhando.

8. **Primeira mudança de um chamado sem técnico quebra o servidor.** Em `RelatorioChamadoService.detectarMudancas()` (linha 46), `chamado.getTecnico()` vem `null` e o `.equals()` dá `NullPointerException`. O Spring devolve 403 em vez de 500. Dá para resolver com `Objects.equals(antigo.getTecnico(), novo.getTecnico())`. O app tenta duas vezes como contorno.
