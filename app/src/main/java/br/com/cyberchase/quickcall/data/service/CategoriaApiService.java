package br.com.cyberchase.quickcall.data.service;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import br.com.cyberchase.quickcall.model.Categoria;
import br.com.cyberchase.quickcall.network.HttpJsonClient;

public class CategoriaApiService {

    private final HttpJsonClient client = new HttpJsonClient();

    public List<Categoria> listarTodas() {
        JSONArray array = client.getArray("/categoria");
        List<Categoria> categorias = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                Categoria categoria = new Categoria();
                categoria.setId(array.getJSONObject(i).optLong("id"));
                categoria.setNome(array.getJSONObject(i).optString("nome", null));
                categoria.setAtiva(true);
                categorias.add(categoria);
            } catch (JSONException ignored) {
            }
        }
        return categorias;
    }
}
