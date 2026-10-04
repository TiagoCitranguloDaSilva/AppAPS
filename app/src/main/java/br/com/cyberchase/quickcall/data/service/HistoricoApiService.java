package br.com.cyberchase.quickcall.data.service;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.com.cyberchase.quickcall.model.HistoricoChamado;
import br.com.cyberchase.quickcall.network.HttpJsonClient;

/**
 * Historico de um chamado.
 * Quem registra o historico e o proprio servidor (quando o chamado muda).
 * O app so le: GET /relatorio/chamado/{id}
 * Cada item: {"id":1, "tipoMudanca":"STATUS", "valorAntigo":"Aberto", "valorNovo":"Resolvido", "dataHora":"...", "idChamado":4}
 */
public class HistoricoApiService {

    private final HttpJsonClient client = new HttpJsonClient();

    public List<HistoricoChamado> listarPorChamado(long chamadoId) {
        List<HistoricoChamado> historicos = new ArrayList<>();
        String rota = "/relatorio/chamado/" + chamadoId;

        // O servidor pode devolver uma lista [...] ou um item so {...}. Tratamos os dois casos.
        JSONArray array = client.getArray(rota);
        if (array.length() == 0) {
            JSONObject unico = client.getObject(rota);
            if (unico != null && unico.has("id")) {
                array.put(unico);
            }
        }

        for (int i = 0; i < array.length(); i++) {
            JSONObject json = array.optJSONObject(i);
            if (json == null) {
                continue;
            }
            HistoricoChamado historico = new HistoricoChamado();
            historico.setId(json.optLong("id"));
            historico.setChamadoId(chamadoId);
            historico.setAcao(json.isNull("tipoMudanca") ? "Alteracao" : json.optString("tipoMudanca"));
            historico.setValorAnterior(json.isNull("valorAntigo") ? null : json.optString("valorAntigo"));
            historico.setValorNovo(json.isNull("valorNovo") ? null : json.optString("valorNovo"));
            if (!json.isNull("dataHora")) {
                try {
                    historico.setDataHora(LocalDateTime.parse(json.optString("dataHora")));
                } catch (Exception ignored) {
                    // formato de data inesperado: deixa sem data
                }
            }
            historicos.add(historico);
        }
        return historicos;
    }

    /** O servidor ja grava o historico sozinho. Este metodo nao envia nada. */
    public HistoricoChamado salvar(HistoricoChamado historico) {
        return historico;
    }
}
