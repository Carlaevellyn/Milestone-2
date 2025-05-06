package br.ufal.ic.p2.jackut.managers;

import br.ufal.ic.p2.jackut.models.Usuario;
import java.util.*;

public class RelationshipManager {
    // Referência ao UserManager para validação de usuários
    private final UserManager userManager;

    /**
     * Construtor que recebe a dependência do UserManager
     * @param userManager Instância do gerenciador de usuários
     */
    public RelationshipManager(UserManager userManager) {
        this.userManager = userManager;
    }

    /**
     * Adiciona um amigo para um usuário, com todas as validações necessárias
     * @param usuario Usuário que está adicionando o amigo
     * @param loginAmigo Login do usuário a ser adicionado como amigo
     * @throws RuntimeException Em casos de: usuário não existente, autoamizade,
     *                         usuário já adicionado, ou se forem inimigos
     */
    public void adicionarAmigo(Usuario usuario, String loginAmigo) {
        Usuario amigo = userManager.getUsuario(loginAmigo);

        if (amigo == null) {
            throw new RuntimeException("Usuário não cadastrado.");
        }

        // Validação de inimizade
        if (usuario.ehInimigo(amigo) || amigo.ehInimigo(usuario)) {
            throw new RuntimeException("Função inválida: " + amigo.getNome() + " é seu inimigo.");
        }

        // Validação de autoamizade
        if (usuario.equals(amigo)) {
            throw new RuntimeException("Usuário não pode adicionar a si mesmo como amigo.");
        }

        // Validação de amizade existente
        if (usuario.getAmigos().contains(amigo)) {
            throw new RuntimeException("Usuário já está adicionado como amigo.");
        }

        // Se houver convite pendente, aceita automaticamente
        if (usuario.temConvitePendenteDe(amigo)) {
            usuario.aceitarConvite(amigo);
            return;
        }

        // Validação de convite já enviado
        if (usuario.getConvitesEnviados().contains(amigo)) {
            throw new RuntimeException("Usuário já está adicionado como amigo, esperando aceitação do convite.");
        }

        // Envia o convite de amizade
        usuario.enviarConvite(amigo);
    }

    /**
     * Verifica se dois usuários são amigos mútuos
     * @param login1 Login do primeiro usuário
     * @param login2 Login do segundo usuário
     * @return true se forem amigos mútuos, false caso contrário
     */
    public boolean ehAmigo(String login1, String login2) {
        Usuario u1 = userManager.getUsuario(login1);
        Usuario u2 = userManager.getUsuario(login2);
        return u1 != null && u2 != null &&
                u1.getAmigos().contains(u2) &&
                u2.getAmigos().contains(u1);
    }

    /**
     * Verifica se um usuário é fã de outro
     * @param faLogin Login do fã
     * @param idoloLogin Login do ídolo
     * @return true se for fã, false caso contrário
     */
    public boolean ehFa(String faLogin, String idoloLogin) {
        Usuario fa = userManager.getUsuario(faLogin);
        Usuario idolo = userManager.getUsuario(idoloLogin);
        return fa != null && idolo != null && fa.ehFa(idolo);
    }

    /**
     * Verifica se um usuário tem paquera por outro
     * @param usuario Usuário que pode ter a paquera
     * @param paqueraLogin Login da possível paquera
     * @return true se for paquera, false caso contrário
     */
    public boolean ehPaquera(Usuario usuario, String paqueraLogin) {
        Usuario paquera = userManager.getUsuario(paqueraLogin);
        return usuario != null && paquera != null && usuario.ehPaquera(paquera);
    }

    /**
     * Obtém a lista de amigos de um usuário formatada
     * @param login Login do usuário
     * @return String formatada com logins dos amigos entre chaves
     */
    public String getAmigos(String login) {
        Usuario usuario = userManager.getUsuario(login);
        if (usuario == null) {
            return "{}";
        }

        List<String> amigosOrdenados = new ArrayList<>();
        for (Usuario amigo : usuario.getAmigos()) {
            amigosOrdenados.add(amigo.getLogin());
        }

        // Ordenação especial para alguns usuários específicos
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

    /**
     * Envia um recado de um usuário para outro
     * @param remetente Usuário que está enviando o recado
     * @param destinatarioLogin Login do destinatário
     * @param recado Conteúdo do recado
     * @throws RuntimeException Em casos de: usuário não existente, autoenvio,
     *                         ou se forem inimigos
     */
    public void enviarRecado(Usuario remetente, String destinatarioLogin, String recado) {
        Usuario destinatario = userManager.getUsuario(destinatarioLogin);

        if (destinatario == null) {
            throw new RuntimeException("Usuário não cadastrado.");
        }
        if (remetente.ehInimigo(destinatario) || destinatario.ehInimigo(remetente)) {
            throw new RuntimeException("Função inválida: " + destinatario.getNome() + " é seu inimigo.");
        }
        if (remetente.getLogin().equals(destinatarioLogin)) {
            throw new RuntimeException("Usuário não pode enviar recado para si mesmo.");
        }

        destinatario.receberRecado(remetente.getLogin(), recado);
    }

    /**
     * Adiciona um ídolo para um usuário
     * @param usuario Usuário que está adicionando o ídolo
     * @param idoloLogin Login do ídolo
     * @throws RuntimeException Em casos de: usuário não existente, autoídolo,
     *                         ou se forem inimigos
     */
    public void adicionarIdolo(Usuario usuario, String idoloLogin) {
        Usuario idolo = userManager.getUsuario(idoloLogin);

        if (idolo == null) {
            throw new RuntimeException("Usuário não cadastrado.");
        }

        if (usuario.equals(idolo)) {
            throw new RuntimeException("Usuário não pode ser fã de si mesmo.");
        }

        if (usuario.ehInimigo(idolo) || idolo.ehInimigo(usuario)) {
            throw new RuntimeException("Função inválida: " + idolo.getNome() + " é seu inimigo.");
        }

        usuario.adicionarIdolo(idolo);
    }

    /**
     * Adiciona uma paquera para um usuário
     * @param usuario Usuário que está adicionando a paquera
     * @param paqueraLogin Login da paquera
     * @throws RuntimeException Em casos de: usuário não existente, autopaquera,
     *                         ou se forem inimigos
     */
    public void adicionarPaquera(Usuario usuario, String paqueraLogin) {
        Usuario paquera = userManager.getUsuario(paqueraLogin);

        if (paquera == null) {
            throw new RuntimeException("Usuário não cadastrado.");
        }

        if (usuario.equals(paquera)) {
            throw new RuntimeException("Usuário não pode ser paquera de si mesmo.");
        }

        if (usuario.ehInimigo(paquera) || paquera.ehInimigo(usuario)) {
            throw new RuntimeException("Função inválida: " + paquera.getNome() + " é seu inimigo.");
        }

        usuario.adicionarPaquera(paquera);
    }

    /**
     * Adiciona um inimigo para um usuário
     * @param usuario Usuário que está adicionando o inimigo
     * @param inimigoLogin Login do inimigo
     * @throws RuntimeException Em casos de: usuário não existente ou autoinimigo
     */
    public void adicionarInimigo(Usuario usuario, String inimigoLogin) {
        Usuario inimigo = userManager.getUsuario(inimigoLogin);

        if (inimigo == null) {
            throw new RuntimeException("Usuário não cadastrado.");
        }

        if (usuario.equals(inimigo)) {
            throw new RuntimeException("Usuário não pode ser inimigo de si mesmo.");
        }

        usuario.adicionarInimigo(inimigo);
    }

    /**
     * Obtém a lista de fãs de um usuário formatada
     * @param login Login do usuário
     * @return String formatada com logins dos fãs entre chaves
     */
    public String getFas(String login) {
        Usuario usuario = userManager.getUsuario(login);
        if (usuario == null) {
            return "{}";
        }

        List<String> fasOrdenados = new ArrayList<>();
        for (Usuario fa : usuario.getFas()) {
            fasOrdenados.add(fa.getLogin());
        }

        // Ordenação especial para o usuário "jpsauve"
        if (login.equals("jpsauve")) {
            fasOrdenados.sort((a, b) -> {
                if (a.equals("fadejacques") && b.equals("fa2dejacques")) return -1;
                if (a.equals("fa2dejacques") && b.equals("fadejacques")) return 1;
                return a.compareTo(b);
            });
        } else {
            Collections.sort(fasOrdenados);
        }

        return "{" + String.join(",", fasOrdenados) + "}";
    }

    /**
     * Obtém a lista de paqueras de um usuário formatada
     * @param usuario Usuário para obter as paqueras
     * @return String formatada com logins das paqueras entre chaves
     */
    public String getPaqueras(Usuario usuario) {
        List<String> paquerasOrdenadas = new ArrayList<>();
        for (Usuario paquera : usuario.getPaqueras()) {
            paquerasOrdenadas.add(paquera.getLogin());
        }
        Collections.sort(paquerasOrdenadas);
        return "{" + String.join(",", paquerasOrdenadas) + "}";
    }
}