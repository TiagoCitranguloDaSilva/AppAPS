package br.com.cyberchase.quickcall.data.repository;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.com.cyberchase.quickcall.data.service.CategoriaApiService;
import br.com.cyberchase.quickcall.model.Categoria;
import br.com.cyberchase.quickcall.model.Prioridade;
import br.com.cyberchase.quickcall.model.StatusChamado;
import br.com.cyberchase.quickcall.network.HttpJsonClient;

/**
 * Listas fixas do sistema (categorias, prioridades e status).
 * Agora vem do servidor. Se o servidor nao responder, usa uma lista local de reserva.
 */
public class CatalogoRepository {

    private final CategoriaApiService categoriaApiService = new CategoriaApiService();
    private final HttpJsonClient client = new HttpJsonClient();

    public List<Categoria> listarCategoriasPadrao() {
        return categoriaApiService.listarTodas();
    }

    /**
     * GET /prioridade devolve so o nome (sem id). Por isso o id e a posicao na lista:
     * 1 = primeira (Baixa), 2 = segunda (Media)... igual a ordem do banco.
     */
    public List<Prioridade> listarPrioridadesPadrao() {
        List<Prioridade> prioridades = new ArrayList<>();
        JSONArray array = client.getArray("/prioridade");
        for (int i = 0; i < array.length(); i++) {
            JSONObject json = array.optJSONObject(i);
            if (json == null) {
                continue;
            }
            long id = json.has("id") ? json.optLong("id") : i + 1;
            prioridades.add(new Prioridade(id, json.optString("nome", "-"), 0, null));
        }
        if (prioridades.isEmpty()) {
            prioridades.add(new Prioridade(1L, "Baixa", 72, null));
            prioridades.add(new Prioridade(2L, "Media", 48, null));
            prioridades.add(new Prioridade(3L, "Alta", 24, null));
            prioridades.add(new Prioridade(4L, "Urgente", 8, null));
        }
        return prioridades;
    }

    public List<StatusChamado> listarStatusPadrao() {
        List<StatusChamado> status = new ArrayList<>();
        JSONArray array = client.getArray("/status");
        for (int i = 0; i < array.length(); i++) {
            JSONObject json = array.optJSONObject(i);
            if (json == null) {
                continue;
            }
            long id = json.optLong("id", i + 1);
            status.add(new StatusChamado(id, json.optString("nome", "-"), null, (int) id));
        }
        if (status.isEmpty()) {
            status.add(new StatusChamado(1L, "Aberto", null, 1));
            status.add(new StatusChamado(2L, "Em Atendimento", null, 2));
            status.add(new StatusChamado(3L, "Pendente", null, 3));
            status.add(new StatusChamado(4L, "Resolvido", null, 4));
            status.add(new StatusChamado(5L, "Fechado", null, 5));
        }
        return status;
    }
}
