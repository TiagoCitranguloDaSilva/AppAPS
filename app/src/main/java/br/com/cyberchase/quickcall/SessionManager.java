package br.com.cyberchase.quickcall;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREFS_NAME = "quickcall_session";
    private static final String KEY_USUARIO_ID = "usuario_id";
    private static final String KEY_USUARIO_NOME = "usuario_nome";
    private static final String KEY_USUARIO_PERFIL = "usuario_perfil";

    private final SharedPreferences preferences;

    public SessionManager(Context context) {
        this.preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void salvarUsuarioLogado(long usuarioId) {
        preferences.edit().putLong(KEY_USUARIO_ID, usuarioId).apply();
    }

    public void salvarSessao(long usuarioId, String nome, String perfil) {
        preferences.edit()
                .putLong(KEY_USUARIO_ID, usuarioId)
                .putString(KEY_USUARIO_NOME, nome)
                .putString(KEY_USUARIO_PERFIL, perfil)
                .apply();
    }

    public Long obterUsuarioLogado() {
        if (!preferences.contains(KEY_USUARIO_ID)) {
            return null;
        }
        return preferences.getLong(KEY_USUARIO_ID, -1L);
    }

    public void logout() {
        preferences.edit()
                .remove(KEY_USUARIO_ID)
                .remove(KEY_USUARIO_NOME)
                .remove(KEY_USUARIO_PERFIL)
                .apply();
    }

    public boolean estaLogado() {
        return preferences.contains(KEY_USUARIO_ID);
    }

    public String obterNomeUsuarioLogado() {
        return preferences.getString(KEY_USUARIO_NOME, null);
    }

    public String obterPerfilUsuarioLogado() {
        return preferences.getString(KEY_USUARIO_PERFIL, null);
    }
}
