package br.com.cyberchase.quickcall;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import br.com.cyberchase.quickcall.data.repository.CatalogoRepository;
import br.com.cyberchase.quickcall.data.repository.ChamadoRepository;
import br.com.cyberchase.quickcall.data.repository.ComentarioRepository;
import br.com.cyberchase.quickcall.data.repository.HistoricoRepository;
import br.com.cyberchase.quickcall.model.Chamado;
import br.com.cyberchase.quickcall.model.Comentario;
import br.com.cyberchase.quickcall.model.HistoricoChamado;
import br.com.cyberchase.quickcall.data.service.TecnicoApiService;
import br.com.cyberchase.quickcall.model.StatusChamado;
import br.com.cyberchase.quickcall.model.Tecnico;
import br.com.cyberchase.quickcall.network.NetworkPolicy;
import br.com.cyberchase.quickcall.ui.UiFormatter;

public class ChamadoDetalheActivity extends AppCompatActivity {

    public static final String EXTRA_CHAMADO_ID = "chamado_id";

    private TextView tituloView;
    private TextView descricaoView;
    private TextView metadadosView;
    private Spinner statusSpinner;
    private Spinner tecnicoSpinner;
    private List<Tecnico> tecnicos = new ArrayList<>();
    private EditText comentarioInput;
    // Antes eram ListView (lista com rolagem propria) dentro de uma tela que tambem rola.
    // Uma rolagem dentro da outra travava a tela. Agora sao caixas simples que crescem com o conteudo.
    private LinearLayout comentariosListView;
    private LinearLayout historicoListView;

    private Chamado chamado;
    private List<StatusChamado> statusChamados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NetworkPolicy.enable();
        setContentView(R.layout.activity_chamado_detalhe);

        tituloView = findViewById(R.id.text_titulo_detalhe);
        descricaoView = findViewById(R.id.text_descricao_detalhe);
        metadadosView = findViewById(R.id.text_metadados_detalhe);
        statusSpinner = findViewById(R.id.spinner_status);
        tecnicoSpinner = findViewById(R.id.spinner_tecnico);
        comentarioInput = findViewById(R.id.input_comentario);
        comentariosListView = findViewById(R.id.list_comentarios);
        historicoListView = findViewById(R.id.list_historico);
        Button salvarStatusButton = findViewById(R.id.button_salvar_status);
        Button comentarButton = findViewById(R.id.button_adicionar_comentario);
        Button excluirButton = findViewById(R.id.button_excluir_chamado);

        // Botao Voltar: fecha esta tela e volta para a lista
        findViewById(R.id.button_voltar).setOnClickListener(v -> finish());

        carregarChamado();
        carregarStatus();
        carregarTecnicos();
        atualizarTela();

        String perfil = new SessionManager(this).obterPerfilUsuarioLogado();
        // So tecnico e administrador podem excluir.
        // Enquanto o servidor nao informar o perfil (vem null), o botao fica visivel.
        if (perfil != null && !"TECNICO".equals(perfil) && !"ADMINISTRADOR".equals(perfil)) {
            excluirButton.setVisibility(View.GONE);
        }

        salvarStatusButton.setOnClickListener(v -> atualizarStatus());
        comentarButton.setOnClickListener(v -> adicionarComentario());
        excluirButton.setOnClickListener(v -> confirmarExclusao());
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarChamado();
        atualizarTela();
    }

    private void carregarChamado() {
        long chamadoId = getIntent().getLongExtra(EXTRA_CHAMADO_ID, -1L);
        chamado = new ChamadoRepository(this).buscarPorId(chamadoId);
    }

    private void carregarStatus() {
        statusChamados = new CatalogoRepository().listarStatusPadrao();
        List<String> nomesStatus = new ArrayList<>();
        int selectedIndex = 0;

        for (int i = 0; i < statusChamados.size(); i++) {
            StatusChamado statusChamado = statusChamados.get(i);
            nomesStatus.add(statusChamado.getNome());
            if (chamado != null && statusChamado.getId().equals(chamado.getStatusId())) {
                selectedIndex = i;
            }
        }

        statusSpinner.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                nomesStatus
        ));
        statusSpinner.setSelection(selectedIndex);
    }

    /**
     * Preenche a lista de tecnicos. O servidor exige um tecnico para mudar o status.
     * Ja deixa selecionado um tecnico que atende a categoria deste chamado.
     */
    private void carregarTecnicos() {
        tecnicos = new TecnicoApiService().listarTodos();
        List<String> nomes = new ArrayList<>();
        int selecionado = 0;
        boolean achou = false;
        for (int i = 0; i < tecnicos.size(); i++) {
            Tecnico tecnico = tecnicos.get(i);
            nomes.add(tecnico.getNome() + " (" + android.text.TextUtils.join(", ", tecnico.getCategoriaNomes()) + ")");
            if (chamado != null && tecnico.atende(chamado.getCategoriaId()) && !achou) {
                selecionado = i;
                achou = true;
            }
        }
        if (nomes.isEmpty()) {
            nomes.add("Nenhum tecnico cadastrado");
        }
        tecnicoSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, nomes));
        tecnicoSpinner.setSelection(selecionado);
    }

    private void atualizarTela() {
        if (chamado == null) {
            Toast.makeText(this, "Chamado nao encontrado.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        CatalogoRepository catalogoRepository = new CatalogoRepository();
        tituloView.setText(chamado.getTitulo() == null ? "Chamado" : chamado.getTitulo());
        // Se a descricao for igual ao titulo (a API ainda nao manda titulo), nao repete.
        boolean descricaoRepetida = chamado.getDescricao() == null || chamado.getDescricao().equals(chamado.getTitulo());
        descricaoView.setText(descricaoRepetida ? "" : chamado.getDescricao());
        descricaoView.setVisibility(descricaoRepetida ? View.GONE : View.VISIBLE);
        metadadosView.setText(
                "Categoria: " + UiFormatter.findCategoriaNome(catalogoRepository.listarCategoriasPadrao(), chamado.getCategoriaId())
                        + "\nPrioridade: " + UiFormatter.findPrioridadeNome(catalogoRepository.listarPrioridadesPadrao(), chamado.getPrioridadeId())
                        + "\nStatus: " + UiFormatter.findStatusNome(catalogoRepository.listarStatusPadrao(), chamado.getStatusId())
                        + "\nAbertura: " + UiFormatter.formatDateTime(chamado.getDataAbertura())
                        + "\nFechamento: " + UiFormatter.formatDateTime(chamado.getDataFechamento())
        );

        carregarComentarios();
        carregarHistorico();
    }

    private void carregarComentarios() {
        List<Comentario> comentarios = new ComentarioRepository(this).listarPorChamado(chamado.getId());
        List<String> linhas = new ArrayList<>();
        for (Comentario comentario : comentarios) {
            linhas.add(UiFormatter.formatDateTime(comentario.getDataHora()) + " - " + comentario.getTexto());
        }
        if (linhas.isEmpty()) {
            linhas.add("Nenhum comentario registrado.");
        }
        preencherLista(comentariosListView, linhas);
    }

    private void carregarHistorico() {
        List<HistoricoChamado> historicos = new HistoricoRepository(this).listarPorChamado(chamado.getId());
        List<String> linhas = new ArrayList<>();
        for (HistoricoChamado historico : historicos) {
            String mudanca = historico.getAcao();
            String antes = textoDoHistorico(historico.getAcao(), historico.getValorAnterior());
            String depois = textoDoHistorico(historico.getAcao(), historico.getValorNovo());
            // So mostra "antes -> depois" quando tem algum valor (na "Criacao" os dois vem vazios)
            if (antes != null || depois != null) {
                mudanca += ": " + (antes == null ? "-" : antes) + " -> " + (depois == null ? "-" : depois);
            }
            linhas.add(UiFormatter.formatDateTime(historico.getDataHora()) + " - " + mudanca);
        }
        if (linhas.isEmpty()) {
            linhas.add("Nenhum historico registrado.");
        }
        preencherLista(historicoListView, linhas);
    }

    /**
     * O servidor grava a mudanca de status com o numero (ex.: "1 -> 2").
     * Aqui trocamos o numero pelo nome (ex.: "Aberto -> Em Atendimento").
     */
    private String textoDoHistorico(String acao, String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }
        if ("status".equalsIgnoreCase(acao) && statusChamados != null) {
            try {
                return UiFormatter.findStatusNome(statusChamados, Long.parseLong(valor.trim()));
            } catch (NumberFormatException ignored) {
                // nao era numero: mostra como veio
            }
        }
        return valor;
    }

    /** Coloca cada linha de texto dentro da caixa, uma embaixo da outra. */
    private void preencherLista(LinearLayout caixa, List<String> linhas) {
        caixa.removeAllViews();
        for (String linha : linhas) {
            TextView item = new TextView(this);
            item.setText(linha);
            item.setTextSize(15);
            item.setPadding(8, 12, 8, 12);
            caixa.addView(item);
        }
    }

    private void atualizarStatus() {
        if (chamado == null) {
            return;
        }

        StatusChamado novoStatus = statusChamados.get(statusSpinner.getSelectedItemPosition());
        String statusAnterior = UiFormatter.findStatusNome(statusChamados, chamado.getStatusId());
        chamado.alterarStatus(novoStatus.getId());

        // O servidor so aceita a mudanca se o chamado tiver um tecnico responsavel.
        if (tecnicos.isEmpty()) {
            Toast.makeText(this, "Nenhum tecnico cadastrado no servidor.", Toast.LENGTH_LONG).show();
            return;
        }
        // O tecnico escolhido assume o chamado e muda o status (metodos da classe Tecnico, da especificacao)
        Tecnico tecnico = tecnicos.get(tecnicoSpinner.getSelectedItemPosition());
        tecnico.aceitarChamado(chamado);
        tecnico.atualizarStatusChamado(chamado, novoStatus.getId());

        // "Resolvido" e "Fechado" sao os status finais no banco
        boolean statusFinal = "Resolvido".equalsIgnoreCase(novoStatus.getNome()) || "Fechado".equalsIgnoreCase(novoStatus.getNome());
        if (statusFinal && chamado.getDataFechamento() == null) {
            chamado.fechar("Encerrado manualmente no prototipo");
        }

        long chamadoId = chamado.getId();
        Chamado resposta = new ChamadoRepository(this).salvar(chamado);

        // CONTORNO TEMPORARIO de um bug do servidor:
        // quando o chamado ainda nao tem tecnico, o servidor da erro interno na 1a tentativa
        // (NullPointerException em RelatorioChamadoService) e responde 403.
        // A 2a tentativa, igual, funciona. Entao tentamos mais uma vez automaticamente.
        // Pode tirar isto quando o Tiago corrigir o back-end.
        if (resposta == null) {
            resposta = new ChamadoRepository(this).salvar(chamado);
        }
        if (resposta == null) {
            String erro = br.com.cyberchase.quickcall.network.HttpJsonClient.getUltimoErro();
            Toast.makeText(this, "Nao foi possivel atualizar o status" + (erro == null ? "." : ": " + erro), Toast.LENGTH_LONG).show();
            carregarChamado();
            return;
        }

        // Depois de salvar, busca o chamado de novo no servidor (GET /chamado/{id}).
        // A resposta do PUT pode vir incompleta, entao confiamos so no GET.
        chamado = new ChamadoRepository(this).buscarPorId(chamadoId);

        // O historico da mudanca e gravado pelo proprio servidor.
        Toast.makeText(this, "Status atualizado.", Toast.LENGTH_SHORT).show();
        atualizarTela();
    }

    private void adicionarComentario() {
        String texto = comentarioInput.getText().toString().trim();
        if (texto.isEmpty()) {
            Toast.makeText(this, "Digite um comentario.", Toast.LENGTH_SHORT).show();
            return;
        }

        Comentario comentario = new Comentario();
        comentario.setChamadoId(chamado.getId());
        comentario.setAutorId(new SessionManager(this).obterUsuarioLogado());
        comentario.setTexto(texto);
        Comentario salvo = new ComentarioRepository(this).salvar(comentario);
        if (salvo == null) {
            Toast.makeText(this, "Nao foi possivel salvar o comentario.", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Comentario adicionado.", Toast.LENGTH_SHORT).show();
        comentarioInput.setText("");
        atualizarTela();
    }

    private void confirmarExclusao() {
        new AlertDialog.Builder(this)
                .setTitle("Excluir chamado")
                .setMessage("Tem certeza que deseja excluir este chamado? Esta acao nao pode ser desfeita.")
                .setPositiveButton("Sim, excluir", (dialog, which) -> excluirChamado())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void excluirChamado() {
        boolean removido = new ChamadoRepository(this).remover(chamado.getId());
        if (removido) {
            Toast.makeText(this, "Chamado excluido com sucesso.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Nao foi possivel excluir o chamado.", Toast.LENGTH_SHORT).show();
        }
    }
}
