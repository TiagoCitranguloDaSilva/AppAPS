package br.com.cyberchase.quickcall.model;
public class StatusChamado {

    private Long id;
    private String nome;
    private String descricao;
    private int ordemFluxo;

    public StatusChamado() {
    }

    public StatusChamado(Long id, String nome, String descricao, int ordemFluxo) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.ordemFluxo = ordemFluxo;
    }

    public boolean podeIrPara(StatusChamado proximoStatus) {
        return proximoStatus != null && proximoStatus.getOrdemFluxo() >= this.ordemFluxo;
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getOrdemFluxo() {
        return ordemFluxo;
    }

    public void setOrdemFluxo(int ordemFluxo) {
        this.ordemFluxo = ordemFluxo;
    }
}
