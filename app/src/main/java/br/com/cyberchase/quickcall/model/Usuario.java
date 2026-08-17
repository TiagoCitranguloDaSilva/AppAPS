package br.com.cyberchase.quickcall.model;

import java.time.LocalDateTime;

public class Usuario {

    private Long id;
    private String nome;
    private String email;
    private String senhaHash;
    private String telefone;
    private boolean ativo;
    private LocalDateTime dataCriacao;
    private TipoPerfil tipoPerfil;
    private Long setorId;
    private String matriculaOuRegistro;
    private String especialidade;
    private String nivelTecnico;
    private boolean disponivel;
    private Integer nivelAcesso;

    public Usuario() {
        this.ativo = true;
        this.disponivel = false;
        this.dataCriacao = LocalDateTime.now();
    }

    public Usuario(Long id, String nome, String email, String senhaHash, String telefone,
                   boolean ativo, LocalDateTime dataCriacao, TipoPerfil tipoPerfil,
                   Long setorId, String matriculaOuRegistro, String especialidade,
                   String nivelTecnico, boolean disponivel, Integer nivelAcesso) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
        this.telefone = telefone;
        this.ativo = ativo;
        this.dataCriacao = dataCriacao;
        this.tipoPerfil = tipoPerfil;
        this.setorId = setorId;
        this.matriculaOuRegistro = matriculaOuRegistro;
        this.especialidade = especialidade;
        this.nivelTecnico = nivelTecnico;
        this.disponivel = disponivel;
        this.nivelAcesso = nivelAcesso;
    }

    public boolean login(String emailInformado, String senhaHashInformada) {
        return ativo
                && email != null
                && senhaHash != null
                && email.equals(emailInformado)
                && senhaHash.equals(senhaHashInformada);
    }

    public void logout() {
        // Mantido como ponto de extensao para controle de sessao.
    }

    public void alterarSenha(String novaSenhaHash) {
        this.senhaHash = novaSenhaHash;
    }

    public void atualizarDados(String nome, String email, String telefone) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
    }

    public boolean podeAbrirChamado() {
        return tipoPerfil == TipoPerfil.SOLICITANTE;
    }

    public boolean podeAtenderChamado() {
        return tipoPerfil == TipoPerfil.TECNICO;
    }

    public boolean podeGerenciarSistema() {
        return tipoPerfil == TipoPerfil.ADMINISTRADOR;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public TipoPerfil getTipoPerfil() {
        return tipoPerfil;
    }

    public void setTipoPerfil(TipoPerfil tipoPerfil) {
        this.tipoPerfil = tipoPerfil;
    }

    public Long getSetorId() {
        return setorId;
    }

    public void setSetorId(Long setorId) {
        this.setorId = setorId;
    }

    public String getMatriculaOuRegistro() {
        return matriculaOuRegistro;
    }

    public void setMatriculaOuRegistro(String matriculaOuRegistro) {
        this.matriculaOuRegistro = matriculaOuRegistro;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    public String getNivelTecnico() {
        return nivelTecnico;
    }

    public void setNivelTecnico(String nivelTecnico) {
        this.nivelTecnico = nivelTecnico;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }

    public Integer getNivelAcesso() {
        return nivelAcesso;
    }

    public void setNivelAcesso(Integer nivelAcesso) {
        this.nivelAcesso = nivelAcesso;
    }
}
