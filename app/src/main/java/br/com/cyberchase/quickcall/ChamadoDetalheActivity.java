package br.com.cyberchase.quickcall;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
import br.com.cyberchase.quickcall.model.StatusChamado;
import br.com.cyberchase.quickcall.network.NetworkPolicy;
import br.com.cyberchase.quickcall.ui.UiFormatter;

public class ChamadoDetalheActivity extends AppCompatActivity {

    public static final String EXTRA_CHAMADO_ID = "chamado_id";

    private TextView tituloView;
    private TextView descricaoView;
    private TextView metadadosView;
    private Spinner statusSpinner;
    private EditText comentarioInput;
    private ListView comentariosListView;
    private ListView historicoListView;

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
        comentarioInput = findViewById(R.id.input_comentario);
        comentariosListView = findViewById(R.id.list_comentarios);
        historicoListView = findViewById(R.id.list_historico);
        Button salvarStatusButton = findViewById(R.id.button_salvar_status);
        Button comentarButton = findViewById(R.id.button_adicionar_comentario);
        Button excluirButton = findViewById(R.id.button_excluir_chamado);

        carregarChamado();
        carregarStatus();
        atualizarTela();

        String perfil = new SessionManager(this).obterPerfilUsuarioLogado();
        if (!"TECNICO".equals(perfil) && !"ADMINISTRADOR".equals(perfil)) {
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

    private void atualizarTela() {
        if (chamado == null) {
            Toast.makeText(this, "Chamado nao encontrado.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        CatalogoRepository catalogoRepository = new CatalogoRepository();
        tituloView.setText(chamado.getTitulo() == null ? "Chamado" : chamado.getTitulo());
        descricaoView.setText(chamado.getDescricao());
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
        comentariosListView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, linhas));
    }

    private void carregarHistorico() {
        List<HistoricoChamado> historicos = new HistoricoRepository(this).listarPorChamado(chamado.getId());
        List<String> linhas = new ArrayList<>();
        for (HistoricoChamado historico : historicos) {
            linhas.add(UiFormatter.formatDateTime(historico.getDataHora()) + " - " + historico.getAcao());
        }
        if (linhas.isEmpty()) {
            linhas.add("Nenhum historico registrado.");
        }
        historicoListView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, linhas));
    }

    private void atualizarStatus() {
        if (chamado == null) {
            return;
        }

        StatusChamado novoStatus = statusChamados.get(statusSpinner.getSelectedItemPosition());
        String statusAnterior = UiFormatter.findStatusNome(statusChamados, chamado.getStatusId());
        chamado.alterarStatus(novoStatus.getId());

        if ("Concluido".equalsIgnoreCase(novoStatus.getNome()) && chamado.getDataFechamento() == null) {
            chamado.fechar("Encerrado manualmente no prototipo");
        }

        chamado = new ChamadoRepository(this).salvar(chamado);
        if (chamado == null) {
            Toast.makeText(this, "Nao foi possivel atualizar o status.", Toast.LENGTH_SHORT).show();
            return;
        }

        HistoricoChamado historico = new HistoricoChamado();
        historico.setChamadoId(chamado.getId());
        historico.setUsuarioId(new SessionManager(this).obterUsuarioLogado());
        historico.registrarAlteracao("Status alterado", statusAnterior, novoStatus.getNome());
        new HistoricoRepository(this).salvar(historico);

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
        new ComentarioRepository(this).salvar(comentario);

        HistoricoChamado historico = new HistoricoChamado();
        historico.setChamadoId(chamado.getId());
        historico.setUsuarioId(new SessionManager(this).obterUsuarioLogado());
        historico.registrarAlteracao("Comentario adicionado", null, texto);
        new HistoricoRepository(this).salvar(historico);

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
