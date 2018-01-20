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

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.logging.Logger;

import javax.swing.table.AbstractTableModel;

import org.demoiselle.signer.jnlp.tiny.Item;

import br.gov.serpro.https.UserCertificateUtils;

/**
 * 
 * @author Demoiselle; Estêvão Monteiro
 * @since 06/10/2016
 * 
 */
@SuppressWarnings("serial")
public class CertificadoFiltradoModel extends AbstractTableModel {

    private Object[][] dados;
    private final String[] columnNames = { "Emitido Para", "Válido de", "Válido até", "Emitido Por" };
    private static final Logger LOGGER = Logger.getLogger(CertificadoFiltradoModel.class.getName());

    @Override
    public int getRowCount() {
        if (this.dados == null) {
            return 0;
        }
        return this.dados.length;
    }

    @Override
    public int getColumnCount() {
        return this.columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return this.columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (this.dados == null || rowIndex < 0) {
            return null;
        }
        return this.dados[rowIndex][columnIndex];
    }

    public void populate(KeyStore keystore, String cpf, String cnpj) {

        LOGGER.fine("populando lista de certificados.");

        if (keystore == null) {
            LOGGER.severe("keystore null");
            return;
        }
        try {
            int linha = keystore.size();
            int coluna = this.columnNames.length;

            // Tabela temporária, pois o número de elementos ao final pode mudar.
            Object[][] tabela = new Object[linha][coluna];

            int ik = 0;
            Enumeration<String> aliases = keystore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                LOGGER.info("Processando ALIAS: " + alias);

                try {
                    UserCertificateUtils.validaIdentidade(cpf, cnpj, keystore, alias);
                } catch (IllegalArgumentException e) {
                    LOGGER.severe("Erro validando identidade do certificado:" + e.getMessage());
                    continue;
                }

                X509Certificate certificate = (X509Certificate) keystore.getCertificate(alias);

                Item item = new Item(alias, certificate.getSubjectDN().getName(), certificate.getNotBefore(),
                        certificate.getNotAfter(), certificate.getIssuerDN().getName());
                tabela[ik][0] = item;
                tabela[ik][1] = item.getInitDate();
                tabela[ik][2] = item.getEndDate();
                tabela[ik][3] = item.getIssuer();
                ik++;
                LOGGER.info("ALIAS: " + alias + " adicionado com sucesso");
            }

            if (ik == 0) {
                return;
            }

            this.dados = new Object[ik][coluna];
            for (int i = 0; i < ik; i++) {
            	this.dados[i] = tabela[i];
            }

            fireTableDataChanged();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

    }
}
