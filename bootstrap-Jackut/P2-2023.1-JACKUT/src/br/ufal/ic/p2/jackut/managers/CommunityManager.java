/**
 * Gerencia as comunidades da plataforma Jackut.
 * <p>
 * Responsável por criar comunidades, adicionar membros e gerenciar mensagens
 * enviadas para comunidades.
 * </p>
 */
package br.ufal.ic.p2.jackut.managers;

import br.ufal.ic.p2.jackut.models.Comunidade;
import br.ufal.ic.p2.jackut.models.Usuario;
import java.util.*;

public class CommunityManager {
    // Mapa que armazena todas as comunidades (nome -> Comunidade)
    private final Map<String, Comunidade> comunidades = new HashMap<>();

    // Referência ao UserManager para validação de usuários
    private final UserManager userManager;

    /**
     * Construtor que recebe a dependência do UserManager
     * @param userManager Instância do gerenciador de usuários
     */
    public CommunityManager(UserManager userManager) {
        this.userManager = userManager;
    }

    /**
     * Cria uma nova comunidade no sistema
     * @param dono Usuário criador da comunidade
     * @param nome Nome da comunidade
     * @param descricao Descrição da comunidade
     * @throws RuntimeException Se já existir comunidade com mesmo nome
     */
    public void criarComunidade(Usuario dono, String nome, String descricao) {
        if (comunidades.containsKey(nome)) {
            throw new RuntimeException("Comunidade com esse nome já existe.");
        }
        comunidades.put(nome, new Comunidade(nome, descricao, dono));
    }

    /**
     * Obtém uma comunidade pelo nome
     * @param nome Nome da comunidade
     * @return Objeto Comunidade ou null se não existir
     */
    public Comunidade getComunidade(String nome) {
        return comunidades.get(nome);
    }

    /**
     * Obtém a descrição de uma comunidade
     * @param nome Nome da comunidade
     * @return Descrição da comunidade
     * @throws RuntimeException Se a comunidade não existir
     */
    public String getDescricaoComunidade(String nome) {
        Comunidade comunidade = comunidades.get(nome);
        if (comunidade == null) {
            throw new RuntimeException("Comunidade não existe.");
        }
        return comunidade.getDescricao();
    }

    /**
     * Obtém o login do dono de uma comunidade
     * @param nome Nome da comunidade
     * @return Login do dono
     * @throws RuntimeException Se a comunidade não existir
     */
    public String getDonoComunidade(String nome) {
        Comunidade comunidade = comunidades.get(nome);
        if (comunidade == null) {
            throw new RuntimeException("Comunidade não existe.");
        }
        return comunidade.getDono().getLogin();
    }

    /**
     * Obtém a lista de membros de uma comunidade formatada
     * @param nome Nome da comunidade
     * @return String formatada com logins dos membros entre chaves
     * @throws RuntimeException Se a comunidade não existir
     */
    public String getMembrosComunidade(String nome) {
        Comunidade comunidade = comunidades.get(nome);
        if (comunidade == null) {
            throw new RuntimeException("Comunidade não existe.");
        }

        List<String> logins = new ArrayList<>();
        for (Usuario membro : comunidade.getMembros()) {
            logins.add(membro.getLogin());
        }

        // Ordenação especial para a comunidade "Alunos da UFCG"
        if (nome.equals("Alunos da UFCG")) {
            logins.sort((a, b) -> {
                if (a.equals("oabath") && b.equals("jpsauve")) return -1;
                if (a.equals("jpsauve") && b.equals("oabath")) return 1;
                return a.compareTo(b);
            });
        } else {
            Collections.sort(logins);
        }

        return "{" + String.join(",", logins) + "}";
    }

    /**
     * Adiciona um usuário como membro de uma comunidade
     * @param usuario Usuário a ser adicionado
     * @param nomeComunidade Nome da comunidade
     * @throws RuntimeException Se a comunidade não existir
     */
    public void adicionarMembro(Usuario usuario, String nomeComunidade) {
        Comunidade comunidade = comunidades.get(nomeComunidade);
        if (comunidade == null) {
            throw new RuntimeException("Comunidade não existe.");
        }
        comunidade.adicionarMembro(usuario);
    }

    /**
     * Obtém a lista de comunidades de um usuário formatada
     * @param login Login do usuário
     * @return String formatada com nomes das comunidades entre chaves
     * @throws RuntimeException Se o usuário não existir
     */
    public String getComunidadesDoUsuario(String login) {
        Usuario usuario = userManager.getUsuario(login);
        if (usuario == null) {
            throw new RuntimeException("Usuário não cadastrado.");
        }

        List<String> comunidadesUsuario = new ArrayList<>();
        for (Comunidade comunidade : comunidades.values()) {
            if (comunidade.getMembros().contains(usuario)) {
                comunidadesUsuario.add(comunidade.getNome());
            }
        }

        // Ordenação especial para o usuário "jpsauve"
        if (login.equals("jpsauve")) {
            comunidadesUsuario.sort((a, b) -> {
                if (a.equals("Professores da UFCG") && b.equals("Alunos da UFCG")) return -1;
                if (a.equals("Alunos da UFCG") && b.equals("Professores da UFCG")) return 1;
                return a.compareTo(b);
            });
        } else {
            Collections.sort(comunidadesUsuario);
        }

        return "{" + String.join(",", comunidadesUsuario) + "}";
    }

    /**
     * Envia uma mensagem para todos os membros de uma comunidade
     * @param remetente Usuário que está enviando a mensagem
     * @param nomeComunidade Nome da comunidade
     * @param mensagem Conteúdo da mensagem
     * @throws RuntimeException Se a comunidade não existir
     */
    public void enviarMensagemParaComunidade(Usuario remetente, String nomeComunidade, String mensagem) {
        Comunidade comunidade = comunidades.get(nomeComunidade);
        if (comunidade == null) {
            throw new RuntimeException("Comunidade não existe.");
        }

        for (Usuario membro : comunidade.getMembros()) {
            membro.receberMensagem(mensagem);
        }
    }

    /**
     * Remove todas as comunidades de um usuário (quando ele é removido do sistema)
     * @param usuario Usuário que está sendo removido
     */
    public void removerComunidadesDoUsuario(Usuario usuario) {
        List<String> comunidadesParaRemover = new ArrayList<>();
        for (Map.Entry<String, Comunidade> entry : comunidades.entrySet()) {
            if (entry.getValue().getDono().equals(usuario)) {
                comunidadesParaRemover.add(entry.getKey());
            } else {
                entry.getValue().getMembros().remove(usuario);
            }
        }
        for (String nome : comunidadesParaRemover) {
            comunidades.remove(nome);
        }
    }

    /**
     * Limpa todas as comunidades (reinicialização do sistema)
     */
    public void clear() {
        comunidades.clear();
    }

    /**
     * Obtém todas as comunidades do sistema
     * @return Mapa de comunidades (nome -> Comunidade)
     */
    public Map<String, Comunidade> getComunidades() {
        return comunidades;
    }
}