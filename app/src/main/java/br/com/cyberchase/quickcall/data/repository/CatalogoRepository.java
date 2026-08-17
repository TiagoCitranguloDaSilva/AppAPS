package br.com.cyberchase.quickcall.data.repository;

import java.util.ArrayList;
import java.util.List;

import br.com.cyberchase.quickcall.data.service.CategoriaApiService;
import br.com.cyberchase.quickcall.model.Categoria;
import br.com.cyberchase.quickcall.model.Prioridade;
import br.com.cyberchase.quickcall.model.StatusChamado;

public class CatalogoRepository {

    private final CategoriaApiService categoriaApiService = new CategoriaApiService();

    public List<Categoria> listarCategoriasPadrao() {
        return categoriaApiService.listarTodas();
    }

    public List<Prioridade> listarPrioridadesPadrao() {
        List<Prioridade> prioridades = new ArrayList<>();
        prioridades.add(new Prioridade(1L, "Baixa", 72, "#4CAF50"));
        prioridades.add(new Prioridade(2L, "Media", 48, "#8BC34A"));
        prioridades.add(new Prioridade(3L, "Alta", 24, "#FFC107"));
        prioridades.add(new Prioridade(4L, "Urgente", 8, "#FF9800"));
        prioridades.add(new Prioridade(5L, "Critica", 4, "#F44336"));
        return prioridades;
    }

    public List<StatusChamado> listarStatusPadrao() {
        List<StatusChamado> status = new ArrayList<>();
        status.add(new StatusChamado(1L, "Aberto", "Chamado criado", 1));
        status.add(new StatusChamado(2L, "Em andamento", "Chamado em atendimento", 2));
        status.add(new StatusChamado(3L, "Concluido", "Chamado finalizado", 3));
        status.add(new StatusChamado(4L, "Cancelado", "Chamado cancelado", 4));
        return status;
    }
}
