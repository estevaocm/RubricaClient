package br.gov.serpro.rubrica.client;

import java.security.KeyStore;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.table.AbstractTableModel;

/**
 * 
 * @author Estêvão Monteiro
 * @since 16/08/16
 *
 */
public interface ControladorJanela {
	
	void setFrame(JFrame janela);
	
    void execute(KeyStore ks, String alias);

    void cancel(KeyStore ks, String alias);
    
    void close();
    
    String getText();
    
    List<String> getArquivos();

    AbstractTableModel filtrarCertificados(KeyStore keystore);
	
}
