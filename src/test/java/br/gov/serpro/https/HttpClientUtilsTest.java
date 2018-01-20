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
