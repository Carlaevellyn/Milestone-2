package br.ufal.ic.p2.jackut.managers;

import br.ufal.ic.p2.jackut.models.Comunidade;
import br.ufal.ic.p2.jackut.models.Usuario;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import java.io.*;
import java.util.Map;

public class PersistenceManager {

    /**
     * Carrega os usuários armazenados no arquivo XML para o UserManager
     * @param userManager Instância do gerenciador de usuários onde os dados serão carregados
     * @throws RuntimeException Se ocorrer algum erro durante o carregamento
     */
    public void carregarUsuarios(UserManager userManager) {
        // Verifica se o arquivo de usuários existe e não está vazio
        File arquivoUsuarios = new File("usuarios.xml");
        if (arquivoUsuarios.exists() && arquivoUsuarios.length() > 0) {
            try (Reader reader = new FileReader(arquivoUsuarios)) {
                // Configura o XStream para desserialização XML
                XStream xstream = new XStream(new StaxDriver());
                // Permite todas as classes do pacote br.ufal.ic.p2.jackut
                xstream.allowTypesByWildcard(new String[] { "br.ufal.ic.p2.jackut.**" });

                // Desserializa o XML para um Map de Usuários
                Map<String, Usuario> dados = (Map<String, Usuario>) xstream.fromXML(reader);
                // Adiciona os usuários carregados ao UserManager
                userManager.getUsuarios().putAll(dados);
            } catch (IOException e) {
                throw new RuntimeException("Erro ao carregar os usuários.", e);
            }
        }
    }

    /**
     * Carrega as comunidades armazenadas no arquivo XML para o CommunityManager
     * @param communityManager Instância do gerenciador de comunidades onde os dados serão carregados
     * @throws RuntimeException Se ocorrer algum erro durante o carregamento
     */
    public void carregarComunidades(CommunityManager communityManager) {
        // Verifica se o arquivo de comunidades existe e não está vazio
        File arquivoComunidades = new File("comunidades.xml");
        if (arquivoComunidades.exists() && arquivoComunidades.length() > 0) {
            try (Reader reader = new FileReader(arquivoComunidades)) {
                // Configura o XStream para desserialização XML
                XStream xstream = new XStream(new StaxDriver());
                // Permite todas as classes do pacote br.ufal.ic.p2.jackut
                xstream.allowTypesByWildcard(new String[] { "br.ufal.ic.p2.jackut.**" });

                // Desserializa o XML para um Map de Comunidades
                Map<String, Comunidade> dados = (Map<String, Comunidade>) xstream.fromXML(reader);
                // Adiciona as comunidades carregadas ao CommunityManager
                communityManager.getComunidades().putAll(dados);
            } catch (IOException e) {
                throw new RuntimeException("Erro ao carregar as comunidades.", e);
            }
        }
    }

    /**
     * Salva os dados atuais de usuários e comunidades em arquivos XML
     * @param userManager Instância do gerenciador de usuários com os dados a serem salvos
     * @param communityManager Instância do gerenciador de comunidades com os dados a serem salvos
     * @throws RuntimeException Se ocorrer algum erro durante o salvamento
     */
    public void salvarDados(UserManager userManager, CommunityManager communityManager) {
        try (Writer writerUsuarios = new FileWriter("usuarios.xml");
             Writer writerComunidades = new FileWriter("comunidades.xml")) {

            // Configura o XStream para serialização XML
            XStream xstream = new XStream(new StaxDriver());
            // Permite todas as classes do pacote br.ufal.ic.p2.jackut
            xstream.allowTypesByWildcard(new String[] { "br.ufal.ic.p2.jackut.**" });

            // Serializa e salva os usuários em XML
            xstream.toXML(userManager.getUsuarios(), writerUsuarios);
            // Serializa e salva as comunidades em XML
            xstream.toXML(communityManager.getComunidades(), writerComunidades);

        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar os dados.", e);
        }
    }
}
