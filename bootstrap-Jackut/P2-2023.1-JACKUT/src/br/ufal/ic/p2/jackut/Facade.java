/**
 * Classe principal do sistema Jackut que atua como fachada para todas as operações.
 * <p>
 * Esta classe segue o padrão Facade, fornecendo uma interface simplificada para o sistema.
 * Centraliza todas as chamadas aos diversos managers especializados.
 * </p>
 */
package br.ufal.ic.p2.jackut;

import br.ufal.ic.p2.jackut.managers.*;
import br.ufal.ic.p2.jackut.models.Usuario;

public class Facade {
    /**
     * Inicializa o sistema e carrega os dados persistentes.
     */

    private final UserManager userManager;
    private final SessionManager sessionManager;
    private final CommunityManager communityManager;
    private final RelationshipManager relationshipManager;
    private final PersistenceManager persistenceManager;

    public Facade() {
        /**
         * Cria um novo usuário no sistema.
         *
         * @param login Identificador único do novo usuário
         * @param senha Senha do novo usuário
         * @param nome Nome completo do novo usuário
         * @throws RuntimeException Se o login já existir ou se algum parâmetro for inválido
         */

        this.userManager = new UserManager();
        this.sessionManager = new SessionManager(userManager);
        this.communityManager = new CommunityManager(userManager);
        this.relationshipManager = new RelationshipManager(userManager);
        this.persistenceManager = new PersistenceManager();

        persistenceManager.carregarUsuarios(userManager);
        persistenceManager.carregarComunidades(communityManager);
    }

    //Limpa todos os dados do sistema, reiniciando todos os managers
    public void zerarSistema() {
        userManager.clear();
        sessionManager.clear();
        communityManager.clear();
    }

    //Cria um novo usuário no sistema através do userManager
    public void criarUsuario(String login, String senha, String nome) {
        userManager.criarUsuario(login, senha, nome);

        /**
         * Adiciona um amigo para o usuário da sessão atual.
         *
         * @param idSessao ID da sessão do usuário que está adicionando
         * @param loginAmigo Login do usuário a ser adicionado como amigo
         * @throws RuntimeException Se a sessão for inválida, se os usuários forem inimigos
         *                         ou se o amigo já estiver na lista
         */
    }

    //Remove um usuário do sistema, incluindo suas comunidades e sessões
    public void removerUsuario(String idSessao) {
        Usuario usuario = sessionManager.getUsuarioPorSessao(idSessao);
        communityManager.removerComunidadesDoUsuario(usuario);
        sessionManager.removeSessoesDoUsuario(usuario.getLogin());
        userManager.removerUsuario(usuario);
    }

    //Abre uma nova sessão para o usuário e retorna um ID de sessão
    public String abrirSessao(String login, String senha) {
        return sessionManager.abrirSessao(login, senha);
    }

    //Retorna um atributo específico do perfil do usuário (nome ou outros atributos do perfil)
    public String getAtributoUsuario(String login, String atributo) {
        Usuario usuario = userManager.getUsuario(login);
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

    //Permite ao usuário editar um atributo de seu perfil
    public void editarPerfil(String idSessao, String atributo, String valor) {
        Usuario usuario = sessionManager.getUsuarioPorSessao(idSessao);

        if (atributo == null || atributo.isEmpty()) {
            throw new RuntimeException("Atributo não preenchido.");
        }

        usuario.getPerfil().adicionarAtributo(atributo, valor);
    }

    //Adiciona um amigo ao usuário atual
    public void adicionarAmigo(String idSessao, String loginAmigo) {
        Usuario usuario = sessionManager.getUsuarioPorSessao(idSessao);
        relationshipManager.adicionarAmigo(usuario, loginAmigo);
    }

    //Verifica se dois usuários são amigos
    public boolean ehAmigo(String login1, String login2) {
        return relationshipManager.ehAmigo(login1, login2);
    }

    //Verifica se um usuário é fã de outro
    public boolean ehFa(String faLogin, String idoloLogin) {
        return relationshipManager.ehFa(faLogin, idoloLogin);
    }

    //Verifica se um usuário tem uma paquera por outro
    public boolean ehPaquera(String idSessao, String paqueraLogin) {
        Usuario usuario = sessionManager.getUsuarioPorSessao(idSessao);
        return relationshipManager.ehPaquera(usuario, paqueraLogin);
    }

    //Retorna a lista de amigos de um usuário
    public String getAmigos(String login) {
        return relationshipManager.getAmigos(login);
    }

    //Envia um recado de um usuário para outro
    public void enviarRecado(String idSessao, String destinatarioLogin, String recado) {
        Usuario remetente = sessionManager.getUsuarioPorSessao(idSessao);
        relationshipManager.enviarRecado(remetente, destinatarioLogin, recado);
    }

    //Cria uma nova comunidade com o usuário atual como dono
    public void criarComunidade(String sessao, String nome, String descricao) {
        Usuario dono = sessionManager.getUsuarioPorSessao(sessao);
        communityManager.criarComunidade(dono, nome, descricao);
    }

    //Obtém a descrição de uma comunidade
    public String getDescricaoComunidade(String nome) {
        return communityManager.getDescricaoComunidade(nome);
    }

    //Obtém o dono de uma comunidade
    public String getDonoComunidade(String nome) {
        return communityManager.getDonoComunidade(nome);
    }

    //Lê o próximo recado na fila de recados do usuário
    public String lerRecado(String idSessao) {
        Usuario usuario = sessionManager.getUsuarioPorSessao(idSessao);

        if (!usuario.temRecados()) {
            throw new RuntimeException("Não há recados.");
        }

        return usuario.lerRecado();
    }

    //Obtém os membros de uma comunidade
    public String getMembrosComunidade(String nome) {
        return communityManager.getMembrosComunidade(nome);
    }

    //Adiciona o usuário atual a uma comunidade
    public void adicionarComunidade(String sessao, String nome) {
        Usuario usuario = sessionManager.getUsuarioPorSessao(sessao);
        communityManager.adicionarMembro(usuario, nome);
    }

    //Obtém as comunidades de um usuário
    public String getComunidades(String login) {
        return communityManager.getComunidadesDoUsuario(login);
    }

    //Obtém os fãs de um usuário
    public String getFas(String login) {
        return relationshipManager.getFas(login);
    }

    //Obtém as paqueras do usuário atual
    public String getPaqueras(String idSessao) {
        Usuario usuario = sessionManager.getUsuarioPorSessao(idSessao);
        return relationshipManager.getPaqueras(usuario);
    }

    //Envia uma mensagem para todos os membros de uma comunidade
    public void enviarMensagem(String idSessao, String nomeComunidade, String mensagem) {
        Usuario remetente = sessionManager.getUsuarioPorSessao(idSessao);
        communityManager.enviarMensagemParaComunidade(remetente, nomeComunidade, mensagem);
    }

    //Adiciona um ídolo ao usuário atual
    public void adicionarIdolo(String idSessao, String idoloLogin) {
        Usuario usuario = sessionManager.getUsuarioPorSessao(idSessao);
        relationshipManager.adicionarIdolo(usuario, idoloLogin);
    }

    //Adiciona uma paquera ao usuário atual
    public void adicionarPaquera(String idSessao, String paqueraLogin) {
        Usuario usuario = sessionManager.getUsuarioPorSessao(idSessao);
        relationshipManager.adicionarPaquera(usuario, paqueraLogin);
    }

    //Adiciona um inimigo ao usuário atual
    public void adicionarInimigo(String idSessao, String inimigoLogin) {
        Usuario usuario = sessionManager.getUsuarioPorSessao(idSessao);
        relationshipManager.adicionarInimigo(usuario, inimigoLogin);
    }

    //Lê a próxima mensagem na fila de mensagens do usuário
    public String lerMensagem(String idSessao) {
        Usuario usuario = sessionManager.getUsuarioPorSessao(idSessao);
        if (!usuario.temMensagens()) {
            throw new RuntimeException("Não há mensagens.");
        }
        return usuario.lerMensagem();
    }

    //Salva todos os dados do sistema antes de encerrar
    public void encerrarSistema() {
        persistenceManager.salvarDados(userManager, communityManager);
    }
}