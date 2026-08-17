package br.com.cyberchase.quickcall.data.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.com.cyberchase.quickcall.model.Chamado;
import br.com.cyberchase.quickcall.network.HttpJsonClient;

public class ChamadoApiService {

    private final HttpJsonClient client = new HttpJsonClient();

    public List<Chamado> listarTodos() {
        JSONArray array = client.getArray("/chamado");
        List<Chamado> chamados = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                chamados.add(toChamado(array.getJSONObject(i)));
            } catch (JSONException ignored) {
            }
        }
        return chamados;
    }

    public List<Chamado> listarPorSolicitante(long solicitanteId) {
        List<Chamado> chamados = new ArrayList<>();
        for (Chamado chamado : listarTodos()) {
            if (chamado.getSolicitanteId() != null && chamado.getSolicitanteId() == solicitanteId) {
                chamados.add(chamado);
            }
        }
        return chamados;
    }

    public Chamado buscarPorId(long id) {
        JSONObject json = client.getObject("/chamado/" + id);
        if (json == null) {
            return null;
        }
        try {
            return toChamado(json);
        } catch (JSONException e) {
            return null;
        }
    }

    public Chamado salvar(Chamado chamado) {
        try {
            JSONObject body = new JSONObject();
            if (chamado.getId() != null) {
                body.put("id", chamado.getId());
            }
            body.put("descricao", toApiDescricao(chamado));
            body.put("nivelPrioridade", chamado.getPrioridadeId() == null ? 1 : chamado.getPrioridadeId().intValue());
            body.put("status", toStatusName(chamado.getStatusId()));
            body.put("usuarioId", chamado.getSolicitanteId());
            body.put("categoriaId", chamado.getCategoriaId());

            JSONObject json = chamado.getId() == null
                    ? client.post("/chamado", body)
                    : client.put("/chamado", body);
            return json == null ? null : toChamado(json);
        } catch (JSONException e) {
            return null;
        }
    }

    public boolean deletar(long id) {
        return client.deleteResource("/chamado/" + id);
    }

    private Chamado toChamado(JSONObject json) throws JSONException {
        Chamado chamado = new Chamado();
        chamado.setId(json.optLong("id"));
        String descricaoApi = json.optString("descricao", null);
        chamado.setTitulo(extrairTitulo(descricaoApi));
        chamado.setDescricao(extrairDescricao(descricaoApi));
        String dataAbertura = json.optString("dataAbertura", null);
        if (dataAbertura != null && !dataAbertura.isBlank() && !"null".equals(dataAbertura)) {
            chamado.setDataAbertura(LocalDateTime.parse(dataAbertura));
        }
        chamado.setSolicitanteId(json.isNull("usuarioId") ? null : json.getLong("usuarioId"));
        chamado.setCategoriaId(json.isNull("categoriaId") ? null : json.getLong("categoriaId"));
        chamado.setPrioridadeId(json.isNull("nivelPrioridade") ? null : json.getLong("nivelPrioridade"));
        chamado.setStatusId(toStatusId(json.optString("status", null)));
        return chamado;
    }

    private Long toStatusId(String status) {
        if (status == null) {
            return 1L;
        }
        switch (status) {
            case "EM_ANDAMENTO":
                return 2L;
            case "CONCLUIDO":
                return 3L;
            case "CANCELADO":
                return 4L;
            default:
                return 1L;
        }
    }

    private String toStatusName(Long statusId) {
        if (statusId == null) {
            return "ABERTO";
        }
        switch (statusId.intValue()) {
            case 2:
                return "EM_ANDAMENTO";
            case 3:
                return "CONCLUIDO";
            case 4:
                return "CANCELADO";
            default:
                return "ABERTO";
        }
    }

    private String toApiDescricao(Chamado chamado) {
        String titulo = sanitize(chamado.getTitulo());
        String descricao = sanitize(chamado.getDescricao());

        if (titulo == null) {
            return descricao;
        }

        if (descricao == null || descricao.equals(titulo)) {
            return titulo;
        }

        return titulo + "\n\n" + descricao;
    }

    private String extrairTitulo(String descricaoApi) {
        String valor = sanitize(descricaoApi);
        if (valor == null) {
            return null;
        }

        int separador = valor.indexOf("\n\n");
        return separador >= 0 ? valor.substring(0, separador).trim() : valor;
    }

    private String extrairDescricao(String descricaoApi) {
        String valor = sanitize(descricaoApi);
        if (valor == null) {
            return null;
        }

        int separador = valor.indexOf("\n\n");
        return separador >= 0 ? valor.substring(separador + 2).trim() : valor;
    }

    private String sanitize(String valor) {
        if (valor == null) {
            return null;
        }

        String normalizado = valor.trim();
        return normalizado.isEmpty() ? null : normalizado;
    }
}
