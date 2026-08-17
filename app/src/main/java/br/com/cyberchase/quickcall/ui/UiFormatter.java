package br.com.cyberchase.quickcall.ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import br.com.cyberchase.quickcall.model.Categoria;
import br.com.cyberchase.quickcall.model.Prioridade;
import br.com.cyberchase.quickcall.model.StatusChamado;

public final class UiFormatter {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private UiFormatter() {
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "-";
        }
        return DATE_TIME_FORMATTER.format(dateTime);
    }

    public static String findCategoriaNome(List<Categoria> categorias, Long categoriaId) {
        if (categoriaId == null) {
            return "-";
        }
        for (Categoria categoria : categorias) {
            if (categoria.getId() != null && categoria.getId().equals(categoriaId)) {
                return categoria.getNome();
            }
        }
        return "-";
    }

    public static String findPrioridadeNome(List<Prioridade> prioridades, Long prioridadeId) {
        if (prioridadeId == null) {
            return "-";
        }
        for (Prioridade prioridade : prioridades) {
            if (prioridade.getId() != null && prioridade.getId().equals(prioridadeId)) {
                return prioridade.getNome();
            }
        }
        return "-";
    }

    public static String findStatusNome(List<StatusChamado> statusChamados, Long statusId) {
        if (statusId == null) {
            return "-";
        }
        for (StatusChamado statusChamado : statusChamados) {
            if (statusChamado.getId() != null && statusChamado.getId().equals(statusId)) {
                return statusChamado.getNome();
            }
        }
        return "-";
    }
}
