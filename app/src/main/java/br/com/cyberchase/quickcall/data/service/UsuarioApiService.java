package br.com.cyberchase.quickcall.data.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.com.cyberchase.quickcall.model.TipoPerfil;
import br.com.cyberchase.quickcall.model.Usuario;
import br.com.cyberchase.quickcall.network.HttpJsonClient;

public class UsuarioApiService {

    private final HttpJsonClient client = new HttpJsonClient();

    public List<Usuario> listarTodos() {
        JSONArray array = client.getArray("/usuarios");
        List<Usuario> usuarios = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                usuarios.add(toUsuario(array.getJSONObject(i)));
            } catch (JSONException ignored) {
            }
        }
        return usuarios;
    }

    public Usuario buscarPorId(long id) {
        JSONObject json = client.getObject("/usuarios/" + id);
        if (json == null) {
            return null;
        }
        try {
            return toUsuario(json);
        } catch (JSONException e) {
            return null;
        }
    }

    public Usuario buscarPorEmail(String email) {
        JSONObject json = client.getObject("/usuarios/email/" + URLEncoder.encode(email, StandardCharsets.UTF_8));
        if (json == null) {
            return null;
        }
        try {
            return toUsuario(json);
        } catch (JSONException e) {
            return null;
        }
    }

    public Usuario salvar(Usuario usuario) {
        try {
            JSONObject body = new JSONObject();
            body.put("nome", usuario.getNome());
            body.put("email", usuario.getEmail());
            body.put("senha", usuario.getSenhaHash());
            body.put("role", usuario.getTipoPerfil() == null ? JSONObject.NULL : usuario.getTipoPerfil().name());
            JSONObject json = usuario.getId() == null
                    ? client.post("/usuarios", body)
                    : client.put("/usuarios/" + usuario.getId(), body);
            return json == null ? null : toUsuario(json);
        } catch (JSONException e) {
            return null;
        }
    }

    public void remover(long id) {
        client.delete("/usuarios/" + id);
    }

    private Usuario toUsuario(JSONObject json) throws JSONException {
        Usuario usuario = new Usuario();
        usuario.setId(json.optLong("id"));
        usuario.setNome(json.optString("nome", null));
        usuario.setEmail(json.optString("email", null));
        usuario.setSenhaHash(json.optString("senhaHash", null));
        usuario.setTelefone(json.optString("telefone", null));
        usuario.setAtivo(json.optBoolean("ativo", true));
        String dataCriacao = json.optString("dataCriacao", null);
        if (dataCriacao != null && !dataCriacao.isBlank() && !"null".equalsIgnoreCase(dataCriacao)) {
            usuario.setDataCriacao(LocalDateTime.parse(dataCriacao));
        }
        String tipoPerfil = json.optString("tipoPerfil", null);
        if (tipoPerfil != null && !tipoPerfil.isBlank() && !"null".equalsIgnoreCase(tipoPerfil)) {
            usuario.setTipoPerfil(TipoPerfil.valueOf(tipoPerfil));
        }
        usuario.setSetorId(json.isNull("setorId") ? null : json.getLong("setorId"));
        usuario.setMatriculaOuRegistro(json.isNull("matriculaOuRegistro") ? null : json.getString("matriculaOuRegistro"));
        usuario.setEspecialidade(json.isNull("especialidade") ? null : json.getString("especialidade"));
        usuario.setNivelTecnico(json.isNull("nivelTecnico") ? null : json.getString("nivelTecnico"));
        usuario.setDisponivel(json.optBoolean("disponivel", false));
        usuario.setNivelAcesso(json.isNull("nivelAcesso") ? null : json.getInt("nivelAcesso"));
        return usuario;
    }
}
