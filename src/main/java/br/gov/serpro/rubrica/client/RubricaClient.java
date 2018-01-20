package br.gov.serpro.rubrica.client;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import org.bouncycastle.util.encoders.Base64;
import org.demoiselle.signer.core.extension.BasicCertificate;
import org.demoiselle.signer.core.keystore.loader.configuration.Configuration;
import org.demoiselle.signer.jnlp.util.AuthorizationException;
import org.demoiselle.signer.jnlp.util.ConectionException;
import org.demoiselle.signer.policy.impl.cades.pkcs7.PKCS7Signer;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.gov.serpro.https.HttpClientUtils;
import br.gov.serpro.https.ServerCertificateUtils;
import br.gov.serpro.https.UserCertificateUtils;

/**
 * 
 * @author Estêvão Monteiro
 * @since 10/08/16
 * 
 */
public class RubricaClient implements ControladorJanela {

    private static final Logger LOGGER = Logger.getLogger(RubricaClient.class.getName());

    //private static final long serialVersionUID = 1L;

    private static final String JNLP_TOKEN = "jnlp.token";
    private static final String JNLP_GET = "jnlp.get";
    private static final String JNLP_POST = "jnlp.post";

    static {
        ServerCertificateUtils.suspenderValidacaoCadeia();//TODO rever isto
    }

    private String cpf;
    private String cnpj;
    private String tokenSessao;
    private String servicoGet;
    private String servicoPost;
    
    private List<String> listaDocumentos;
    private SelecaoAssinaturaRemota selecao;
    private JFrame janela;
    private BasicCertificate certificado;
    
    private ObjectMapper jsonConv;
	
    public RubricaClient() {
        configurar();
        obterDadosSessao();
    }

    private void configurar() {
    	this.tokenSessao = System.getProperty(JNLP_TOKEN);
    	this.servicoGet = System.getProperty(JNLP_GET);
    	this.servicoPost = System.getProperty(JNLP_POST);

        LOGGER.info("jnlp.token: " + this.tokenSessao);
        LOGGER.info("jnlp.get: " + this.servicoGet);
        LOGGER.info("jnlp.post: " + this.servicoPost);

        if (this.tokenSessao == null || this.tokenSessao.isEmpty()) {
            falharCritico("A variável \"jnlp.token\" nao está configurada.");
        }

        if (this.servicoGet == null || this.servicoGet.isEmpty()) {
            falharCritico("A variável \"jnlp.get\" nao está configurada.");
        }

        if (this.servicoPost == null || this.servicoPost.isEmpty()) {
            falharCritico("A variável \"jnlp.post\" nao está configurada.");
        }
    }
    
    private void obterDadosSessao() {
        try {
        	String parametros = HttpClientUtils.getJson(this.servicoGet, this.tokenSessao);
        	
        	this.jsonConv = new ObjectMapper();
        	this.selecao = this.jsonConv.readValue(parametros, SelecaoAssinaturaRemota.class);
        	
        	String cpf = selecao.getCpfAssinante();
            if (cpf.length() == 11) {
                this.cpf = cpf;
            }
            else {
                falharCritico("Falha ao obter CPF da sessão do usuário.");
            }
        	
        	if(this.selecao.getAssinaturasRemotas() == null || this.selecao.getAssinaturasRemotas().isEmpty()){
        		falharCritico("Erro de parâmetros do servidor: documentos");
        	}

        	this.listaDocumentos = new ArrayList<String>();
        	
        	for(AssinaturaRemota doc : this.selecao.getAssinaturasRemotas()){
        		if(doc.getHash() == null || doc.getHash().trim().isEmpty()) {
        			continue;
        		}
        		this.listaDocumentos.add(formatarLinhaDocumento(doc));
        	}
        	
        	if(this.listaDocumentos.isEmpty()) {
        		falharCritico("Erro de parâmetros do servidor: nenhum documento com hash");
        	}

        }
        catch (AuthorizationException e) {
            falharCritico(e, "Erro de autorização: ");
        }
        catch (ConectionException e) {
            falharCritico(e, "Erro na comunicação com o servidor: ");
        }
        catch (ClassCastException | IOException e) {
        	falharCritico(e, "Erro com os parâmetros recebidos: ");
		} 
    }
    
    private String formatarLinhaDocumento(AssinaturaRemota doc){
    	//TODO já deveria vir do servidor formatado!
		String resumo = doc.getResumo().replaceAll("'", "").replaceAll(":", "").replaceAll("\"", "");
		//TODO abreviar campos, separar campos por "/"
		int i = resumo.indexOf("Empresa") +8;
		resumo = resumo.substring(1, i).toLowerCase() + resumo.substring(i, resumo.length() -1);
		return doc.getTipoDocumento() + " " + doc.getSistema() + ": " + resumo;
    }

    /*
     * (non-Javadoc)
     * @see br.gov.serpro.rubrica.client.ControladorJanela#getText()
     */
    @Override
    public String getText() {
        return null;
    }

	/**
	 * A lista de arquivos que será exibida na tela. Não faz seleção, todos os itens serão assinados.
	 */
    /*
	 * (non-Javadoc)
	 * @see br.gov.serpro.rubrica.client.ControladorJanela#getArquivos()
	 */
    @Override
	public List<String> getArquivos() {
		return this.listaDocumentos;
	}

    /*
     * (non-Javadoc)
     * @see br.gov.serpro.rubrica.client.ControladorJanela#execute(java.security.KeyStore, java.lang.String)
     */
    @Override
    public void execute(KeyStore keystore, String alias) {

        LOGGER.info("Microsoft CryptoAPI está "
                + (Configuration.getInstance().isMSCapiDisabled() ? "indisponível" : "disponível"));

        try {
        	verificarCertificado(keystore, alias);
        	
        	PKCS7Signer signer = UserCertificateUtils.buildSigner(keystore, alias);
        	
            //Varrendo todos os arquivos, gera uma assinatura para cada arquivo
        	int assinados = 0;
            for (AssinaturaRemota doc : this.selecao.getAssinaturasRemotas()) {
            	LOGGER.log(Level.INFO, "Assinando documento: " + doc);
            	if(doc.getHash() == null) {
            		LOGGER.log(Level.WARNING, "Hash não veio!");
            		continue;
            	}
            	LOGGER.info(doc.getHash());
            	//TODO co-assinatura
            	doc.setAssinatura(UserCertificateUtils.signHashToBase64(Base64.decode(doc.getHash()), signer));
            	assinados++;
            }
            if(assinados == 0) {
            	falharCritico("Nenhum documento foi assinado");
            }
            
            String json = this.jsonConv.writeValueAsString(this.selecao);
            
            String mimeJson = "application/json";
            HttpClientUtils.send(this.servicoPost, "POST", json, mimeJson, mimeJson, this.tokenSessao);
            
            String ok = "Sucesso na assinatura digital. O Assinador será encerrado.";
            LOGGER.info(ok);
            System.exit(0);

        }
        catch (Exception e) {
            e.printStackTrace();
            falhar(e.getMessage());
        }
    }
    
    private void verificarCertificado(KeyStore keystore, String alias) throws KeyStoreException{
        if (this.certificado == null) {
        	this.certificado = UserCertificateUtils.getICPBrasilCertificate(keystore, alias, false);
        }
        try {
            UserCertificateUtils.validaIdentidade(this.cpf, this.cnpj, this.certificado);
        }
        catch (IllegalArgumentException e) {
            falhar(e.getMessage());
        }
    }

    /*
     * (non-Javadoc)
     * @see br.gov.serpro.rubrica.client.ControladorJanela#cancel(java.security.KeyStore, java.lang.String)
     */
    @Override
    public void cancel(KeyStore keystore, String alias) {
    	this.janela.setVisible(false);
        this.janela.dispose();
    }

    /*
     * (non-Javadoc)
     * @see br.gov.serpro.rubrica.client.ControladorJanela#close()
     */
    @Override
    public void close() {
    }

    public void setFrame(JFrame janela) {
        this.janela = janela;
    }

    private void falharCritico(Exception e, String preambulo) {
        LOGGER.severe(e.getMessage());
        e.printStackTrace();
        JOptionPane.showMessageDialog(this.janela, preambulo + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        System.exit(0);
    }

    private void falharCritico(String mensagem) {
        falhar(mensagem);
        System.exit(0);
    }

    private void falhar(String mensagem) {
        LOGGER.severe(mensagem);
        JOptionPane.showMessageDialog(this.janela, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    public AbstractTableModel filtrarCertificados(KeyStore keystore) {
        if ((this.cpf == null || this.cpf.isEmpty()) && (this.cnpj == null || this.cnpj.isEmpty())) {
            falharCritico("Falha ao obter CPF ou CNPJ do usuário.");
        }
        CertificadoFiltradoModel certificateModel = new CertificadoFiltradoModel();
        certificateModel.populate(keystore, this.cpf, this.cnpj);
        if (certificateModel.getRowCount() == 0) {
            String msg = "Não há certificado no sistema para o identificador ";
            msg += (this.cpf == null || this.cpf.isEmpty() ? this.cnpj : this.cpf);
            msg += ". Verifique se o token ou smartcard foi removido e tente executar o Assinador novamente.";
            falharCritico(msg);
        }
        return certificateModel;
    }

    private String mockParametros() {
    	
    	String parametros = "{\"cpfAssinante\": \"99999999999\","
    			+ "\"assinaturasRemotas\": [{" 
        			+ "\"codSolicitacao\": 20,"
        			+ "\"hash\": \"GXwk+CXw5DQOXkdrybvllYfx0rqNcL2O/7NXJe2nt1k=\","
        			+ "\"tipoDocumento\": \"Oficio\","
        			+ "\"sistema\": \"Sistema\","
        			+ "\"resumo\": \"Processo: 50600.356476/2016-87, Valor: 379.959,01\""
        			+ "}]}";
    	   	return parametros;
    }

}