// Classe Perfil.java - Representa o perfil de um usuário, armazenando atributos personalizados
package br.ufal.ic.p2.jackut.models;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class Perfil implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Map<String, String> atributos = new HashMap<>();

    // Adiciona um atributo ao perfil do usuário
    public void adicionarAtributo(String chave, String valor) {
        if (chave == null || chave.isEmpty()) {
            throw new RuntimeException("Atributo não preenchido.");
        }
        atributos.put(chave, valor);
    }

    // Retorna o valor de um atributo específico
    public String getAtributo(String chave) {
        return atributos.get(chave);
    }
}
