package br.com.cyberchase.quickcall.data.repository.storage;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;

import br.com.cyberchase.quickcall.model.Chamado;
import br.com.cyberchase.quickcall.model.Comentario;
import br.com.cyberchase.quickcall.model.HistoricoChamado;
import br.com.cyberchase.quickcall.model.TipoPerfil;
import br.com.cyberchase.quickcall.model.Usuario;

public final class JsonMapper {

    private JsonMapper() {
    }

    public static JSONObject toJson(Usuario usuario) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("id", usuario.getId());
        json.put("nome", usuario.getNome());
        json.put("email", usuario.getEmail());
        json.put("senhaHash", usuario.getSenhaHash());
        json.put("telefone", usuario.getTelefone());
        json.put("ativo", usuario.isAtivo());
        json.put("dataCriacao", toStringValue(usuario.getDataCriacao()));
        json.put("tipoPerfil", usuario.getTipoPerfil() == null ? JSONObject.NULL : usuario.getTipoPerfil().name());
        json.put("setorId", nullableLong(usuario.getSetorId()));
        json.put("matriculaOuRegistro", nullableString(usuario.getMatriculaOuRegistro()));
        json.put("especialidade", nullableString(usuario.getEspecialidade()));
        json.put("nivelTecnico", nullableString(usuario.getNivelTecnico()));
        json.put("disponivel", usuario.isDisponivel());
        json.put("nivelAcesso", nullableInteger(usuario.getNivelAcesso()));
        return json;
    }

    public static Usuario toUsuario(JSONObject json) throws JSONException {
        Usuario usuario = new Usuario();
        usuario.setId(json.getLong("id"));
        usuario.setNome(json.optString("nome", null));
        usuario.setEmail(json.optString("email", null));
        usuario.setSenhaHash(json.optString("senhaHash", null));
        usuario.setTelefone(json.optString("telefone", null));
        usuario.setAtivo(json.optBoolean("ativo", true));
        usuario.setDataCriacao(parseDateTime(json.optString("dataCriacao", null)));
        String tipoPerfil = json.optString("tipoPerfil", null);
        usuario.setTipoPerfil(tipoPerfil == null || "null".equals(tipoPerfil) ? null : TipoPerfil.valueOf(tipoPerfil));
        usuario.setSetorId(json.isNull("setorId") ? null : json.getLong("setorId"));
        usuario.setMatriculaOuRegistro(json.isNull("matriculaOuRegistro") ? null : json.getString("matriculaOuRegistro"));
        usuario.setEspecialidade(json.isNull("especialidade") ? null : json.getString("especialidade"));
        usuario.setNivelTecnico(json.isNull("nivelTecnico") ? null : json.getString("nivelTecnico"));
        usuario.setDisponivel(json.optBoolean("disponivel", false));
        usuario.setNivelAcesso(json.isNull("nivelAcesso") ? null : json.getInt("nivelAcesso"));
        return usuario;
    }

    public static JSONObject toJson(Chamado chamado) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("id", chamado.getId());
        json.put("titulo", chamado.getTitulo());
        json.put("descricao", chamado.getDescricao());
        json.put("dataAbertura", toStringValue(chamado.getDataAbertura()));
        json.put("dataFechamento", toStringValue(chamado.getDataFechamento()));
        json.put("solicitanteId", nullableLong(chamado.getSolicitanteId()));
        json.put("tecnicoResponsavelId", nullableLong(chamado.getTecnicoResponsavelId()));
        json.put("categoriaId", nullableLong(chamado.getCategoriaId()));
        json.put("prioridadeId", nullableLong(chamado.getPrioridadeId()));
        json.put("statusId", nullableLong(chamado.getStatusId()));
        json.put("equipamentoId", nullableLong(chamado.getEquipamentoId()));
        json.put("setorId", nullableLong(chamado.getSetorId()));
        json.put("solucaoFinal", nullableString(chamado.getSolucaoFinal()));
        return json;
    }

    public static Chamado toChamado(JSONObject json) throws JSONException {
        Chamado chamado = new Chamado();
        chamado.setId(json.getLong("id"));
        chamado.setTitulo(json.optString("titulo", null));
        chamado.setDescricao(json.optString("descricao", null));
        chamado.setDataAbertura(parseDateTime(json.optString("dataAbertura", null)));
        chamado.setDataFechamento(parseDateTime(json.optString("dataFechamento", null)));
        chamado.setSolicitanteId(json.isNull("solicitanteId") ? null : json.getLong("solicitanteId"));
        chamado.setTecnicoResponsavelId(json.isNull("tecnicoResponsavelId") ? null : json.getLong("tecnicoResponsavelId"));
        chamado.setCategoriaId(json.isNull("categoriaId") ? null : json.getLong("categoriaId"));
        chamado.setPrioridadeId(json.isNull("prioridadeId") ? null : json.getLong("prioridadeId"));
        chamado.setStatusId(json.isNull("statusId") ? null : json.getLong("statusId"));
        chamado.setEquipamentoId(json.isNull("equipamentoId") ? null : json.getLong("equipamentoId"));
        chamado.setSetorId(json.isNull("setorId") ? null : json.getLong("setorId"));
        chamado.setSolucaoFinal(json.isNull("solucaoFinal") ? null : json.getString("solucaoFinal"));
        return chamado;
    }

    public static JSONObject toJson(Comentario comentario) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("id", comentario.getId());
        json.put("chamadoId", nullableLong(comentario.getChamadoId()));
        json.put("autorId", nullableLong(comentario.getAutorId()));
        json.put("texto", nullableString(comentario.getTexto()));
        json.put("dataHora", toStringValue(comentario.getDataHora()));
        json.put("interno", comentario.isInterno());
        return json;
    }

    public static Comentario toComentario(JSONObject json) throws JSONException {
        Comentario comentario = new Comentario();
        comentario.setId(json.getLong("id"));
        comentario.setChamadoId(json.isNull("chamadoId") ? null : json.getLong("chamadoId"));
        comentario.setAutorId(json.isNull("autorId") ? null : json.getLong("autorId"));
        comentario.setTexto(json.isNull("texto") ? null : json.getString("texto"));
        comentario.setDataHora(parseDateTime(json.optString("dataHora", null)));
        comentario.setInterno(json.optBoolean("interno", false));
        return comentario;
    }

    public static JSONObject toJson(HistoricoChamado historico) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("id", historico.getId());
        json.put("chamadoId", nullableLong(historico.getChamadoId()));
        json.put("usuarioId", nullableLong(historico.getUsuarioId()));
        json.put("acao", nullableString(historico.getAcao()));
        json.put("valorAnterior", nullableString(historico.getValorAnterior()));
        json.put("valorNovo", nullableString(historico.getValorNovo()));
        json.put("dataHora", toStringValue(historico.getDataHora()));
        return json;
    }

    public static HistoricoChamado toHistorico(JSONObject json) throws JSONException {
        HistoricoChamado historico = new HistoricoChamado();
        historico.setId(json.getLong("id"));
        historico.setChamadoId(json.isNull("chamadoId") ? null : json.getLong("chamadoId"));
        historico.setUsuarioId(json.isNull("usuarioId") ? null : json.getLong("usuarioId"));
        historico.setAcao(json.isNull("acao") ? null : json.getString("acao"));
        historico.setValorAnterior(json.isNull("valorAnterior") ? null : json.getString("valorAnterior"));
        historico.setValorNovo(json.isNull("valorNovo") ? null : json.getString("valorNovo"));
        historico.setDataHora(parseDateTime(json.optString("dataHora", null)));
        return historico;
    }

    private static Object nullableLong(Long value) {
        return value == null ? JSONObject.NULL : value;
    }

    private static Object nullableInteger(Integer value) {
        return value == null ? JSONObject.NULL : value;
    }

    private static Object nullableString(String value) {
        return value == null ? JSONObject.NULL : value;
    }

    private static String toStringValue(LocalDateTime value) {
        return value == null ? null : value.toString();
    }

    private static LocalDateTime parseDateTime(String value) {
        if (value == null || value.isBlank() || "null".equals(value)) {
            return null;
        }
        return LocalDateTime.parse(value);
    }
}
