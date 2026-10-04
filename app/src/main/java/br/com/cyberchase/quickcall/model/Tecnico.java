package br.com.cyberchase.quickcall.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Tecnico: o profissional que atende os chamados.
 *
 * HERANCA: "Tecnico extends Usuario" quer dizer que todo Tecnico E um Usuario.
 * Ele ganha de graca tudo o que o Usuario tem (id, nome, email, telefone, login...)
 * e acrescenta o que e so dele: especialidade, nivel, disponibilidade e as categorias que atende.
 *
 * Segue a especificacao do projeto (ESPECIFICACAO_CONCEITUAL.md, item 6.3).
 */
public class Tecnico extends Usuario {

    private String especialidade;
    private String nivelTecnico;
    private boolean disponivel;
    private final List<Long> categoriaIds = new ArrayList<>();
    private final List<String> categoriaNomes = new ArrayList<>();

    public Tecnico() {
        super();                              // monta a parte "Usuario" primeiro
        setTipoPerfil(TipoPerfil.TECNICO);    // todo tecnico tem o perfil TECNICO
        this.disponivel = true;
    }

    // ---------- Metodos da especificacao ----------

    /** O tecnico assume o chamado: vira o responsavel por ele. */
    public void aceitarChamado(Chamado chamado) {
        chamado.atribuirTecnico(getId());
    }

    /** Muda o status de um chamado que o tecnico esta atendendo. */
    public void atualizarStatusChamado(Chamado chamado, Long novoStatusId) {
        chamado.alterarStatus(novoStatusId);
    }

    /** Registra como o problema foi resolvido. */
    public void registrarSolucao(Chamado chamado, String solucao) {
        chamado.setSolucaoFinal(solucao);
    }

    /** Cria um comentario escrito por este tecnico. */
    public Comentario adicionarComentarioTecnico(Chamado chamado, String texto) {
        Comentario comentario = new Comentario();
        comentario.setChamadoId(chamado.getId());
        comentario.setAutorId(getId());
        comentario.setTexto(texto);
        return comentario;
    }

    /** Encerra o chamado, guardando a solucao e a data de fechamento. */
    public void encerrarChamado(Chamado chamado, String solucao) {
        chamado.fechar(solucao);
    }

    /** true se este tecnico atende a categoria informada (ex.: Hardware). */
    public boolean atende(Long categoriaId) {
        return categoriaId != null && categoriaIds.contains(categoriaId);
    }

    // ---------- Getters e setters ----------

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

    public List<Long> getCategoriaIds() {
        return categoriaIds;
    }

    public List<String> getCategoriaNomes() {
        return categoriaNomes;
    }
}
