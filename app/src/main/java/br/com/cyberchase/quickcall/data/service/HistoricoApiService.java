package br.com.cyberchase.quickcall.data.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.com.cyberchase.quickcall.data.repository.storage.JsonMapper;
import br.com.cyberchase.quickcall.model.HistoricoChamado;
import br.com.cyberchase.quickcall.network.HttpJsonClient;

public class HistoricoApiService {

    private final HttpJsonClient client = new HttpJsonClient();

    public List<HistoricoChamado> listarPorChamado(long chamadoId) {
        JSONArray array = client.getArray("/historicos/chamado/" + chamadoId);
        List<HistoricoChamado> historicos = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                historicos.add(JsonMapper.toHistorico(array.getJSONObject(i)));
            } catch (JSONException ignored) {
            }
        }
        return historicos;
    }

    public HistoricoChamado salvar(HistoricoChamado historico) {
        try {
            JSONObject json = client.post("/historicos", JsonMapper.toJson(historico));
            return json == null ? null : JsonMapper.toHistorico(json);
        } catch (JSONException e) {
            return null;
        }
    }
}
