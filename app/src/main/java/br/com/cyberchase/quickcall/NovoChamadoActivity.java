package br.com.cyberchase.quickcall;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.com.cyberchase.quickcall.data.repository.CatalogoRepository;
import br.com.cyberchase.quickcall.data.repository.ChamadoRepository;
import br.com.cyberchase.quickcall.data.repository.HistoricoRepository;
import br.com.cyberchase.quickcall.model.Categoria;
import br.com.cyberchase.quickcall.model.Chamado;
import br.com.cyberchase.quickcall.model.HistoricoChamado;
import br.com.cyberchase.quickcall.model.Prioridade;
import br.com.cyberchase.quickcall.network.NetworkPolicy;

public class NovoChamadoActivity extends AppCompatActivity {

    private EditText tituloInput;
    private EditText descricaoInput;
    private Spinner categoriaSpinner;
    private Spinner prioridadeSpinner;

    private List<Categoria> categorias;
    private List<Prioridade> prioridades;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NetworkPolicy.enable();
        setContentView(R.layout.activity_novo_chamado);

        tituloInput = findViewById(R.id.input_titulo);
        descricaoInput = findViewById(R.id.input_descricao);
        categoriaSpinner = findViewById(R.id.spinner_categoria);
        prioridadeSpinner = findViewById(R.id.spinner_prioridade);
        Button salvarButton = findViewById(R.id.button_salvar_chamado);

        carregarCatalogos();
        salvarButton.setOnClickListener(v -> salvarChamado());
    }

    private void carregarCatalogos() {
        CatalogoRepository catalogoRepository = new CatalogoRepository();
        categorias = catalogoRepository.listarCategoriasPadrao();
        prioridades = catalogoRepository.listarPrioridadesPadrao();

        List<String> categoriasNomes = new ArrayList<>();
        for (Categoria categoria : categorias) {
            categoriasNomes.add(categoria.getNome());
        }

        List<String> prioridadesNomes = new ArrayList<>();
        for (Prioridade prioridade : prioridades) {
            prioridadesNomes.add(prioridade.getNome());
        }

        categoriaSpinner.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                categoriasNomes
        ));

        prioridadeSpinner.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                prioridadesNomes
        ));
    }

    private void salvarChamado() {
        String titulo = tituloInput.getText().toString().trim();
        String descricao = descricaoInput.getText().toString().trim();

        if (titulo.isEmpty() || descricao.isEmpty()) {
            Toast.makeText(this, "Preencha titulo e descricao.", Toast.LENGTH_SHORT).show();
            return;
        }

        Long usuarioId = new SessionManager(this).obterUsuarioLogado();
        if (usuarioId == null) {
            Toast.makeText(this, "Nenhum solicitante disponivel.", Toast.LENGTH_SHORT).show();
            return;
        }

        Categoria categoriaSelecionada = categorias.get(categoriaSpinner.getSelectedItemPosition());
        Prioridade prioridadeSelecionada = prioridades.get(prioridadeSpinner.getSelectedItemPosition());

        Chamado chamado = new Chamado();
        chamado.setTitulo(titulo);
        chamado.setDescricao(descricao);
        chamado.setDataAbertura(LocalDateTime.now());
        chamado.abrir(usuarioId, categoriaSelecionada.getId(), prioridadeSelecionada.getId(), 1L);

        ChamadoRepository chamadoRepository = new ChamadoRepository(this);
        chamado = chamadoRepository.salvar(chamado);
        if (chamado == null || chamado.getId() == null) {
            Toast.makeText(this, "Nao foi possivel salvar o chamado.", Toast.LENGTH_SHORT).show();
            return;
        }

        HistoricoChamado historico = new HistoricoChamado();
        historico.setChamadoId(chamado.getId());
        historico.setUsuarioId(usuarioId);
        historico.registrarAlteracao("Chamado aberto", null, "Status: Aberto");
        new HistoricoRepository(this).salvar(historico);

        Toast.makeText(this, "Chamado salvo com sucesso.", Toast.LENGTH_SHORT).show();
        finish();
    }
}
