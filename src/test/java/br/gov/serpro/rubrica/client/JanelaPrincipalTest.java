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

import br.gov.serpro.rubrica.client.JanelaPrincipal;

public class JanelaPrincipalTest {
	
	public static void main(String[] args) {
		System.setProperty("jnlp.myClassName", "br.gov.serpro.rubrica.client.RubricaClient");
		System.setProperty("jnlp.token", 
				"jwt eyJraWQiOiJkZW1vaXNlbGxlLXNlY3VyaXR5LWp3dCIsImFsZyI6IlJTMjU2In0.eyJpc3MiOiJBcHAiLCJleHAiOjE1MTcxODkxMjIsImF1ZCI6InNpc2RuaXQiLCJqdGkiOiJJdDdEMEFrV1FPV3U2VW9jZVhPNkFnIiwiaWF0IjoxNTE2ODI5MTIyLCJuYmYiOjE1MTY4MjkwNjIsImlkZW50aXR5IjoiOTAxNyIsIm5hbWUiOiJFU1TDilbDg08gQ0hBVkVTIE1PTlRFSVJPIiwicm9sZXMiOltdLCJwZXJtaXNzaW9ucyI6e30sInBhcmFtcyI6eyJVbmlkYWRlIjoiQ09PUkRFTkHDh8ODTy1HRVJBTCBERSBNQU5VVEVOw4fDg08gRSBSRVNUQVVSQcOHw4NPICBST0RPVknDgVJJQSIsIkVtYWlsIjoiZXN0ZXZhby5tb250ZWlyb0BzZXJwcm8uZ292LmJyIiwiQ1BGIjoiMDE2MDgzOTA1MDAiLCJDb1VuaWRhZGUiOiIxNSIsIlNnVW5pZGFkZSI6IkNHTVJSIn19.qTCvLXL9Y21Pxn1S-Ek1yJ_lm6d9Ng1OPUGhU-oeYXL_bFIw15Yg2JNv0RbfXEaqDwHvtMurVHw2sB_eJcEX9kIdWVXWxuWNkzvwEiod47lFubYSq05DDNnGBUzqQtHdXXR9mjGyopCJ_0xjsM02aVOD0DwOQYN5e6EXUXH_DiwiWR-Yt_hQH7-lPJHqPp5kXKRX5BcjKTW1S2v0KyhabaRnH4sHWSdo4HRi8vmgFDe8o6dXAThDHx_6XS55rLuOd_zqs2nFDpr94kKdk3iJVlQ7ewJM8Q8vRL78qMILQSJRuJpPEu6Pd5xjBMJLEaePNC0D3Wjt0K7y6H52sWCZfA");
		System.setProperty("jnlp.get", "https://localhost:8443/assinador-backend/api/documento/selecao-assinar/28");
		System.setProperty("jnlp.post", "https://localhost:8443/assinador-backend/api/documento/assinatura/");
		JanelaPrincipal.main(null);
		//Cuidado com drivers.config, causou falha com libsoftokn3.so, teve que ser instalado manualmente
	}
}