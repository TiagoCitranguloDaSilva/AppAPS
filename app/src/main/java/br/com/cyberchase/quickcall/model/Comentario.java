package br.com.cyberchase.quickcall.model;

import java.time.LocalDateTime;

public class Comentario {

    private Long id;
    private Long chamadoId;
    private Long autorId;
    private String texto;
    private LocalDateTime dataHora;
    private boolean interno;

    public Comentario() {
        this.dataHora = LocalDateTime.now();
    }

    public Comentario(Long id, Long chamadoId, Long autorId, String texto,
                      LocalDateTime dataHora, boolean interno) {
        this.id = id;
        this.chamadoId = chamadoId;
        this.autorId = autorId;
        this.texto = texto;
        this.dataHora = dataHora;
        this.interno = interno;
    }

    public void editarTexto(String texto) {
        this.texto = texto;
    }

    public void marcarComoInterno() {
        this.interno = true;
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

    public Long getAutorId() {
        return autorId;
    }

    public void setAutorId(Long autorId) {
        this.autorId = autorId;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public boolean isInterno() {
        return interno;
    }

    public void setInterno(boolean interno) {
        this.interno = interno;
    }
}
