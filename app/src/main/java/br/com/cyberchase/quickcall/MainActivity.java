package br.com.cyberchase.quickcall;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

import br.com.cyberchase.quickcall.data.repository.CatalogoRepository;
import br.com.cyberchase.quickcall.data.repository.ChamadoRepository;
import br.com.cyberchase.quickcall.data.repository.DemoDataSeeder;
import br.com.cyberchase.quickcall.model.Categoria;
import br.com.cyberchase.quickcall.model.Chamado;
import br.com.cyberchase.quickcall.model.Prioridade;
import br.com.cyberchase.quickcall.model.StatusChamado;
import br.com.cyberchase.quickcall.network.NetworkPolicy;
import br.com.cyberchase.quickcall.ui.UiFormatter;

public class MainActivity extends AppCompatActivity {

    private final List<Chamado> chamados = new ArrayList<>();
    private ArrayAdapter<String> chamadosAdapter;
    private TextView resumoView;
    private ListView chamadosListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NetworkPolicy.enable();
        new DemoDataSeeder(this).seedIfNeeded();
        SessionManager sessionManager = new SessionManager(this);
        if (!sessionManager.estaLogado()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        resumoView = findViewById(R.id.text_resumo);
        chamadosListView = findViewById(R.id.list_chamados);
        Button novoChamadoButton = findViewById(R.id.button_novo_chamado);
        Button sairButton = findViewById(R.id.button_sair);

        chamadosAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        chamadosListView.setAdapter(chamadosAdapter);

        novoChamadoButton.setOnClickListener(v ->
                startActivity(new Intent(this, NovoChamadoActivity.class)));

        sairButton.setOnClickListener(v -> {
            new SessionManager(this).logout();
            br.com.cyberchase.quickcall.network.HttpJsonClient.setToken(null);
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        chamadosListView.setOnItemClickListener((parent, view, position, id) -> {
            Chamado chamado = chamados.get(position);
            Intent intent = new Intent(this, ChamadoDetalheActivity.class);
            intent.putExtra(ChamadoDetalheActivity.EXTRA_CHAMADO_ID, chamado.getId());
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarDashboard();
    }

    private void carregarDashboard() {
        chamados.clear();
        chamados.addAll(new ChamadoRepository(this).listarTodos());

        // Busca as listas do servidor UMA vez (antes buscava a cada chamado da lista).
        CatalogoRepository catalogoRepository = new CatalogoRepository();
        List<StatusChamado> status = catalogoRepository.listarStatusPadrao();
        List<Prioridade> prioridades = catalogoRepository.listarPrioridadesPadrao();
        List<Categoria> categorias = catalogoRepository.listarCategoriasPadrao();

        SessionManager sessionManager = new SessionManager(this);
        String nomeUsuario = sessionManager.obterNomeUsuarioLogado();
        String perfilUsuario = sessionManager.obterPerfilUsuarioLogado();

        // Cada item da lista: titulo na 1a linha; status, prioridade, categoria e data na 2a.
        List<String> linhas = new ArrayList<>();
        for (Chamado chamado : chamados) {
            linhas.add(
                    "#" + chamado.getId() + "  " + (chamado.getTitulo() == null ? "Chamado" : chamado.getTitulo())
                            + "\n"
                            + UiFormatter.findStatusNome(status, chamado.getStatusId())
                            + " | " + UiFormatter.findPrioridadeNome(prioridades, chamado.getPrioridadeId())
                            + " | " + UiFormatter.findCategoriaNome(categorias, chamado.getCategoriaId())
                            + " | " + UiFormatter.formatDateTime(chamado.getDataAbertura())
            );
        }

        chamadosAdapter.clear();
        chamadosAdapter.addAll(linhas);
        chamadosAdapter.notifyDataSetChanged();

        resumoView.setText(
                "Usuario logado: " + (nomeUsuario == null ? "-" : nomeUsuario)
                        + "\nPerfil: " + (perfilUsuario == null ? "nao informado pelo servidor" : perfilUsuario)
                        + "\nChamados carregados: " + chamados.size()
        );
    }
}
