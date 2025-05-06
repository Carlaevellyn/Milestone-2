/**
 * Gerencia todas as operações relacionadas a usuários no sistema.
 * <p>
 * Responsável por criar, remover e buscar usuários, mantendo um registro centralizado
 * de todos os usuários cadastrados.
 * </p>
 */
package br.ufal.ic.p2.jackut.managers;

import br.ufal.ic.p2.jackut.models.Usuario;
import java.util.*;

public class UserManager {
    // Mapa que armazena todos os usuários do sistema, indexados por login
    private final Map<String, Usuario> usuarios = new HashMap<>();

    /**
     * Cria um novo usuário no sistema
     * @param login Identificador único do usuário
     * @param senha Senha do usuário
     * @param nome Nome real do usuário
     * @throws RuntimeException Se login/senha forem inválidos ou se o login já existir
     */
    public void criarUsuario(String login, String senha, String nome) {
        // Validações dos parâmetros
        if (login == null || login.isEmpty()) {
            throw new RuntimeException("Login inválido.");
        }
        if (senha == null || senha.isEmpty()) {
            throw new RuntimeException("Senha inválida.");
        }
        // Verifica se o usuário já existe
        if (usuarios.containsKey(login)) {
            throw new RuntimeException("Conta com esse nome já existe.");
        }
        // Cria e armazena o novo usuário
        usuarios.put(login, new Usuario(login, senha, nome));
    }

    /**
     * Obtém um usuário pelo login
     * @param login Login do usuário a ser buscado
     * @return Objeto Usuario ou null se não encontrado
     */
    public Usuario getUsuario(String login) {
        return usuarios.get(login);
    }

    /**
     * Verifica se um usuário existe no sistema
     * @param login Login do usuário a ser verificado
     * @return true se o usuário existe, false caso contrário
     */
    public boolean containsUsuario(String login) {
        return usuarios.containsKey(login);
    }

    /**
     * Remove um usuário do sistema e todas as suas referências
     * @param usuario Usuário a ser removido
     */
    public void removerUsuario(Usuario usuario) {
        String login = usuario.getLogin();

        // Remove todas as referências ao usuário em outros usuários
        for (Usuario outro : usuarios.values()) {
            outro.getAmigos().remove(usuario);
            outro.getConvitesRecebidos().remove(usuario);
            outro.getConvitesEnviados().remove(usuario);
            outro.getIdolos().remove(usuario);
            outro.getFas().remove(usuario);
            outro.getPaqueras().remove(usuario);
            outro.inimigos.remove(usuario);
            outro.limparRecadosDoUsuario(login);
        }

        // Remove o usuário do mapa principal
        usuarios.remove(login);
    }

    /**
     * Limpa todos os usuários do sistema (reinicialização)
     */
    public void clear() {
        usuarios.clear();
    }

    /**
     * Obtém todos os usuários do sistema
     * @return Mapa de usuários (login -> Usuario)
     */
    public Map<String, Usuario> getUsuarios() {
        return usuarios;
    }
}
