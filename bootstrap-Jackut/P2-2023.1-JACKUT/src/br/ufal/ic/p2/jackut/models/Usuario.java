/**
 * Representa um usuário na plataforma Jackut com todas suas informações e relacionamentos.
 * <p>
 * Esta classe armazena os dados básicos do usuário (login, senha, nome), seu perfil,
 * e todos os relacionamentos com outros usuários (amigos, paqueras, ídolos, inimigos).
 * Também gerencia as mensagens e recados recebidos.
 * </p>
 *
 * @author SeuNome
 * @version 1.0
 * @since 2023-01-01
 */
package br.ufal.ic.p2.jackut.models;

import java.io.Serializable;
import java.util.*;

public class Usuario implements Serializable {
    // Controle de versão para serialização
    private static final long serialVersionUID = 1L;

    // Atributos básicos do usuário (imutáveis)
    private final String login;
    private final String senha;
    private final String nome;
    private final Perfil perfil;

    // Estruturas de dados para relacionamentos e mensagens
    private final Set<Usuario> amigos = new LinkedHashSet<>();
    private final Set<Usuario> convitesEnviados = new LinkedHashSet<>();
    private final Set<Usuario> convitesRecebidos = new LinkedHashSet<>();
    private final Queue<String> recadosRecebidos = new LinkedList<>();
    private final Queue<String> mensagensRecebidas = new LinkedList<>();
    private final Set<Usuario> idolos = new HashSet<>();
    private final Set<Usuario> fas = new HashSet<>();
    private final Set<Usuario> paqueras = new HashSet<>();
    public final Set<Usuario> inimigos = new HashSet<>();
    private final Queue<String> recadosIdentificados = new LinkedList<>();
    private final Map<String, String> autoresRecados = new LinkedHashMap<>();

    /**
     * Construtor - Inicializa o usuário com login, senha e nome
     * @param login Identificador único do usuário
     * @param senha Senha de acesso
     * @param nome Nome completo do usuário
     */
    public Usuario(String login, String senha, String nome) {
        this.login = login;
        this.senha = senha;
        this.nome = nome;
        this.perfil = new Perfil();
    }

    // Métodos getters básicos
    public String getLogin() { return login; }
    public String getSenha() { return senha; }
    public String getNome() { return nome; }
    public Perfil getPerfil() { return perfil; }

    // Métodos getters que retornam cópias defensivas
    public Set<Usuario> getAmigos() { return new HashSet<>(amigos); }
    public Set<Usuario> getConvitesEnviados() { return new HashSet<>(convitesEnviados); }
    public Set<Usuario> getConvitesRecebidos() { return new HashSet<>(convitesRecebidos); }
    public Set<Usuario> getIdolos() { return new HashSet<>(idolos); }
    public Set<Usuario> getFas() { return new HashSet<>(fas); }
    public Set<Usuario> getPaqueras() { return new HashSet<>(paqueras); }

    /**
     * Envia um convite de amizade para outro usuário
     * @param amigo Usuário que receberá o convite
     */
    public void enviarConvite(Usuario amigo) {
        if (!convitesEnviados.contains(amigo)) {
            convitesEnviados.add(amigo);
            amigo.receberConvite(this);
        }
    }

    /**
     * Recebe um convite de amizade de outro usuário
     * @param amigo Usuário que enviou o convite
     */
    public void receberConvite(Usuario amigo) {
        if (!convitesRecebidos.contains(amigo)) {
            convitesRecebidos.add(amigo);
        }
    }

    /**
     * Adiciona um ídolo à lista do usuário
     * @param idolo Usuário a ser adicionado como ídolo
     * @throws RuntimeException Se o usuário já for ídolo
     */
    public void adicionarIdolo(Usuario idolo) {
        if (idolos.contains(idolo)) {
            throw new RuntimeException("Usuário já está adicionado como ídolo.");
        }
        idolos.add(idolo);
        idolo.fas.add(this); // Adiciona como fã do ídolo
    }

    /**
     * Adiciona uma paquera à lista do usuário
     * @param paquera Usuário a ser adicionado como paquera
     * @throws RuntimeException Se o usuário já for paquera
     */
    public void adicionarPaquera(Usuario paquera) {
        if (paqueras.contains(paquera)) {
            throw new RuntimeException("Usuário já está adicionado como paquera.");
        }
        paqueras.add(paquera);

        // Verifica se é uma paquera mútua
        if (paquera.paqueras.contains(this)) {
            String recado1 = paquera.getNome() + " é seu paquera - Recado do Jackut.";
            String recado2 = this.getNome() + " é seu paquera - Recado do Jackut.";

            this.receberRecado(paquera.getLogin(), recado1);
            paquera.receberRecado(this.getLogin(), recado2);
        }
    }

    /**
     * Adiciona um inimigo à lista do usuário
     * @param inimigo Usuário a ser adicionado como inimigo
     * @throws RuntimeException Se o usuário já for inimigo
     */
    public void adicionarInimigo(Usuario inimigo) {
        if (inimigos.contains(inimigo)) {
            throw new RuntimeException("Usuário já está adicionado como inimigo.");
        }
        inimigos.add(inimigo);
    }

    /**
     * Remove todos os recados de um remetente específico
     * @param loginRemetente Login do remetente cujos recados serão removidos
     */
    public void limparRecadosDoUsuario(String loginRemetente) {
        Queue<String> novosRecados = new LinkedList<>();
        for (String recado : recadosRecebidos) {
            if (!loginRemetente.equals(autoresRecados.get(recado))) {
                novosRecados.add(recado);
            }
        }
        recadosRecebidos.clear();
        recadosRecebidos.addAll(novosRecados);
        autoresRecados.entrySet().removeIf(entry -> loginRemetente.equals(entry.getValue()));
    }

    /**
     * Aceita um convite de amizade
     * @param amigo Usuário que enviou o convite
     * @return true se o convite foi aceito com sucesso, false caso contrário
     */
    public boolean aceitarConvite(Usuario amigo) {
        if (convitesRecebidos.remove(amigo)) {
            amigos.add(amigo);
            amigo.amigos.add(this);
            amigo.convitesEnviados.remove(this);
            return true;
        }
        return false;
    }

    /**
     * Lê o próximo recado na fila
     * @return O recado ou null se não houver recados
     */
    public String lerRecado() {
        String recado = recadosRecebidos.poll();
        if (recado != null) {
            autoresRecados.remove(recado);
        }
        return recado;
    }

    /**
     * Verifica se há convites pendentes de um usuário específico
     * @param usuario Usuário a ser verificado
     * @return true se houver convite pendente, false caso contrário
     */
    public boolean temConvitePendenteDe(Usuario usuario) {
        return convitesRecebidos.contains(usuario);
    }

    /**
     * Verifica se há recados não lidos
     * @return true se houver recados, false caso contrário
     */
    public boolean temRecados() {
        return !recadosRecebidos.isEmpty();
    }

    /**
     * Recebe um novo recado
     * @param remetenteLogin Login do remetente
     * @param recado Conteúdo do recado
     */
    public void receberRecado(String remetenteLogin, String recado) {
        recadosRecebidos.add(recado);
        autoresRecados.put(recado, remetenteLogin);
    }

    /**
     * Recebe uma nova mensagem
     * @param mensagem Conteúdo da mensagem
     */
    public void receberMensagem(String mensagem) {
        mensagensRecebidas.add(mensagem);
    }

    /**
     * Verifica se há mensagens não lidas
     * @return true se houver mensagens, false caso contrário
     */
    public boolean temMensagens() {
        return !mensagensRecebidas.isEmpty();
    }

    /**
     * Verifica se o usuário é fã de outro
     * @param idolo Usuário a ser verificado como ídolo
     * @return true se for fã, false caso contrário
     */
    public boolean ehFa(Usuario idolo) {
        return idolo.fas.contains(this);
    }

    /**
     * Verifica se o usuário tem uma paquera por outro
     * @param paquera Usuário a ser verificado como paquera
     * @return true se for paquera, false caso contrário
     */
    public boolean ehPaquera(Usuario paquera) {
        return paqueras.contains(paquera);
    }

    /**
     * Verifica se o usuário tem outro como inimigo
     * @param usuario Usuário a ser verificado como inimigo
     * @return true se for inimigo, false caso contrário
     */
    public boolean ehInimigo(Usuario usuario) {
        return inimigos.contains(usuario);
    }

    /**
     * Lê a próxima mensagem na fila
     * @return A mensagem ou null se não houver mensagens
     */
    public String lerMensagem() {
        return mensagensRecebidas.poll();
    }

    // Métodos para comparar usuários (baseado no login)
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Usuario other = (Usuario) obj;
        return login.equals(other.login);
    }

    @Override
    public int hashCode() {
        return login.hashCode();
    }
}