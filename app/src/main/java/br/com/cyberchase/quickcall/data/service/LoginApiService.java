package br.com.cyberchase.quickcall.data.service;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.cyberchase.quickcall.model.TipoPerfil;
import br.com.cyberchase.quickcall.model.Usuario;
import br.com.cyberchase.quickcall.network.HttpJsonClient;

public class LoginApiService {

    private final HttpJsonClient client = new HttpJsonClient();

    public Usuario login(String email, String senha) {
        try {
            JSONObject body = new JSONObject();
            body.put("email", email);
            body.put("senha", senha);

            JSONObject json = client.post("/login", body);
            if (json == null) {
                return null;
            }

            Usuario usuario = new Usuario();
            usuario.setId(json.optLong("id"));
            usuario.setNome(json.optString("nome", null));
            String role = json.optString("role", null);
            if (role != null && !role.isBlank() && !"null".equalsIgnoreCase(role)) {
                usuario.setTipoPerfil(TipoPerfil.valueOf(role));
            }
            usuario.setEmail(email);
            return usuario;
        } catch (JSONException e) {
            return null;
        }
    }
}
