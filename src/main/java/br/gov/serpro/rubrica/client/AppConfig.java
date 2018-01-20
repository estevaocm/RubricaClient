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

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author SUPST/STDCS
 */
public enum AppConfig {

	MESSAGE_ERROR_DRIVER_NOT_AVAILABLE("message.error.driver.not.available"), 
	MESSAGE_ERROR_PKCS11_NOT_FOUND("message.error.pkcs11.not.found"), 
	MESSAGE_ERROR_LOAD_TOKEN("message.error.load.driver"), 
	MESSAGE_ERROR_INVALID_PIN("message.error.invalid.pin"), 
	MESSAGE_ERROR_UNEXPECTED("message.error.unexpected"),

	LABEL_DIALOG_FRAME_TITLE("label.dialog.frame.title"), 
	LABEL_DIALOG_OPTION_PANE_TITLE("label.dialog.option_pane.title"), 
	LABEL_DIALOG_BUTTON_RUN("label.dialog.button.run"), 
	LABEL_DIALOG_BUTTON_CANCEL("label.dialog.button.cancel"),

	CONFIG_DIALOG_TABLE_LABEL("label.dialog.label.table"), 
	CONFIG_DIALOG_TABLE_LABEL_FONT("config.dialog.title.label.font"), 
	CONFIG_DIALOG_TABLE_LABEL_FONT_STYLE("config.dialog.title.label.font.style"), 
	CONFIG_DIALOG_TABLE_LABEL_FONT_SIZE("config.dialog.title.label.font.size"),
	
	CONFIG_DIALOG_LIST_FILES_LABEL("label.dialog.lable.files"), 

	CONFIG_DIALOG_TABLE_CERTIFICATES_WIDTH("config.dialog.table.certificates.width"), 
	CONFIG_DIALOG_TABLE_CERTIFICATES_HEIGHT("config.dialog.table.certificates.height"), 
	CONFIG_DIALOG_TABLE_CERTIFICATES_ROW_HEIGHT("config.dialog.table.certificates.row.heigth"),

	CONFIG_DIALOG_LIST_FILES_WIDTH("config.dialog.table.certificates.width"), 
	CONFIG_DIALOG_LIST_FILES_HEIGHT("config.dialog.table.certificates.height"), 
	CONFIG_DIALOG_LIST_FILES_ROW_HEIGHT("config.dialog.table.certificates.row.heigth"),

	CONFIG_DIALOG_BUTTON_RUN_WIDTH("config.dialog.button-run.width"), 
	CONFIG_DIALOG_BUTTON_RUN_HEIGHT("config.dialog.button-run.height"),
	CONFIG_DIALOG_BUTTON_CANCEL_WIDTH("config.dialog.button-cancel.width"), 
	CONFIG_DIALOG_BUTTON_CANCEL_HEIGHT(	"config.dialog.button-cancel.height"),
	
	//CONFIG_HTTPS_PROTOCOL("config.https.protocol");
	
	LABEL_DIALOG_PASSWORD("label.dialog.password"),
	TITLE_DIALOG_PASSWORD("title.dialog.password");

	private String key;
	private static ResourceBundle rb;

	private AppConfig(String key) {
		this.key = key;
	}

	public String getValue() {
		return getResourceBundle().getString(key);
	}

	/**
	 * Retorna o valor de enum convertido para o tipo 'int' conforme sua
	 * respectiva chave
	 * 
	 * @return
	 */
	public int getValueInt() {
		return Integer.valueOf(getValue());
	}

	private ResourceBundle getResourceBundle() {
		if (rb != null) {
			return rb;
		}
		try {
			rb = getBundle("desktop-config");
		} catch (MissingResourceException mre) {
			try {
				rb = getBundle("desktop-config-default");
			} catch (MissingResourceException e) {
				throw new RuntimeException("key '" + key
						+ "' not found for resource ''");
			}
		}
		return rb;
	}

	public ResourceBundle getBundle(String bundleName) {
		return ResourceBundle.getBundle(bundleName);
	}

}
