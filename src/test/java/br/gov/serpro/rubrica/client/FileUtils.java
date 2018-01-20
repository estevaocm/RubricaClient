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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author SUPST/STDCS
 */
public final class FileUtils {

	private static final Logger LOGGER = Logger.getLogger(FileUtils.class.getName());
	
	/**
	 * Read the given binary file, and return its contents as a byte array.
	 *
	 * @param file
	 *            Caminho e nome do arquivo
	 * @return Conteudo lido
	 */
	public static byte[] readContentFromDisk(String file) {
		File f = new File(file);

		byte[] result = new byte[(int) f.length()];
		try {
			InputStream in = null;
			int totalBytesRead = 0;
			try {
				in = new BufferedInputStream(new FileInputStream(f));
				while (totalBytesRead < result.length) {
					int bytesRemaining = result.length - totalBytesRead;
					int bytesRead = in.read(result, totalBytesRead,
							bytesRemaining);
					if (bytesRead > 0) {
						totalBytesRead = totalBytesRead + bytesRead;
					}
				}
			}
			finally {
				in.close();
			}
		}
		catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
		return result;
	}

	/**
	 *
	 * @param content
	 *            Conteudo a ser gravado
	 * @param file
	 *            Caminho e nome do arquivo
	 */
	public static void writeContentToDisk(byte[] content, String file) {
		FileOutputStream os = null;
		try{
			try {
				os = new FileOutputStream(new File(file));
				os.write(content);
				os.flush();
			}
			finally{
				if(os != null){
					try {
						os.close();
					}
					catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}
	
}
