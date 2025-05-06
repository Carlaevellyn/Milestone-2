package br.ufal.ic.p2.jackut.managers;

import br.ufal.ic.p2.jackut.models.Usuario;
import java.util.*;

public class SessionManager {
    // Mapa que armazena as sessões ativas (idSessao -> login)
    private final Map<String, String> sessoes = new HashMap<>();

    // Contador para gerar IDs únicos de sessão
    private int proximoIdSessao = 1;

    // Referência ao UserManager para validação de usuários
    private final UserManager userManager;

    /**
     * Construtor que recebe a dependência do UserManager
     * @param userManager Instância do gerenciador de usuários
     */
    public SessionManager(UserManager userManager) {
        this.userManager = userManager;
    }

    /**
     * Abre uma nova sessão para um usuário
     * @param login Login do usuário
     * @param senha Senha do usuário
     * @return ID da sessão criada
     * @throws RuntimeException Se as credenciais forem inválidas
     */
    public String abrirSessao(String login, String senha) {
        // Obtém e valida o usuário
        Usuario usuario = userManager.getUsuario(login);
        if (usuario == null || !usuario.getSenha().equals(senha)) {
            throw new RuntimeException("Login ou senha inválidos.");
        }

        // Cria um ID único para a nova sessão
        String idSessao = "sessao_" + proximoIdSessao++;

        // Registra a sessão no mapa
        sessoes.put(idSessao, login);

        return idSessao;
    }

    /**
     * Obtém o usuário associado a uma sessão
     * @param idSessao ID da sessão
     * @return Objeto Usuario associado à sessão
     * @throws RuntimeException Se a sessão for inválida ou o usuário não existir
     */
    public Usuario getUsuarioPorSessao(String idSessao) {
        // Validação básica do ID da sessão
        if (idSessao == null || idSessao.isEmpty()) {
            throw new RuntimeException("Usuário não cadastrado.");
        }

        // Obtém o login associado à sessão
        String login = sessoes.get(idSessao);

        // Verifica se o usuário ainda existe no sistema
        if (login == null || !userManager.containsUsuario(login)) {
            throw new RuntimeException("Usuário não cadastrado.");
        }

        // Retorna o objeto Usuario completo
        Usuario usuario = userManager.getUsuario(login);
        if (usuario == null) {
            throw new RuntimeException("Usuário não cadastrado.");
        }

        return usuario;
    }

    /**
     * Remove todas as sessões de um usuário específico
     * @param login Login do usuário cujas sessões serão encerradas
     */
    public void removeSessoesDoUsuario(String login) {
        sessoes.values().removeIf(v -> v.equals(login));
    }

    /**
     * Limpa todas as sessões e reinicia o contador de IDs
     * (Usado para resetar o sistema)
     */
    public void clear() {
        sessoes.clear();
        proximoIdSessao = 1;
    }
}
