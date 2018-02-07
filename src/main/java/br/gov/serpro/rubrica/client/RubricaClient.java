/*
 * RubricaClient
 * Copyright (C) 2018 SERPRO
 * ----------------------------------------------------------------------------
 * RubricaClient is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License version 3
 * along with this program; if not,  see <http://www.gnu.org/licenses/>
 * or write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA  02110-1301, USA.
 * ----------------------------------------------------------------------------
 * RubricaClient é um software livre; você pode redistribuí-lo e/ou
 * modificá-lo dentro dos termos da GNU LGPL versão 3 publicada pela Fundação
 * do Software Livre (FSF).
 *
 * Este programa é distribuído com a esperança que possa ser útil, mas SEM GARANTIA
 * ALGUMA; sem sequer uma garantia implícita de ADEQUAÇÃO a qualquer MERCADO ou
 * APLICAÇÃO ESPECÍFICA. Veja a Licença Pública Geral GNU/LGPL em português
 * para mais detalhes.
 *
 * Você deve ter recebido uma cópia da GNU LGPL versão 3, sob o título
 * "LICENCA.txt", junto com esse programa. Caso contrário, acesse 
 * <http://www.gnu.org/licenses/> ou escreva para a Fundação do Software Livre (FSF) 
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02111-1301, USA.
 */
package br.gov.serpro.rubrica.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.ProviderException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;
import org.bouncycastle.util.encoders.Base64;
import org.demoiselle.signer.core.extension.BasicCertificate;
import org.demoiselle.signer.core.keystore.loader.configuration.Configuration;
import org.demoiselle.signer.jnlp.util.AuthorizationException;
import org.demoiselle.signer.jnlp.util.ConectionException;
import org.demoiselle.signer.policy.impl.cades.SignerAlgorithmEnum;
import org.demoiselle.signer.policy.impl.cades.pkcs7.PKCS7Signer;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.gov.serpro.https.HttpClientUtils;
import br.gov.serpro.https.ServerCertificateUtils;
import br.gov.serpro.https.UserCertificateUtils;

/**
 * Esta classe implementa as regras de negócio do RubricaClient através da interface ControladorJanela.
 * @author Estêvão Monteiro
 * @since 10/08/16
 * 
 */
public class RubricaClient implements ControladorJanela {
	
	private static final Logger L = Logger.getLogger(RubricaClient.class);

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
		getDadosSessao();
	}

	/*
	 * (non-Javadoc)
	 * @see br.gov.serpro.dnit.rubrica.client.ControladorJanela#execute(java.security.KeyStore, java.lang.String)
	 */
	@Override
	public void execute(KeyStore keystore, String alias) {

		L.info("Microsoft CryptoAPI está "
				+ (Configuration.getInstance().isMSCapiDisabled() ? "indisponível" : "disponível"));

		try {
			verificarCertificado(keystore, alias);

			PKCS7Signer signer = UserCertificateUtils.buildSigner(keystore, alias);
			SignerAlgorithmEnum algoritmo = UserCertificateUtils.getAlgoritmo(signer);

			//Varrendo todos os arquivos, gera uma assinatura para cada arquivo
			int assinados = 0;
			for (AssinaturaRemota doc : this.selecao.getAssinaturasRemotas()) {
				L.info("Assinando documento: " + doc);

				byte[] hash = getHash(doc, algoritmo, signer);
				if(hash == null) {
					continue;
				}
				String assinatura = null;
				try {
					//TODO testar co-assinatura
					assinatura = UserCertificateUtils.signHashToBase64(hash, signer);
				}
				catch(ProviderException e) {
					Throwable t = e.getCause();
					if(t == null) t = e;
					falharCritico(t, "Falha no driver do token: ");
				}
				doc.setAssinatura(assinatura);
				doc.setDescricaoArquivo(forcarUtf8(doc.getDescricaoArquivo()));
				assinados++;
			}
			if(assinados == 0) {
				falharCritico("Nenhum documento foi assinado");
			}

			String json = this.jsonConv.writeValueAsString(this.selecao);
			L.info("Enviar " + json);

			String contentType = "application/json;charset=UTF-8";
			//String accept = "application/json";
			try {
				HttpClientUtils.request(this.servicoPost, "POST", json, contentType, contentType, this.tokenSessao);
			}
			catch(ConectionException ce) {
				//A descrição pode dar problema de encoding no Windows.
				Throwable c = ce.getCause();
				if(c != null && c instanceof ConectionException 
						&& c.getMessage().contains("Invalid UTF-8 middle byte")) {
					L.warn(c.getMessage());
					for (AssinaturaRemota doc : this.selecao.getAssinaturasRemotas()) {
						//O servidor não precisa dessa informação.
						doc.setDescricaoArquivo(null);
					}
					json = this.jsonConv.writeValueAsString(this.selecao);
					L.info("Enviar " + json);
					HttpClientUtils.request(this.servicoPost, "POST", json, contentType, contentType, this.tokenSessao);
				}
				else {
					throw ce;
				}
			}

			String ok = "Sucesso na assinatura digital. O Assinador será encerrado.";
			L.info(ok);
			JOptionPane.showMessageDialog(this.janela, ok, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
			System.exit(0);

		}
		catch (Throwable t) {
			System.out.println();
			t.printStackTrace();
			falhar(t.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see br.gov.serpro.dnit.rubrica.client.ControladorJanela#filtrarCertificados(java.security.KeyStore)
	 */
	@Override
	public AbstractTableModel filtrarCertificados(KeyStore keystore) {
		if ((this.cpf == null || this.cpf.isEmpty()) && (this.cnpj == null || this.cnpj.isEmpty())) {
			falharCritico("Falha ao obter CPF ou CNPJ do usuário.");
		}
		CertificadoFiltradoModel certificateModel = new CertificadoFiltradoModel();
		certificateModel.populate(keystore, this.cpf, this.cnpj);
		if (certificateModel.getRowCount() == 0) {
			String msg = "Não foi encontrado no sistema um certificado com o CPF ou CNPJ ";
			msg += (this.cpf == null || this.cpf.isEmpty() ? this.cnpj : this.cpf);
			msg += ". Verifique se o token ou smartcard foi removido e tente executar o Assinador novamente.";
			falharCritico(msg);
		}
		return certificateModel;
	}

	/*
	 * (non-Javadoc)
	 * @see br.gov.serpro.dnit.rubrica.client.ControladorJanela#cancel(java.security.KeyStore, java.lang.String)
	 */
	@Override
	public void cancel(KeyStore keystore, String alias) {
		this.janela.setVisible(false);
		this.janela.dispose();
	}

	/*
	 * (non-Javadoc)
	 * @see br.gov.serpro.dnit.rubrica.client.ControladorJanela#close()
	 */
	@Override
	public void close() {
	}

	/**
	 * A lista de arquivos que será exibida na tela. Não faz seleção, todos os itens serão assinados.
	 */
	/*
	 * (non-Javadoc)
	 * @see br.gov.serpro.dnit.rubrica.client.ControladorJanela#getArquivos()
	 */
	@Override
	public List<String> getArquivos() {
		return this.listaDocumentos;
	}

	/*
	 * (non-Javadoc)
	 * @see br.gov.serpro.dnit.rubrica.client.ControladorJanela#getText()
	 */
	@Override
	public String getText() {
		return null;
	}

	public void setFrame(JFrame janela) {
		this.janela = janela;
	}

	private void configurar() {
		this.tokenSessao = System.getProperty(JNLP_TOKEN);
		this.servicoGet = System.getProperty(JNLP_GET);
		this.servicoPost = System.getProperty(JNLP_POST);

		L.info("jnlp.token: " + this.tokenSessao);
		L.info("jnlp.get: " + this.servicoGet);
		L.info("jnlp.post: " + this.servicoPost);

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

	private void getDadosSessao() {
		try {
			String parametros = HttpClientUtils.getJson(this.servicoGet, this.tokenSessao);
			if(parametros == null || parametros.isEmpty()) {
				falharCritico("Usuário não possui seleção válida de documentos a assinar.");
			}

			this.jsonConv = new ObjectMapper();
			this.selecao = this.jsonConv.readValue(parametros, SelecaoAssinaturaRemota.class);

			String cpf = this.selecao.getCpfAssinante();
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
				if(doc.getHash256() == null || doc.getHash256().trim().isEmpty()
						|| doc.getHash512() == null || doc.getHash512().trim().isEmpty()) {
					continue;
				}
				this.listaDocumentos.add(doc.getDescricaoArquivo());
			}

			if(this.listaDocumentos.isEmpty()) {
				falharCritico("Erro de parâmetros do servidor: nenhum documento com hash");
			}

		}
		catch (AuthorizationException e) {
			falharCritico(e, "Erro de sessão: ");
		}
		catch (ConectionException e) {
			falharCritico(e, "Erro de conexão: ");
		}
		catch (ClassCastException | IOException e) {
			falharCritico(e, "Erro de parâmetros do servidor: ");
		} 
	}

	private byte[] getHash(AssinaturaRemota doc, SignerAlgorithmEnum algoritmo, PKCS7Signer signer) {
		String hashBase64;
		if(algoritmo == SignerAlgorithmEnum.SHA256withRSA) {
			if(doc.getHash256() == null) {
				L.warn("Hash não recebido!");
				return null;
			}
			L.info("Código hash a assinar: " + doc.getHash256());
			hashBase64 = doc.getHash256();
			doc.setHash512(null);//Sinalizar para o servidor remoto qual hash será usado
		}
		else if(algoritmo == SignerAlgorithmEnum.SHA512withRSA){
			if(doc.getHash512() == null) {
				L.warn("Hash não recebido!");
				return null;
			}
			L.info("Código hash a assinar: " + doc.getHash512());
			hashBase64 = doc.getHash512();
			doc.setHash256(null);//Sinalizar para o servidor remoto qual hash será usado
		}
		else {
			return null;
		}

		return Base64.decode(hashBase64);

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

	private void falhar(String mensagem) {
		L.error(mensagem);
		JOptionPane.showMessageDialog(this.janela, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
	}

	private void falharCritico(Throwable t, String preambulo) {
		System.out.println();
		t.printStackTrace();
		falharCritico(preambulo + t.getMessage());
	}

	private void falharCritico(String mensagem) {
		L.error(mensagem);
		JOptionPane.showMessageDialog(this.janela, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
		System.exit(0);
	}

    /**
     * ISO-8859-1 pode ser convertido para UTF-8, embora o inverso não seja válido.
     * Sistemas legados podem ainda estar usando ISO em vez de UTF.
     * @param texto
     * @return
     */
    private String forcarUtf8(String texto) {
    	CharsetEncoder enc = StandardCharsets.UTF_8.newEncoder();
    	if(!enc.canEncode(texto)) {
    		L.warn("Texto incompatível com UTF-8: " + texto);
    	}
    	ByteBuffer buf;
    	try {
			buf = enc.encode(CharBuffer.wrap(texto));
		}
    	catch (CharacterCodingException e) {
    		L.warn(e.getMessage());
			return new String(texto.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
		}
		byte[] bytes = new byte[buf.remaining()];
		buf.get(bytes);
		return new String(bytes, StandardCharsets.UTF_8);
    }

}