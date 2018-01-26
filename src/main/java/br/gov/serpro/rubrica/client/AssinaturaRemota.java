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

/**
 * 
 * @author Estêvão Monteiro
 * @since 09/01/2018
 *
 */
public class AssinaturaRemota {
	
	private String identificador;	
	private String dataAssinatura;
	private String descricaoArquivo;
	private String hash256;
	private String hash512;
	private String assinatura;
	
	public AssinaturaRemota() {
	}
	
	/**
	 * Identificador único do arquivo a assinar.
	 * @return
	 */
	public String getIdentificador() {
		return identificador;
	}
	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}

	/**
	 * Código SHA-256 em base 64.
	 * @return
	 */
	public String getHash256() {
		return hash256;
	}
	public void setHash256(String hash) {
		this.hash256 = hash;
	}

	/**
	 * Código SHA-512 em base 64.
	 * @return
	 */
	public String getHash512() {
		return hash512;
	}

	public void setHash512(String hash512) {
		this.hash512 = hash512;
	}
	
	/**
	 * Assinatura digital em base 64.
	 * @return
	 */
	public String getAssinatura() {
		return assinatura;
	}
	public void setAssinatura(String hashAssinado) {
		this.assinatura = hashAssinado;
	}

	/**
	 * Descrição do arquivo para verificação pelo usuário.
	 * @return
	 */
	public String getDescricaoArquivo() {
		return descricaoArquivo;
	}

	public void setDescricaoArquivo(String descricao) {
		this.descricaoArquivo = descricao;
	}

	/**
	 * Data que será registrada para a assinatura.
	 * No caso de documentos PDF, precisa ser pré-definida para preparação do arquivo
	 * antes de se computar o código hash e assinar.
	 * @return
	 */
	public String getDataAssinatura() {
		return dataAssinatura;
	}

	public void setDataAssinatura(String dataAssinatura) {
		this.dataAssinatura = dataAssinatura;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AssinaturaRemota [identificador=" + identificador + ", dataAssinatura=" + dataAssinatura
				+ ", descricao=" + descricaoArquivo + ", hash256=" + hash256 + ", hash512=" + hash512 + ", assinatura="
				+ assinatura + "]";
	}

}