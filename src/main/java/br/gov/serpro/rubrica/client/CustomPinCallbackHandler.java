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

import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.IOException;
import java.util.concurrent.CancellationException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

/**
 * @author SUPST/STDCS; Estêvão Monteiro
*/
public class CustomPinCallbackHandler implements CallbackHandler {

	private static final Logger L = Logger.getLogger(CustomPinCallbackHandler.class);
	
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {

        for (Callback callback : callbacks) {
            if (callback instanceof PasswordCallback) {
                handlePasswordCallback((PasswordCallback) callback);
            } else {
                throw new UnsupportedCallbackException(callback, 
                		"Função de retorno não suportada: " + callback.getClass().getName());
            }
        }
    }

    private void handlePasswordCallback(PasswordCallback passwordCallback) 
    throws UnsupportedCallbackException {
    	
        // dialog
        final JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1));

        // label
        String label = AppConfig.LABEL_DIALOG_PASSWORD.getValue();
        if(label == null || label.isEmpty()){
        	label = passwordCallback.getPrompt();
        }
        else{
        	L.info(passwordCallback.getPrompt());
        }
        panel.add(new JLabel(label));
        
        //option pane
        final JOptionPane opPane = new JOptionPane(panel, JOptionPane.QUESTION_MESSAGE, 
        		JOptionPane.OK_CANCEL_OPTION);
        
        // password input
        final JTextField txtPwd = new JPasswordField(20);
        panel.add(txtPwd);
        
        //dialog
        String title = AppConfig.TITLE_DIALOG_PASSWORD.getValue();
        if(title == null || title.isEmpty()){
        	title = "Senha";
        }
        final JDialog dialog = opPane.createDialog(null, title);
        dialog.addWindowFocusListener(new WindowFocusListener() {
			@Override
			public void windowLostFocus(WindowEvent e) {
			}
			@Override
			public void windowGainedFocus(WindowEvent e) {
				txtPwd.requestFocusInWindow();
			}
		});
        
        // show dialog
        dialog.setVisible(true);
        
        dialog.dispose();
        int retorno = opPane.getValue() != null ? ((Integer) opPane.getValue()).intValue() 
        		: JOptionPane.CANCEL_OPTION;

        switch (retorno) {
            case JOptionPane.OK_OPTION:
                // return password
                passwordCallback.setPassword(txtPwd.getText().toCharArray());
                break;
            case JOptionPane.CANCEL_OPTION:
                // return password
            	System.exit(0);
            default:
                // canceled by user
                throw new CancellationException("Callback da senha cancelado pelo usuário");
        }
    }
}