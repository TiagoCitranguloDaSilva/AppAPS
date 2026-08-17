package br.com.cyberchase.quickcall.data.repository;

import android.content.Context;

import java.util.List;

import br.com.cyberchase.quickcall.data.service.ComentarioApiService;
import br.com.cyberchase.quickcall.model.Comentario;

public class ComentarioRepository {

    private final ComentarioApiService apiService;

    public ComentarioRepository(Context context) {
        this.apiService = new ComentarioApiService();
    }

    public List<Comentario> listarPorChamado(long chamadoId) {
        return apiService.listarPorChamado(chamadoId);
    }

    public Comentario salvar(Comentario comentario) {
        return apiService.salvar(comentario);
    }
}
