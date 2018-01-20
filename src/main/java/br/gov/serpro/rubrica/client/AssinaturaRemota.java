package br.gov.serpro.rubrica.client;

/**
 * 
 * @author Estêvão Monteiro
 * @since 09/01/2018
 *
 */
public class AssinaturaRemota {
	
	private Long idArquivo;
	private String descricaoArquivo;
	private String hash;
	private String assinatura;
	private String dadosNegocio;
	
	public AssinaturaRemota(){
		super();//Nada a fazer.
	}

	public Long getIdArquivo() {
		return idArquivo;
	}

	public void setIdArquivo(Long idArquivo) {
		this.idArquivo = idArquivo;
	}

	public String getDescricaoArquivo() {
		return descricaoArquivo;
	}

	public void setDescricaoArquivo(String descricaoArquivo) {
		this.descricaoArquivo = descricaoArquivo;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getAssinatura() {
		return assinatura;
	}

	public void setAssinatura(String assinatura) {
		this.assinatura = assinatura;
	}

	public String getDadosNegocio() {
		return dadosNegocio;
	}

	public void setDadosNegocio(String dadosNegocio) {
		this.dadosNegocio = dadosNegocio;
	}

	@Override
	public String toString() {
		return "AssinaturaRemota [idArquivo=" + idArquivo + ", descricaoArquivo=" + descricaoArquivo + ", hash=" + hash
				+ ", assinatura=" + assinatura + ", dadosNegocio=" + dadosNegocio + "]";
	}

}
