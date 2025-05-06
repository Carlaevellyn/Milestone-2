/**
 * Representa o perfil personalizável de um usuário, armazenando atributos adicionais.
 * <p>
 * Permite que cada usuário tenha um perfil único com informações extras além dos dados básicos.
 * Os atributos são armazenados como pares chave-valor.
 * </p>
 */
package br.ufal.ic.p2.jackut.models;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Perfil implements Serializable {
    // Controle de versão para serialização
    @Serial
    private static final long serialVersionUID = 1L;

    // Mapa que armazena os atributos do perfil (chave-valor)
    private final Map<String, String> atributos = new HashMap<>();

    /**
     * Adiciona ou atualiza um atributo no perfil do usuário
     * @param chave Nome do atributo (não pode ser nulo ou vazio)
     * @param valor Valor do atributo
     * @throws RuntimeException Se a chave for nula ou vazia
     */
    public void adicionarAtributo(String chave, String valor) {
        // Validação do parâmetro chave
        if (chave == null || chave.isEmpty()) {
            throw new RuntimeException("Atributo não preenchido.");
        }
        // Armazena o atributo no mapa
        atributos.put(chave, valor);
    }

    /**
     * Obtém o valor de um atributo do perfil
     * @param chave Nome do atributo a ser recuperado
     * @return O valor do atributo ou null se não existir
     */
    public String getAtributo(String chave) {
        return atributos.get(chave);
    }
}