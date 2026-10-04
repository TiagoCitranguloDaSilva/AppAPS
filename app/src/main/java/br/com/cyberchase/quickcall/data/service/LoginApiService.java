package br.com.cyberchase.quickcall.data.service;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.cyberchase.quickcall.model.Usuario;
import br.com.cyberchase.quickcall.network.HttpJsonClient;

public class LoginApiService {

    private final HttpJsonClient client = new HttpJsonClient();

    /**
     * Passo 1: manda email e senha para /auth/login e recebe um token.
     * Passo 2: guarda o token (todos os pedidos seguintes usam ele).
     * Passo 3: busca nome, id e perfil do usuario em /usuario/email/{email}.
     * Retorna null se email/senha estiverem errados ou o servidor estiver desligado.
     */
    public Usuario login(String email, String senha) {
        try {
            JSONObject body = new JSONObject();
            body.put("email", email);
            body.put("senha", senha);

            HttpJsonClient.setToken(null);
            JSONObject json = client.post("/auth/login", body);
            if (json == null || !json.has("token")) {
                return null;
            }

            HttpJsonClient.setToken(json.getString("token"));

            Usuario usuario = new UsuarioApiService().buscarPorEmail(email);
            if (usuario == null || usuario.getId() == null || usuario.getId() == 0) {
                HttpJsonClient.setToken(null);
                return null;
            }
            return usuario;
        } catch (JSONException e) {
            return null;
        }
    }
}
