package br.com.cyberchase.quickcall.model;

import java.time.LocalDateTime;

public class HistoricoChamado {

    private Long id;
    private Long chamadoId;
    private Long usuarioId;
    private String acao;
    private String valorAnterior;
    private String valorNovo;
    private LocalDateTime dataHora;

    public HistoricoChamado() {
        this.dataHora = LocalDateTime.now();
    }

    public HistoricoChamado(Long id, Long chamadoId, Long usuarioId, String acao,
                            String valorAnterior, String valorNovo, LocalDateTime dataHora) {
        this.id = id;
        this.chamadoId = chamadoId;
        this.usuarioId = usuarioId;
        this.acao = acao;
        this.valorAnterior = valorAnterior;
        this.valorNovo = valorNovo;
        this.dataHora = dataHora;
    }

    public void registrarAlteracao(String acao, String valorAnterior, String valorNovo) {
        this.acao = acao;
        this.valorAnterior = valorAnterior;
        this.valorNovo = valorNovo;
        this.dataHora = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChamadoId() {
        return chamadoId;
    }

    public void setChamadoId(Long chamadoId) {
        this.chamadoId = chamadoId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getAcao() {
        return acao;
    }

    public void setAcao(String acao) {
        this.acao = acao;
    }

    public String getValorAnterior() {
        return valorAnterior;
    }

    public void setValorAnterior(String valorAnterior) {
        this.valorAnterior = valorAnterior;
    }

    public String getValorNovo() {
        return valorNovo;
    }

    public void setValorNovo(String valorNovo) {
        this.valorNovo = valorNovo;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
}
