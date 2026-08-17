package br.com.cyberchase.quickcall.data.repository;

import android.content.Context;

import java.util.List;

import br.com.cyberchase.quickcall.data.service.UsuarioApiService;
import br.com.cyberchase.quickcall.model.Usuario;

public class UsuarioRepository {

    private final UsuarioApiService apiService;

    public UsuarioRepository(Context context) {
        this.apiService = new UsuarioApiService();
    }

    public List<Usuario> listarTodos() {
        return apiService.listarTodos();
    }

    public Usuario buscarPorId(long id) {
        return apiService.buscarPorId(id);
    }

    public Usuario buscarPorEmail(String email) {
        return apiService.buscarPorEmail(email);
    }

    public Usuario salvar(Usuario usuario) {
        return apiService.salvar(usuario);
    }

    public void remover(long id) {
        apiService.remover(id);
    }
}
