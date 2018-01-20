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
