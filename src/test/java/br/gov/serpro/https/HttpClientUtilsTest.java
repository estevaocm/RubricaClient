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

import java.io.IOException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import br.gov.serpro.https.HttpClientUtils;
import br.gov.serpro.https.ServerCertificateUtils;

public class HttpClientUtilsTest {

    public static void main(String[] args) {

        String[] args2 = { "127.0.0.1" };
        try {
            InstallCert.main(args2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String servico = "https://localhost:8543/assinador";
        // servico = "https://www.google.com";
        String tokenLogin = "59c3d95e-9ffd-4def-8238-d45a4fa2a8e4";

        ServerCertificateUtils.suspenderValidacaoCadeia();

        String resposta = HttpClientUtils.getJson(servico, "0");
        // String resposta = HttpClientUtils.getJson(servico + "/config/" + tokenLogin, "0", certificateForHTTPS);
        // HttpClientUtils.getTls();
        // checkProtocols();
    }

    public static void checkProtocols() throws IOException {
        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        SSLSocket socket = (SSLSocket) factory.createSocket("127.0.0.1", 8543);
        String[] protocols = socket.getEnabledProtocols();
        System.out.println("Enabled Protocols: ");
        for (int i = 0; i < protocols.length; i++) {
            System.out.println(protocols[i] + ", ");
        }
        String[] supportedProtocols = socket.getSupportedProtocols();
        System.out.println("Supported Protocols: ");
        for (int i = 0; i < protocols.length; i++) {
            System.out.println(supportedProtocols[i] + ", ");
        }
    }
}
