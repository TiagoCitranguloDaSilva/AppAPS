package br.com.cyberchase.quickcall.data.repository;

import android.content.Context;

import java.util.List;

import br.com.cyberchase.quickcall.data.service.ChamadoApiService;
import br.com.cyberchase.quickcall.model.Chamado;

public class ChamadoRepository {

    private final ChamadoApiService apiService;

    public ChamadoRepository(Context context) {
        this.apiService = new ChamadoApiService();
    }

    public List<Chamado> listarTodos() {
        return apiService.listarTodos();
    }

    public List<Chamado> listarPorSolicitante(long solicitanteId) {
        return apiService.listarPorSolicitante(solicitanteId);
    }

    public Chamado buscarPorId(long id) {
        return apiService.buscarPorId(id);
    }

    public Chamado salvar(Chamado chamado) {
        return apiService.salvar(chamado);
    }

    public boolean remover(long id) {
        return apiService.deletar(id);
    }
}
