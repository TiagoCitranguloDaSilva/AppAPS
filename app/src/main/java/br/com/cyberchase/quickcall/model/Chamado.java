package br.com.cyberchase.quickcall.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Chamado {

    private Long id;
    private String titulo;
    private String descricao;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataFechamento;
    private Long solicitanteId;
    private Long tecnicoResponsavelId;
    private Long categoriaId;
    private Long prioridadeId;
    private Long statusId;
    private Long equipamentoId;
    private Long setorId;
    private String solucaoFinal;

    public Chamado() {
        this.dataAbertura = LocalDateTime.now();
    }

    public Chamado(Long id, String titulo, String descricao, LocalDateTime dataAbertura,
                   LocalDateTime dataFechamento, Long solicitanteId, Long tecnicoResponsavelId,
                   Long categoriaId, Long prioridadeId, Long statusId, Long equipamentoId,
                   Long setorId, String solucaoFinal) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.dataAbertura = dataAbertura;
        this.dataFechamento = dataFechamento;
        this.solicitanteId = solicitanteId;
        this.tecnicoResponsavelId = tecnicoResponsavelId;
        this.categoriaId = categoriaId;
        this.prioridadeId = prioridadeId;
        this.statusId = statusId;
        this.equipamentoId = equipamentoId;
        this.setorId = setorId;
        this.solucaoFinal = solucaoFinal;
    }

    public void abrir(Long solicitanteId, Long categoriaId, Long prioridadeId, Long statusId) {
        this.solicitanteId = solicitanteId;
        this.categoriaId = categoriaId;
        this.prioridadeId = prioridadeId;
        this.statusId = statusId;
        this.dataAbertura = LocalDateTime.now();
        this.dataFechamento = null;
        this.solucaoFinal = null;
    }

    public void atribuirTecnico(Long tecnicoResponsavelId) {
        this.tecnicoResponsavelId = tecnicoResponsavelId;
    }

    public void alterarStatus(Long statusId) {
        this.statusId = statusId;
    }

    public void alterarPrioridade(Long prioridadeId) {
        this.prioridadeId = prioridadeId;
    }

    public void registrarSolucao(String solucaoFinal) {
        this.solucaoFinal = solucaoFinal;
    }

    public void fechar(String solucaoFinal) {
        this.solucaoFinal = solucaoFinal;
        this.dataFechamento = LocalDateTime.now();
    }

    public void reabrir() {
        this.dataFechamento = null;
    }

    public long calcularTempoAtendimentoEmHoras() {
        LocalDateTime fim = dataFechamento != null ? dataFechamento : LocalDateTime.now();
        return Duration.between(dataAbertura, fim).toHours();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDateTime dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public LocalDateTime getDataFechamento() {
        return dataFechamento;
    }

    public void setDataFechamento(LocalDateTime dataFechamento) {
        this.dataFechamento = dataFechamento;
    }

    public Long getSolicitanteId() {
        return solicitanteId;
    }

    public void setSolicitanteId(Long solicitanteId) {
        this.solicitanteId = solicitanteId;
    }

    public Long getTecnicoResponsavelId() {
        return tecnicoResponsavelId;
    }

    public void setTecnicoResponsavelId(Long tecnicoResponsavelId) {
        this.tecnicoResponsavelId = tecnicoResponsavelId;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public Long getPrioridadeId() {
        return prioridadeId;
    }

    public void setPrioridadeId(Long prioridadeId) {
        this.prioridadeId = prioridadeId;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public Long getEquipamentoId() {
        return equipamentoId;
    }

    public void setEquipamentoId(Long equipamentoId) {
        this.equipamentoId = equipamentoId;
    }

    public Long getSetorId() {
        return setorId;
    }

    public void setSetorId(Long setorId) {
        this.setorId = setorId;
    }

    public String getSolucaoFinal() {
        return solucaoFinal;
    }

    public void setSolucaoFinal(String solucaoFinal) {
        this.solucaoFinal = solucaoFinal;
    }
}
