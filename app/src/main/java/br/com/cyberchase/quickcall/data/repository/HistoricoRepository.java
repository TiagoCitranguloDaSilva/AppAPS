package br.com.cyberchase.quickcall.data.repository;

import android.content.Context;

import java.util.List;

import br.com.cyberchase.quickcall.data.service.HistoricoApiService;
import br.com.cyberchase.quickcall.model.HistoricoChamado;

public class HistoricoRepository {

    private final HistoricoApiService apiService;

    public HistoricoRepository(Context context) {
        this.apiService = new HistoricoApiService();
    }

    public List<HistoricoChamado> listarPorChamado(long chamadoId) {
        return apiService.listarPorChamado(chamadoId);
    }

    public HistoricoChamado salvar(HistoricoChamado historico) {
        return apiService.salvar(historico);
    }
}
