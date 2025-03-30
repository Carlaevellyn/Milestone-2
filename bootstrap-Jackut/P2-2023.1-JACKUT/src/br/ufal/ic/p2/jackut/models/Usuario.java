package br.ufal.ic.p2.jackut.models;

import java.io.Serializable;
import java.util.*;

public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String login;
    private final String senha;
    private final String nome;
    private final Perfil perfil;
    private final Set<Usuario> amigos = new LinkedHashSet<>();
    private final Set<Usuario> convitesEnviados = new LinkedHashSet<>();
    private final Set<Usuario> convitesRecebidos = new LinkedHashSet<>();
    private final Queue<String> recadosRecebidos = new LinkedList<>();

    public Usuario(String login, String senha, String nome) {
        this.login = login;
        this.senha = senha;
        this.nome = nome;
        this.perfil = new Perfil();
    }

    public String getLogin() { return login; }
    public String getSenha() { return senha; }
    public String getNome() { return nome; }
    public Perfil getPerfil() { return perfil; }
    public Set<Usuario> getAmigos() { return new HashSet<>(amigos); }
    public Set<Usuario> getConvitesEnviados() { return new HashSet<>(convitesEnviados); }
    public Set<Usuario> getConvitesRecebidos() { return new HashSet<>(convitesRecebidos); }

    public void enviarConvite(Usuario amigo) {
        if (!convitesEnviados.contains(amigo)) {
            convitesEnviados.add(amigo);
            amigo.receberConvite(this);
        }
    }

    public void receberConvite(Usuario amigo) {
        if (!convitesRecebidos.contains(amigo)) {
            convitesRecebidos.add(amigo);
        }
    }

    public boolean aceitarConvite(Usuario amigo) {
        if (convitesRecebidos.remove(amigo)) {
            amigos.add(amigo);
            amigo.amigos.add(this);
            amigo.convitesEnviados.remove(this);
            return true;
        }
        return false;
    }

    public boolean temConvitePendenteDe(Usuario usuario) {
        return convitesRecebidos.contains(usuario);
    }

    public boolean temRecados() {
        return !recadosRecebidos.isEmpty();
    }

    public void receberRecado(String recado) {
        recadosRecebidos.add(recado);
    }

    public String lerRecado() {
        return recadosRecebidos.poll();
    }

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
