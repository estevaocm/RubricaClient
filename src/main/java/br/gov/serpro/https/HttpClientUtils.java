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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.logging.Logger;

import org.demoiselle.signer.jnlp.util.AuthorizationException;
import org.demoiselle.signer.jnlp.util.ConectionException;

/**
 * 
 * @author Demoiselle; Estêvão Monteiro
 * @since 22/08/16
 * 
 */
public class HttpClientUtils {

	private static final Logger LOGGER = Logger.getLogger(HttpClientUtils.class.getName());
	private static final String RUNTIME_VERSION = System.getProperty("java.version");
	private static final int RUNTIME_MAJOR_VERSION = Integer.valueOf(RUNTIME_VERSION.substring(2, 3));
	private static final int BUFFER_SIZE = 4096;

	public static String request(String url, String metodo, String params, String contentType, 
			String accept, String sessao) {
		
		if(url == null || url.trim().isEmpty()) {
			throw new IllegalArgumentException("url=" + url);
		}

		if(metodo == null || metodo.trim().isEmpty()) {
			throw new IllegalArgumentException("metodo=" + metodo);
		}

		LOGGER.info(metodo + ": " + url);
		
		HttpURLConnection con = getConnection(url, sessao);
		
		if(accept != null) {
			con.setRequestProperty("Accept", accept);
		}
		
		if(contentType != null) {
			con.setRequestProperty("Content-Type", contentType);
		}
		
		try {
			con.setRequestMethod(metodo);

			if(params != null) {
				// encodedData = URLEncoder.encode(params, "UTF-8");
				con.setRequestProperty("Content-Length", String.valueOf(params.length()));
				con.setDoOutput(true);
				try(OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream())){
					wr.write(params);
					wr.flush();
				}
			}

			getResponseCode(con);
			String resposta = read(con.getInputStream());
			LOGGER.info("Resposta: " + resposta);
			return resposta;
		}
		catch (IOException e) {
			Throwable causa = e.getCause() == null ? e : e.getCause();
			throw new ConectionException(e.getMessage(), causa);
		}
		finally{
			con.disconnect();
		}
	}

	private static HttpURLConnection getConnection(String url, String sessao) {
		HttpURLConnection con = null;
		try {
			con = (HttpURLConnection) new URL(url).openConnection();
			con.setRequestProperty("Authorization", sessao);
			con.setRequestProperty("User-Agent", "Java/" + RUNTIME_VERSION);
			return con;
		} 
		catch (IOException e) {
			throw new ConectionException(e.getMessage(), e.getCause());
		}
		finally{
			if (con != null) {
				con.disconnect();
			}
		}
	}

	private static void getResponseCode(HttpURLConnection con) throws IOException {

		int responseCode = con.getResponseCode();

		String mensagem = "";
		if (responseCode >= 400) {
			mensagem = "Erro HTTP " + responseCode;
			LOGGER.severe(mensagem);
		} 
		else {
			LOGGER.info("Resposta HTTP " + responseCode);
		}

		if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED || responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
			throw new AuthorizationException("Acesso ou operação não autorizado(a).");
		}

		if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
			throw new AuthorizationException("Acesso ou operação inválido(a).");
		}

		if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
			throw new AuthorizationException("Objeto não encontrado.");
		}

		if (responseCode == HttpURLConnection.HTTP_CONFLICT) {
			throw new AuthorizationException("O objeto a inserir já existe.");
		}

		if (responseCode >= 400) {
			LOGGER.severe(read(con.getErrorStream())); // Tende a trazer HTML do container Web
			throw new ConectionException(mensagem);
		}
	}

	public static String getText(String url, String sessao) {
		return get(url, "text/plain", sessao);
	}

	public static String getJson(String url, String sessao) {
		return get(url, "application/json", sessao);
	}

	public static String get(String url, String accept, String sessao) {
		return request(url, "GET", null, null, accept, sessao);
	}

	public static String sendForm(String url, String params, String sessao, InputStream certificate, String method) {
		return request(url, method, params, "application/x-www-form-urlencoded", null, sessao);
		//ou "application/octet-stream"
	}

	public static String read(InputStream stream) throws IOException {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(stream, Charset.forName("UTF-8")))){
			StringBuilder response = new StringBuilder();
			String linha = in.readLine();
			if (linha != null) {
				response.append(linha);//Pega a primeira linha.
			}
			while ((linha = in.readLine()) != null) {
				//A partir da segunda linha, insere o separador de linha após cada uma.
				response.append(System.getProperty("line.separator"));
				response.append(linha);
			}
			return response.toString();
		} 
	}

	public static String getTls() {
		String prop = "https.protocols";
		String tls;
		if (RUNTIME_MAJOR_VERSION < 7) {
			throw new RuntimeException(
					"Favor atualizar o Java. Este assinador digital requer no mínimo a versão 7 (1.7)");
		} 
		else {
			tls = "TLSv1.2";
			System.setProperty(prop, "TLSv1,TLSv1.1," + tls);
		} 
		return tls;
	}

	/**
	 *
	 * @param content
	 *            O conteudo a ser enviado
	 * @param urlToUpload
	 *            A url para onde o conteudo sera enviado via HTTPS
	 * @param token
	 *            Token que identifica o conteudo a ser enviado	 
	 */
	public static void uploadToURL(byte[] content, String urlToUpload, String token) {
		HttpURLConnection con = getConnection(urlToUpload, token);
		try(ByteArrayInputStream in = new ByteArrayInputStream(content)){
			con.setDoOutput(true);
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/zip");
			con.setRequestProperty("Authorization", "Token " + token);

			try(OutputStream out = con.getOutputStream()){
				copy(in, out);
				out.flush();
			}
			getResponseCode(con);
		} 
		catch (IOException e) {
			throw new ConectionException(e.getMessage(), e.getCause());
		}
		finally{
			con.disconnect();
		}
	}

	public static byte[] downloadFromUrl(String urlToDownload, String token) {
		HttpURLConnection con = getConnection(urlToDownload, token);
		con.setRequestProperty("Authorization", "Token " + token);
		con.setRequestProperty("Accept", "application/zip");
		try{
			con.setRequestMethod("GET");
			getResponseCode(con);
			try(InputStream in = con.getInputStream();
					ByteArrayOutputStream out = new ByteArrayOutputStream()){
				copy(in, out);
				return out.toByteArray();
			}
		}
		catch (IOException e) {
			throw new ConectionException(e.getMessage(), e.getCause());
		}
		finally{
			con.disconnect();
		}
	}

	private static long copy(InputStream input, OutputStream output) throws IOException {
		byte[] buffer = new byte[BUFFER_SIZE];
		long count = 0L;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

}