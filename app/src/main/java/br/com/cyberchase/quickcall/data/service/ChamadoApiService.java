package br.com.cyberchase.quickcall.data.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.com.cyberchase.quickcall.model.Chamado;
import br.com.cyberchase.quickcall.network.HttpJsonClient;

public class ChamadoApiService {

    private final HttpJsonClient client = new HttpJsonClient();

    public List<Chamado> listarTodos() {
        JSONArray array = client.getArray("/chamado");
        List<Chamado> chamados = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                chamados.add(toChamado(array.getJSONObject(i)));
            } catch (JSONException ignored) {
            }
        }
        return chamados;
    }

    public List<Chamado> listarPorSolicitante(long solicitanteId) {
        List<Chamado> chamados = new ArrayList<>();
        for (Chamado chamado : listarTodos()) {
            if (chamado.getSolicitanteId() != null && chamado.getSolicitanteId() == solicitanteId) {
                chamados.add(chamado);
            }
        }
        return chamados;
    }

    public Chamado buscarPorId(long id) {
        JSONObject json = client.getObject("/chamado/" + id);
        if (json == null) {
            return null;
        }
        try {
            return toChamado(json);
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * Cria (POST) ou atualiza (PUT) um chamado.
     * Campos no formato que a API espera: titulo, descricao, idPrioridade, idStatus, idCategoria, idUsuario.
     */
    public Chamado salvar(Chamado chamado) {
        try {
            JSONObject body = new JSONObject();
            if (chamado.getId() != null) {
                body.put("id", chamado.getId());
            }
            body.put("titulo", chamado.getTitulo());
            body.put("descricao", chamado.getDescricao() == null ? chamado.getTitulo() : chamado.getDescricao());
            body.put("idPrioridade", chamado.getPrioridadeId() == null ? 1 : chamado.getPrioridadeId());
            body.put("idStatus", chamado.getStatusId() == null ? 1 : chamado.getStatusId());
            body.put("idCategoria", chamado.getCategoriaId());
            body.put("idUsuario", chamado.getSolicitanteId());
            if (chamado.getTecnicoResponsavelId() != null) {
                body.put("idTecnico", chamado.getTecnicoResponsavelId());
            }

            JSONObject json = chamado.getId() == null
                    ? client.post("/chamado", body)
                    : client.put("/chamado", body);
            return json == null ? null : toChamado(json);
        } catch (JSONException e) {
            return null;
        }
    }

    public boolean deletar(long id) {
        return client.deleteResource("/chamado/" + id);
    }

    /**
     * Converte o JSON do servidor em um objeto Chamado. Exemplo do que chega:
     * {"id":1,"descricao":"...","prioridade":{"nome":"Alta"},"dataAbertura":"2026-09-28T09:00:00",
     *  "idUsuario":4,"status":{"id":2,"nome":"Em Atendimento"},"categoria":{"id":3,"nome":"Redes"}}
     */
    private Chamado toChamado(JSONObject json) throws JSONException {
        Chamado chamado = new Chamado();
        chamado.setId(json.optLong("id"));

        // A API ainda nao devolve "titulo". Enquanto isso, a descricao vira o titulo.
        String descricao = texto(json, "descricao");
        String titulo = texto(json, "titulo");
        chamado.setTitulo(titulo != null ? titulo : descricao);
        chamado.setDescricao(descricao);

        String dataAbertura = texto(json, "dataAbertura");
        if (dataAbertura != null) {
            try {
                chamado.setDataAbertura(LocalDateTime.parse(dataAbertura));
            } catch (Exception ignored) {
                // formato inesperado: deixa sem data
            }
        }

        chamado.setSolicitanteId(json.isNull("idUsuario") ? null : json.optLong("idUsuario"));

        JSONObject status = json.optJSONObject("status");
        chamado.setStatusId(status == null ? null : status.optLong("id"));

        JSONObject categoria = json.optJSONObject("categoria");
        chamado.setCategoriaId(categoria == null ? null : categoria.optLong("id"));

        // Prioridade vem so com o nome: procuramos o id pelo nome na lista de prioridades.
        JSONObject prioridade = json.optJSONObject("prioridade");
        chamado.setPrioridadeId(prioridade == null ? null : idDaPrioridade(prioridade.optString("nome", null)));

        return chamado;
    }

    private List<String> nomesPrioridades;

    private Long idDaPrioridade(String nome) {
        if (nome == null) {
            return null;
        }
        if (nomesPrioridades == null) {
            nomesPrioridades = new ArrayList<>();
            JSONArray array = client.getArray("/prioridade");
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.optJSONObject(i);
                nomesPrioridades.add(item == null ? "" : item.optString("nome", ""));
            }
        }
        int posicao = nomesPrioridades.indexOf(nome);
        return posicao >= 0 ? (long) (posicao + 1) : null;
    }

    private String texto(JSONObject json, String campo) {
        if (json.isNull(campo)) {
            return null;
        }
        String valor = json.optString(campo, "").trim();
        return valor.isEmpty() ? null : valor;
    }
}
