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

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.security.KeyStore;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import org.demoiselle.signer.core.exception.CertificateValidatorException;
import org.demoiselle.signer.core.keystore.loader.DriverNotAvailableException;
import org.demoiselle.signer.core.keystore.loader.InvalidPinException;
import org.demoiselle.signer.core.keystore.loader.KeyStoreLoader;
import org.demoiselle.signer.core.keystore.loader.KeyStoreLoaderException;
import org.demoiselle.signer.core.keystore.loader.PKCS11NotFoundException;
import org.demoiselle.signer.core.keystore.loader.factory.KeyStoreLoaderFactory;
import org.demoiselle.signer.jnlp.tiny.Item;

/**
 * @author SUPST/STDCS; Estêvão Monteiro
 * @since 16/08/16
 * @see http://demoiselle.sourceforge.net/docs/components/certificate/reference/1.1.0/html_single/
 */
public class JanelaPrincipal extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(JanelaPrincipal.class.getName());

	private JButton btnCancelar;
	private JButton btnExecutar;
	private JPanel panelbottom;
	private JPanel paneltop;
	private JScrollPane scrollPane;
	private JTable tableCertificates;

	private JScrollPane scrollPaneFiles;
	private JList<String> listFiles;

	private KeyStore keystore = null;
	private String className = "";

	private ControladorJanela businessController;

	/**
	 * Creates new form NovoJFrame
	 */
	public JanelaPrincipal() {
		initComponents();
		this.className = System.getProperty("jnlp.myClassName");

		if (this.className == null || this.className.isEmpty()) {
			this.className = "br.gov.serpro.certificate.ui.user.App";
		}
		this.businessController = ControladorJanelaFactory.factory(this.className);
		this.businessController.setFrame(this);

		List<String> arquivos = this.businessController.getArquivos();

		if(arquivos == null || arquivos.isEmpty()){
			showFailDialog("Falha ao obter arquivos para assinar.");
			System.exit(0);
		}
		DefaultListModel<String> files = new DefaultListModel<String>();
		this.listFiles.setModel(files);
		for (String string : arquivos) {
			files.addElement(string);
		}
		
		while (this.keystore == null){
			initKeyStore();// Recupera o repositorio de certificados digitais
		}
		
		loadCerts();

		setLocationRelativeTo(null); // Centraliza o frame

		addWindowListener(new WindowListener() {

			@Override
			public void windowIconified(WindowEvent e) {}
			@Override
			public void windowDeiconified(WindowEvent e) {}
			@Override
			public void windowDeactivated(WindowEvent e) {}
			@Override
			public void windowClosed(WindowEvent e) {}
			@Override
			public void windowActivated(WindowEvent e) {}
			@Override
			public void windowOpened(WindowEvent e) {}
			@Override
			public void windowClosing(WindowEvent e) {
				closeWindow(e);
			}
		});
	}
	
	private void loadCerts(){

		this.tableCertificates.setModel(this.businessController.filtrarCertificados(this.keystore));

		if (this.tableCertificates.getRowCount() == 0) {
			this.btnExecutar.setEnabled(false);
		} 
		else {
			this.tableCertificates.setRowSelectionInterval(0, 0);
		}

		this.tableCertificates.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		// Dimensiona cada coluna separadamente
		TableColumnModel columnModel = this.tableCertificates.getColumnModel();
		columnModel.getColumn(0).setPreferredWidth(200);
		columnModel.getColumn(1).setPreferredWidth(140);
		columnModel.getColumn(2).setPreferredWidth(140);
		columnModel.getColumn(3).setPreferredWidth(300);
	}

	private void initComponents() {

		this.paneltop = new JPanel();
		this.scrollPane = new JScrollPane();
		//this.scrollPaneText = new JScrollPane();
		this.scrollPaneFiles = new JScrollPane();
		this.tableCertificates = new JTable();
		this.panelbottom = new JPanel();
		this.btnExecutar = new JButton();
		this.btnCancelar = new JButton();
		this.listFiles = new JList<String>();

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setLocation(new Point(0, 0));
		setResizable(false);
		setTitle(AppConfig.LABEL_DIALOG_FRAME_TITLE.getValue());

		this.scrollPane.setAutoscrolls(true);
		this.scrollPane.setViewportView(this.tableCertificates);
		this.scrollPane.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(),
				AppConfig.CONFIG_DIALOG_TABLE_LABEL.getValue(),
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION,
				new java.awt.Font(AppConfig.CONFIG_DIALOG_TABLE_LABEL_FONT.getValue(), 
						AppConfig.CONFIG_DIALOG_TABLE_LABEL_FONT_STYLE.getValueInt(), 
						AppConfig.CONFIG_DIALOG_TABLE_LABEL_FONT_SIZE.getValueInt()))); // NOI18N


		this.tableCertificates.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		this.tableCertificates.setModel(new DefaultTableModel(
				new Object[][] { { null, null, null, null },
					{ null, null, null, null }, { null, null, null, null },
					{ null, null, null, null } }, new String[] { "Title 1",
							"Title 2", "Title 3", "Title 4" }));
		this.tableCertificates.setFillsViewportHeight(true);
		this.tableCertificates.setRowHeight(AppConfig.CONFIG_DIALOG_TABLE_CERTIFICATES_ROW_HEIGHT.getValueInt());

		this.scrollPaneFiles.setAutoscrolls(true);
		this.scrollPaneFiles.setViewportView(this.listFiles);
		this.scrollPaneFiles.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(),
				AppConfig.CONFIG_DIALOG_LIST_FILES_LABEL.getValue(),
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION,
				new java.awt.Font(AppConfig.CONFIG_DIALOG_TABLE_LABEL_FONT.getValue(), 
						AppConfig.CONFIG_DIALOG_TABLE_LABEL_FONT_STYLE.getValueInt(), 
						AppConfig.CONFIG_DIALOG_TABLE_LABEL_FONT_SIZE.getValueInt())));


		//this.listFiles.setEnabled(false);

		GroupLayout paneltopLayout = new GroupLayout(this.paneltop);
		this.paneltop.setLayout(paneltopLayout);

		paneltopLayout.setHorizontalGroup(paneltopLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(this.scrollPane, GroupLayout.DEFAULT_SIZE,	
						AppConfig.CONFIG_DIALOG_TABLE_CERTIFICATES_WIDTH.getValueInt(), Short.MAX_VALUE)
				.addComponent(this.scrollPaneFiles, GroupLayout.DEFAULT_SIZE,	
						AppConfig.CONFIG_DIALOG_LIST_FILES_WIDTH.getValueInt(), Short.MAX_VALUE));

		paneltopLayout.setVerticalGroup(paneltopLayout.createSequentialGroup()
				.addComponent(this.scrollPane,GroupLayout.PREFERRED_SIZE,
						AppConfig.CONFIG_DIALOG_TABLE_CERTIFICATES_HEIGHT.getValueInt(),GroupLayout.PREFERRED_SIZE)
				.addComponent(this.scrollPaneFiles,GroupLayout.PREFERRED_SIZE,
						AppConfig.CONFIG_DIALOG_LIST_FILES_HEIGHT.getValueInt(),GroupLayout.PREFERRED_SIZE));

		this.panelbottom.setBorder(BorderFactory.createEtchedBorder());

		this.btnExecutar.setText(AppConfig.LABEL_DIALOG_BUTTON_RUN.getValue());
		this.btnExecutar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				btnExecutarActionPerformed(evt);
			}
		});

		this.btnCancelar.setText(AppConfig.LABEL_DIALOG_BUTTON_CANCEL.getValue());
		this.btnCancelar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				btnCancelarActionPerformed(evt);
			}
		});

		GroupLayout panelbottomLayout = new GroupLayout(this.panelbottom);
		this.panelbottom.setLayout(panelbottomLayout);
		panelbottomLayout.setHorizontalGroup(panelbottomLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(panelbottomLayout.createSequentialGroup()
						.addComponent(this.btnExecutar,GroupLayout.PREFERRED_SIZE, 
								AppConfig.CONFIG_DIALOG_BUTTON_RUN_WIDTH.getValueInt(), GroupLayout.PREFERRED_SIZE)
						.addComponent(this.btnCancelar, GroupLayout.PREFERRED_SIZE, 
								AppConfig.CONFIG_DIALOG_BUTTON_CANCEL_WIDTH.getValueInt(), GroupLayout.PREFERRED_SIZE)
						));
		panelbottomLayout.setVerticalGroup(panelbottomLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(panelbottomLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(panelbottomLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(this.btnExecutar, GroupLayout.PREFERRED_SIZE, 
										AppConfig.CONFIG_DIALOG_BUTTON_RUN_HEIGHT.getValueInt(), 
										GroupLayout.PREFERRED_SIZE)
								.addComponent(this.btnCancelar, GroupLayout.PREFERRED_SIZE, 
										AppConfig.CONFIG_DIALOG_BUTTON_CANCEL_HEIGHT.getValueInt(), 
										GroupLayout.PREFERRED_SIZE)
								)
						.addContainerGap()
						));

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(layout.createParallelGroup(Alignment.LEADING,	false)
								.addComponent(this.paneltop, GroupLayout.DEFAULT_SIZE,
										GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(this.panelbottom, GroupLayout.DEFAULT_SIZE, 
										GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addContainerGap(GroupLayout.DEFAULT_SIZE,Short.MAX_VALUE)));
		layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addContainerGap()
						.addComponent(this.paneltop, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, 
								GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(this.panelbottom, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, 
								GroupLayout.PREFERRED_SIZE)
						.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		pack();

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				btnExecutar.requestFocusInWindow();
			}
		});
	}

	private void btnExecutarActionPerformed(java.awt.event.ActionEvent evt) {
		this.businessController.execute(this.keystore, getAlias());
	}

	private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {
		this.businessController.cancel(this.keystore, getAlias());
	}

	private void closeWindow(WindowEvent e) {
		this.businessController.close();
	}

	/**
	 * Retorna o keystore do dispositivo a partir do valor de pin
	 *
	 * @return
	 */
	private void initKeyStore() {
		
		Cursor hourGlassCursor = new Cursor(Cursor.WAIT_CURSOR);
		setCursor(hourGlassCursor);

		try {
			KeyStoreLoader loader = KeyStoreLoaderFactory.factoryKeyStoreLoader();
			loader.setCallbackHandler(new CustomPinCallbackHandler());
			this.keystore = loader.getKeyStore();
		}
		catch (DriverNotAvailableException e) {
			showFailDialog(AppConfig.MESSAGE_ERROR_DRIVER_NOT_AVAILABLE.getValue());
		}
		catch (PKCS11NotFoundException e) {
			showFailDialog(AppConfig.MESSAGE_ERROR_PKCS11_NOT_FOUND.getValue());
		}
		catch (CertificateValidatorException e) {
			showFailDialog(AppConfig.MESSAGE_ERROR_LOAD_TOKEN.getValue());
		}
		catch (InvalidPinException e) {
			showFailDialog(AppConfig.MESSAGE_ERROR_INVALID_PIN.getValue());
		}
		catch (KeyStoreLoaderException ke) {
			showFailDialog(ke.getMessage());
		}
		catch (Exception ex) {
			showFailDialog(AppConfig.MESSAGE_ERROR_UNEXPECTED.getValue());
		}
		finally {
			Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
			setCursor(defaultCursor);
		}
	}

	/**
	 * Obtem o apelido associado a um certificado
	 *
	 * @return O apelido associado ao certificado
	 */
	public String getAlias() {
		int qtdCerts = this.tableCertificates.getModel().getRowCount();
		if (qtdCerts > 0) {
			int row = this.tableCertificates.getSelectedRow();
			if(row < 0){
				if(qtdCerts == 1){
					row = 0;
				}
			}
			Item item = (Item) this.tableCertificates.getModel().getValueAt(row, 0);
			return item.getAlias();
		} 
		else {
			return "";
		}
	}

	/**
	 * Exibe as mensagens de erro
	 *
	 * @param message
	 */
	private void showFailDialog(String message) {
		JOptionPane.showMessageDialog(this, message,
				AppConfig.LABEL_DIALOG_OPTION_PANE_TITLE.getValue(),
				JOptionPane.ERROR_MESSAGE);
	}   

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		/* Set the Nimbus look and feel */
		//<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
		/* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
		 * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
		 */
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			LOGGER.log(Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			LOGGER.log(Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			LOGGER.log(Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			LOGGER.log(Level.SEVERE, null, ex);
		}
		//</editor-fold>
		//</editor-fold>
		//</editor-fold>
		//</editor-fold>

		/* Create and display the form */
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new JanelaPrincipal().setVisible(true);
			}
		});
	}
}