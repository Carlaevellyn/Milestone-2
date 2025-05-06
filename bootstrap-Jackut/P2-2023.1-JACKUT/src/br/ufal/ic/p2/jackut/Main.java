package br.ufal.ic.p2.jackut;

import easyaccept.EasyAccept;
import java.io.File;
import java.util.Arrays;

public class Main {
    /**
     * Método principal que executa os testes de aceitação
     * @param args Argumentos da linha de comando (não utilizados)
     */
    public static void main(String[] args) {
        // Cria uma referência para a pasta de testes
        File pasta = new File("tests");

        // Verifica se a pasta de testes existe e é um diretório válido
        if (!pasta.exists() || !pasta.isDirectory()) {
            System.out.println("A pasta 'tests/' não existe ou não é um diretório.");
            return;
        }

        /**
         * Lista todos os arquivos .txt na pasta de testes
         * Usa um filtro para selecionar apenas arquivos com extensão .txt
         */
        File[] arquivos = pasta.listFiles((dir, name) -> name.endsWith(".txt"));

        // Verifica se foram encontrados arquivos de teste
        if (arquivos == null || arquivos.length == 0) {
            System.out.println("Nenhum arquivo de teste encontrado na pasta 'tests/'.");
            return;
        }

        /**
         * Ordena os arquivos alfabeticamente para garantir
         * uma ordem consistente de execução dos testes
         */
        Arrays.sort(arquivos);

        /**
         * Executa os testes para cada arquivo encontrado
         * Usa o framework EasyAccept para executar os scripts de teste
         */
        for (File arquivo : arquivos) {
            // Monta o caminho relativo do arquivo de teste
            String caminhoRelativo = "tests/" + arquivo.getName();

            // Prepara os argumentos para o EasyAccept:
            // 1. A classe Facade que implementa os comandos
            // 2. O arquivo de script de teste
            String[] easyAcceptArgs = { "br.ufal.ic.p2.jackut.Facade", caminhoRelativo };

            System.out.println("Executando teste: " + caminhoRelativo);

            // Executa o EasyAccept com os argumentos configurados
            EasyAccept.main(easyAcceptArgs);
        }

        // Mensagem final indicando conclusão de todos os testes
        System.out.println("Todos os testes foram executados.");
    }
}


