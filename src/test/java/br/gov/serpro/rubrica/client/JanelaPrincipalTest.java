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

public class JanelaPrincipalTest {
	
	public static void main(String[] args) {
		System.setProperty("jnlp.myClassName", 
				"br.gov.serpro.rubrica.client.RubricaClient");
		System.setProperty("jnlp.token", "jwt eyJraWQiOiJkZW1vaXNlbGxlLXNlY3VyaXR5LWp3dCIsImFsZyI6IlJTMjU2In0.eyJpc3MiOiJBcHAiLCJleHAiOjE1MTY2NjMyODgsImF1ZCI6InNpc2RuaXQiLCJqdGkiOiJlRy00RmJtYnQ3OTBzcThXa1NNUkRBIiwiaWF0IjoxNTE2MzAzMjg4LCJuYmYiOjE1MTYzMDMyMjgsImlkZW50aXR5IjoiOTAxNyIsIm5hbWUiOiJFU1TDilbDg08gQ0hBVkVTIE1PTlRFSVJPIiwicm9sZXMiOltdLCJwZXJtaXNzaW9ucyI6e30sInBhcmFtcyI6eyJVbmlkYWRlIjoiQ09PUkRFTkHDh8ODTy1HRVJBTCBERSBNQU5VVEVOw4fDg08gRSBSRVNUQVVSQcOHw4NPICBST0RPVknDgVJJQSIsIkVtYWlsIjoiZXN0ZXZhby5tb250ZWlyb0BzZXJwcm8uZ292LmJyIiwiYXNzaW5hdHVyYXMiOiJbe1wiY29kaWdvXCI6MSxcImNvZFNvbGljaXRhY2FvXCI6MTczLFwiY29kVW5pZGFkZVwiOjMwMCxcImNwZlwiOm51bGwsXCJmdW5jYW9cIjpcIkRJUkVUT1JcIixcImRhdGFBc3NpbmF0dXJhXCI6bnVsbCxcIm9yZGVtXCI6MSxcImNwZkFzc2luYW50ZVwiOm51bGx9XSIsIkNQRiI6IjAxNjA4MzkwNTAwIiwiQ29VbmlkYWRlIjoiMTUiLCJTZ1VuaWRhZGUiOiJDR01SUiJ9fQ.KmzrS0EbdnK4Ij5kUM2AY1Zhk5QR__g-DzzhHV-SG8qVkMGmJgm0Z16BicOEs7xDwS77QDQzq5Kou87iDLyL6BdB7piu4FPkeVoOkjxklOdFW79VrFWQqGC5FH7BKoFhOFKJjIOfcFXEIfAB5mQ0Z7fy_VzlBKUI-akh68N0PWpNTXZH2xt6utiWOI4ezGgEIc95G03RBVHNwF2Ew9sZyWiizAJks5kbbQ8wl1CRh-S1TJ7Vx-xZCErGVWRJVWjBTfsgo9gzLkMaIwbzb4H1eiP1lZ983p_nfwAotwAfH3-bWfoUQFJgcTnoYzEv0bW4u8MKg8zIxW8pzqOZ7_2YqA");
		System.setProperty("jnlp.get", "http://localhost:8080/assinador-backend/api/documento/selecao-assinar/");
		System.setProperty("jnlp.post", "http://localhost:8080/assinador-backend/api/documento/assinatura/");
		JanelaPrincipal.main(null);
		//Cuidado com drivers.config, causou falha com libsoftokn3.so, teve que ser instalado manualmente
	}
}