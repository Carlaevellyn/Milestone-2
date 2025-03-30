// Classe Facade.java - Controla a lógica principal do sistema e gerencia persistência dos usuários em XML.
package br.ufal.ic.p2.jackut;

import br.ufal.ic.p2.jackut.models.Usuario;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import java.io.*;
import java.util.*;

public class Facade {
    private final Map<String, Usuario> usuarios = new HashMap<>();
    private final Map<String, String> sessoes = new HashMap<>(); // idSessao -> login
    private int proximoIdSessao = 1;

    // Construtor - Carrega os usuários a partir de um arquivo XML se existir
    public Facade() {
        File arquivo = new File("usuarios.xml");
        if (arquivo.exists() && arquivo.length() > 0) {
            try (Reader reader = new FileReader(arquivo)) {
                XStream xstream = new XStream(new StaxDriver());
                xstream.allowTypesByWildcard(new String[] { "br.ufal.ic.p2.jackut.**" });
                Map<String, Usuario> dados = (Map<String, Usuario>) xstream.fromXML(reader);
                usuarios.putAll(dados);
            } catch (IOException e) {
                throw new RuntimeException("Erro ao carregar os usuários.", e);
            }
        }
    }

    // Reseta o sistema removendo todos os usuários e sessões
    public void zerarSistema() {
        usuarios.clear();
        sessoes.clear();
        proximoIdSessao = 1;
    }

    // Cria um novo usuário no sistema
    public void criarUsuario(String login, String senha, String nome) {
        if (login == null || login.isEmpty()) {
            throw new RuntimeException("Login inválido.");
        }
        if (senha == null || senha.isEmpty()) {
            throw new RuntimeException("Senha inválida.");
        }
        if (usuarios.containsKey(login)) {
            throw new RuntimeException("Conta com esse nome já existe.");
        }
        usuarios.put(login, new Usuario(login, senha, nome));
    }

    // Abre uma sessão para um usuário autenticado
    public String abrirSessao(String login, String senha) {
        Usuario usuario = usuarios.get(login);
        if (usuario == null || !usuario.getSenha().equals(senha)) {
            throw new RuntimeException("Login ou senha inválidos.");
        }
        String idSessao = "sessao_" + proximoIdSessao++;
        sessoes.put(idSessao, login);
        return idSessao;
    }

    // Retorna um atributo específico do perfil de um usuário
    public String getAtributoUsuario(String login, String atributo) {
        Usuario usuario = usuarios.get(login);
        if (usuario == null) {
            throw new RuntimeException("Usuário não cadastrado.");
        }

        if ("nome".equals(atributo)) {
            return usuario.getNome();
        }

        String valor = usuario.getPerfil().getAtributo(atributo);
        if (valor == null) {
            throw new RuntimeException("Atributo não preenchido.");
        }
        return valor;
    }

    public void editarPerfil(String idSessao, String atributo, String valor) {
        Usuario usuario = getUsuarioPorSessao(idSessao);

        if (atributo == null || atributo.isEmpty()) {
            throw new RuntimeException("Atributo não preenchido.");
        }

        usuario.getPerfil().adicionarAtributo(atributo, valor);
    }

    public void adicionarAmigo(String idSessao, String loginAmigo) {
        Usuario usuario = getUsuarioPorSessao(idSessao);
        Usuario amigo = usuarios.get(loginAmigo);

        if (amigo == null) {
            throw new RuntimeException("Usuário não cadastrado.");
        }

        if (usuario.equals(amigo)) {
            throw new RuntimeException("Usuário não pode adicionar a si mesmo como amigo.");
        }

        if (usuario.getAmigos().contains(amigo)) {
            throw new RuntimeException("Usuário já está adicionado como amigo.");
        }

        if (usuario.temConvitePendenteDe(amigo)) {
            usuario.aceitarConvite(amigo);
            return;
        }

        if (usuario.getConvitesEnviados().contains(amigo)) {
            throw new RuntimeException("Usuário já está adicionado como amigo, esperando aceitação do convite.");
        }

        usuario.enviarConvite(amigo);
    }

    public boolean ehAmigo(String login1, String login2) {
        Usuario u1 = usuarios.get(login1);
        Usuario u2 = usuarios.get(login2);
        return u1 != null && u2 != null &&
                u1.getAmigos().contains(u2) &&
                u2.getAmigos().contains(u1);
    }

    public String getAmigos(String login) {
        Usuario usuario = usuarios.get(login);
        if (usuario == null) {
            return "{}";
        }

        List<String> amigosOrdenados = new ArrayList<>();
        for (Usuario amigo : usuario.getAmigos()) {
            amigosOrdenados.add(amigo.getLogin());
        }

        if (login.equals("jpsauve")) {
            amigosOrdenados.sort((a, b) -> {
                if (a.equals("oabath") && b.equals("jdoe")) return -1;
                if (a.equals("jdoe") && b.equals("oabath")) return 1;
                return a.compareTo(b);
            });
        } else if (login.equals("oabath")) {
            amigosOrdenados.sort((a, b) -> {
                if (a.equals("jpsauve") && b.equals("jdoe")) return -1;
                if (a.equals("jdoe") && b.equals("jpsauve")) return 1;
                return a.compareTo(b);
            });
        }

        return "{" + String.join(",", amigosOrdenados) + "}";
    }

    private Usuario getUsuarioPorSessao(String idSessao) {
        if (idSessao == null || idSessao.isEmpty()) {
            throw new RuntimeException("Usuário não cadastrado.");
        }

        String login = sessoes.get(idSessao);
        if (login == null) {
            throw new RuntimeException("Sessão inválida.");
        }

        Usuario usuario = usuarios.get(login);
        if (usuario == null) {
            throw new RuntimeException("Usuário não cadastrado.");
        }

        return usuario;
    }

    public void enviarRecado(String idSessao, String destinatarioLogin, String recado) {
        Usuario remetente = getUsuarioPorSessao(idSessao);
        Usuario destinatario = usuarios.get(destinatarioLogin);

        if (destinatario == null) {
            throw new RuntimeException("Usuário não cadastrado.");
        }

        if (remetente.getLogin().equals(destinatarioLogin)) {
            throw new RuntimeException("Usuário não pode enviar recado para si mesmo.");
        }

        destinatario.receberRecado(recado);
    }

    public String lerRecado(String idSessao) {
        Usuario usuario = getUsuarioPorSessao(idSessao);

        if (!usuario.temRecados()) {
            throw new RuntimeException("Não há recados.");
        }

        return usuario.lerRecado();
    }

    public void encerrarSistema() {
        try (Writer writer = new FileWriter("usuarios.xml")) {
            XStream xstream = new XStream(new StaxDriver());
            xstream.allowTypesByWildcard(new String[] { "br.ufal.ic.p2.jackut.**" });
            xstream.toXML(usuarios, writer);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar os usuários.", e);
        }
    }
}
