package br.ufal.ic.p2.jackut;

import easyaccept.EasyAccept;
import java.io.File;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        File pasta = new File("tests");

        // Verifica se a pasta "tests" existe
        if (!pasta.exists() || !pasta.isDirectory()) {
            System.out.println("A pasta 'tests/' não existe ou não é um diretório.");
            return;
        }

        // Lista todos os arquivos da pasta "tests" que terminam com ".txt"
        File[] arquivos = pasta.listFiles((dir, name) -> name.endsWith(".txt"));

        // Se não houver arquivos, sai do programa
        if (arquivos == null || arquivos.length == 0) {
            System.out.println("Nenhum arquivo de teste encontrado na pasta 'tests/'.");
            return;
        }

        // Ordena os arquivos para garantir que sejam executados na ordem correta
        Arrays.sort(arquivos);

        // Executa os testes para cada arquivo encontrado
        for (File arquivo : arquivos) {
            String caminhoRelativo = "tests/" + arquivo.getName();
            String[] easyAcceptArgs = { "br.ufal.ic.p2.jackut.Facade", caminhoRelativo };
            System.out.println("Executando teste: " + caminhoRelativo);
            EasyAccept.main(easyAcceptArgs);
        }
        System.out.println("Todos os testes foram executados.");
    }
}
