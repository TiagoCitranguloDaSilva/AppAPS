package br.com.cyberchase.quickcall.model;
public class Prioridade {

    private Long id;
    private String nome;
    private int tempoLimiteHoras;
    private String corIndicadora;

    public Prioridade() {
    }

    public Prioridade(Long id, String nome, int tempoLimiteHoras, String corIndicadora) {
        this.id = id;
        this.nome = nome;
        this.tempoLimiteHoras = tempoLimiteHoras;
        this.corIndicadora = corIndicadora;
    }

    public void alterarTempoLimite(int tempoLimiteHoras) {
        this.tempoLimiteHoras = tempoLimiteHoras;
    }

    public void definirCor(String corIndicadora) {
        this.corIndicadora = corIndicadora;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getTempoLimiteHoras() {
        return tempoLimiteHoras;
    }

    public void setTempoLimiteHoras(int tempoLimiteHoras) {
        this.tempoLimiteHoras = tempoLimiteHoras;
    }

    public String getCorIndicadora() {
        return corIndicadora;
    }

    public void setCorIndicadora(String corIndicadora) {
        this.corIndicadora = corIndicadora;
    }
}
