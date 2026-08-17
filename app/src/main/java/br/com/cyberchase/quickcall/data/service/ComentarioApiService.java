package br.com.cyberchase.quickcall.data.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.com.cyberchase.quickcall.data.repository.storage.JsonMapper;
import br.com.cyberchase.quickcall.model.Comentario;
import br.com.cyberchase.quickcall.network.HttpJsonClient;

public class ComentarioApiService {

    private final HttpJsonClient client = new HttpJsonClient();

    public List<Comentario> listarPorChamado(long chamadoId) {
        JSONArray array = client.getArray("/comentarios/chamado/" + chamadoId);
        List<Comentario> comentarios = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                comentarios.add(JsonMapper.toComentario(array.getJSONObject(i)));
            } catch (JSONException ignored) {
            }
        }
        return comentarios;
    }

    public Comentario salvar(Comentario comentario) {
        try {
            JSONObject json = client.post("/comentarios", JsonMapper.toJson(comentario));
            return json == null ? null : JsonMapper.toComentario(json);
        } catch (JSONException e) {
            return null;
        }
    }
}
