package br.ufal.ic.p2.jackut.models;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

public class Comunidade implements Serializable {
    // Controle de versão para serialização
    private static final long serialVersionUID = 1L;

    // Atributos básicos da comunidade (imutáveis após criação)
    private final String nome;
    private final String descricao;
    private final Usuario dono;

    // Conjunto de membros da comunidade (mantém ordem de inserção)
    private final Set<Usuario> membros;

    /**
     * Construtor - Cria uma nova comunidade
     * @param nome Nome da comunidade
     * @param descricao Descrição da comunidade
     * @param dono Usuário criador da comunidade (torna-se primeiro membro)
     */
    public Comunidade(String nome, String descricao, Usuario dono) {
        this.nome = nome;
        this.descricao = descricao;
        this.dono = dono;
        this.membros = new LinkedHashSet<>();
        this.membros.add(dono); // O dono é automaticamente adicionado como membro
    }

    /**
     * Retorna o nome da comunidade
     * @return Nome da comunidade
     */
    public String getNome() {
        return nome;
    }

    /**
     * Retorna a descrição da comunidade
     * @return Descrição da comunidade
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * Retorna o dono da comunidade
     * @return Objeto Usuario representando o dono
     */
    public Usuario getDono() {
        return dono;
    }

    /**
     * Retorna uma cópia do conjunto de membros da comunidade
     * @return Conjunto de membros (cópia para proteção do encapsulamento)
     */
    public Set<Usuario> getMembros() {
        return new LinkedHashSet<>(membros);
    }

    /**
     * Adiciona um novo membro à comunidade
     * @param usuario Usuário a ser adicionado
     * @throws RuntimeException Se o usuário já for membro da comunidade
     */
    public void adicionarMembro(Usuario usuario) {
        if (!membros.add(usuario)) {
            throw new RuntimeException("Usuario já faz parte dessa comunidade.");
        }
    }
}
