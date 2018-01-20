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
package br.gov.serpro.https;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.logging.Logger;

import org.bouncycastle.util.encoders.Base64;
import org.demoiselle.signer.core.extension.BasicCertificate;
import org.demoiselle.signer.policy.engine.factory.PolicyFactory;
import org.demoiselle.signer.policy.engine.factory.PolicyFactory.Policies;
import org.demoiselle.signer.policy.impl.cades.factory.PKCS7Factory;
import org.demoiselle.signer.policy.impl.cades.pkcs7.PKCS7Signer;

/**
 * 
 * @author Estêvão Monteiro
 * @since 15/08/2016
 * 
 */
public class UserCertificateUtils {

	private static final Logger LOGGER = Logger.getLogger(UserCertificateUtils.class.getName());

	public static BasicCertificate getICPBrasilCertificate(KeyStore keystore, String alias, boolean valid)
			throws KeyStoreException {

		LOGGER.info("getICPBrasilCertificate() - ALIAS: " + alias);

		if (alias == null || alias.equals("")) {
			alias = keystore.aliases().nextElement();
		}

		X509Certificate x509 = (X509Certificate) keystore.getCertificateChain(alias)[0];
		BasicCertificate cert = new BasicCertificate(x509);
		//LOGGER.info("Certificado >>>>" + cert.toString());
		return cert;
	}

	public static PKCS7Signer buildSigner(KeyStore keystore, String alias) throws GeneralSecurityException{
		// Parametrizando o objeto signer
		PKCS7Signer signer = PKCS7Factory.getInstance().factory();
		//signer = PKCS7Factory.getInstance().factoryDefault();
		configSigner(signer, keystore, alias);
		return signer;
	}
	
	private static void configSigner(PKCS7Signer signer, KeyStore keyStore, String alias)
			throws GeneralSecurityException{
		
		// Especificando a politica a ser utilizada. Para mais detalhes, consulte a documentação do security-signer
		Policies signaturePolicy = PolicyFactory.Policies.AD_RB_CADES_2_2;
		signer.setSignaturePolicy(signaturePolicy);
		
		signer.setPrivateKey((PrivateKey) keyStore.getKey(alias, null));
		
		signer.setCertificates(keyStore.getCertificateChain(alias));
		
		signer.setProvider(keyStore.getProvider());
	}

	public static byte[] sign(byte[] content, PKCS7Signer signer) throws GeneralSecurityException{
		return signer.doAttachedSign(content);
	}
	
	public static byte[] signHash(byte[] content, PKCS7Signer signer) throws GeneralSecurityException{
		return signer.doHashSign(content);
	}
	
	public static byte[] sign(byte[] content, KeyStore keystore, String alias) throws GeneralSecurityException{
		return sign(content, buildSigner(keystore, alias));
	}
	
	public static byte[] sign(String textToSign, KeyStore keystore, String alias)
			throws GeneralSecurityException, UnsupportedEncodingException {
		
		LOGGER.info("Assinando texto: \"" + textToSign + "\"");
		byte[] content = textToSign.getBytes("UTF-8");
		return sign(content, keystore, alias);
	}

	public static byte[] sign(String textToSign, PKCS7Signer signer)
			throws GeneralSecurityException, UnsupportedEncodingException {
		
		LOGGER.info("Assinando texto: \"" + textToSign + "\"");
		byte[] content = textToSign.getBytes("UTF-8");
		return sign(content, signer);
	}
	
	public static String signToBase64(String textToSign, KeyStore keystore, String alias)
			throws GeneralSecurityException, UnsupportedEncodingException {

		String assinado = new String(Base64.encode(sign(textToSign, keystore, alias)));
		LOGGER.info("Texto assinado: " + assinado);
		return assinado;
	}

	public static String signToBase64(byte[] content, PKCS7Signer signer)
			throws GeneralSecurityException {

		String assinado = new String(Base64.encode(sign(content, signer)));
		LOGGER.info("Texto assinado: " + assinado);
		return assinado;
	}

	public static String signHashToBase64(byte[] content, PKCS7Signer signer)
			throws GeneralSecurityException {

		String assinado = new String(Base64.encode(signHash(content, signer)));
		LOGGER.info("Texto assinado: " + assinado);
		return assinado;
	}

	public static String signToBase64(byte[] content, KeyStore keystore, String alias)
			throws GeneralSecurityException {

		String assinado = new String(Base64.encode(sign(content, keystore, alias)));
		LOGGER.info("Texto assinado: " + assinado);
		return assinado;
	}

	/**
	 * Verifica se o CPF ou CNPJ do certificado recebido para assinatura está consistente com o mesmo que estabeleceu a
	 * conexão segura (HTTPS) com o sistema.
	 * 
	 * @param cpf
	 * @param cnpj
	 * @param certificado
	 * @param applet
	 * @return
	 */
	public static void validaIdentidade(String cpf, String cnpj, BasicCertificate certificado) {

		if (cpf != null && cpf.trim().length() > 0) {
			cpf = cpf.replace("-", "").replace(".", "");

			if (certificado.getICPBRCertificatePF() == null
					|| !cpf.equals(certificado.getICPBRCertificatePF().getCPF())) {
				throw new IllegalArgumentException(
						"Certificado escolhido inválido. Selecione o certificado digital do CPF: " + cpf);
			}

		} else if (cnpj != null && cnpj.trim().length() > 0) {
			cnpj = cnpj.replace("-", "").replace(".", "").replace("/", "");

			boolean pessoaInvalida=true;            


			if (certificado.getICPBRCertificatePJ() != null){
				String raizCnpj = cnpj.substring(0,8);
				pessoaInvalida = !certificado.getICPBRCertificatePJ().getCNPJ().startsWith(raizCnpj);
				LOGGER.info("Raiz:" + raizCnpj + "---");
				LOGGER.info("CNPJ:" + certificado.getICPBRCertificatePJ().getCNPJ() +"---");
			}

			if (pessoaInvalida) {
				throw new IllegalArgumentException(
						"Certificado escolhido inválido. Selecione o certificado digital do CNPJ: " + cnpj);
			}

		}
	}

	public static void validaIdentidade(String cpf, String cnpj, KeyStore keystore, String alias)
			throws KeyStoreException {
		validaIdentidade(cpf, cnpj, getICPBrasilCertificate(keystore, alias, false));
	}

}
