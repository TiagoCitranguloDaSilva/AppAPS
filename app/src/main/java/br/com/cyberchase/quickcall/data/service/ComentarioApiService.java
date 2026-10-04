package br.com.cyberchase.quickcall.data.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.com.cyberchase.quickcall.model.Comentario;
import br.com.cyberchase.quickcall.network.HttpJsonClient;

/**
 * Comentarios de um chamado.
 * Listar: GET /comentario/chamado/{id}
 * Criar:  POST /comentario  {"chamadoId":3, "usuarioId":1, "mensagem":"texto"}
 * O servidor devolve: {"id":1, "chamadoId":3, "usuarioId":1, "mensagem":"texto", "dataHora":"..."}
 */
public class ComentarioApiService {

    private final HttpJsonClient client = new HttpJsonClient();

    public List<Comentario> listarPorChamado(long chamadoId) {
        JSONArray array = client.getArray("/comentario/chamado/" + chamadoId);
        List<Comentario> comentarios = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject json = array.optJSONObject(i);
            if (json != null) {
                comentarios.add(toComentario(json));
            }
        }
        return comentarios;
    }

    public Comentario salvar(Comentario comentario) {
        try {
            JSONObject body = new JSONObject();
            body.put("chamadoId", comentario.getChamadoId());
            body.put("usuarioId", comentario.getAutorId());
            body.put("mensagem", comentario.getTexto());
            JSONObject json = client.post("/comentario", body);
            return json == null || !json.has("id") ? null : toComentario(json);
        } catch (JSONException e) {
            return null;
        }
    }

    private Comentario toComentario(JSONObject json) {
        Comentario comentario = new Comentario();
        comentario.setId(json.optLong("id"));
        comentario.setChamadoId(json.isNull("chamadoId") ? null : json.optLong("chamadoId"));
        comentario.setAutorId(json.isNull("usuarioId") ? null : json.optLong("usuarioId"));
        comentario.setTexto(json.isNull("mensagem") ? null : json.optString("mensagem"));
        if (!json.isNull("dataHora")) {
            try {
                comentario.setDataHora(LocalDateTime.parse(json.optString("dataHora")));
            } catch (Exception ignored) {
                // formato de data inesperado: deixa sem data
            }
        }
        return comentario;
    }
}
