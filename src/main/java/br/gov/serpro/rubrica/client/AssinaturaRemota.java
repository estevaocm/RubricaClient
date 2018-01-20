package br.gov.serpro.rubrica.client;

/**
 * 
 * @author Estêvão Monteiro
 * @since 09/01/2017
 *
 */
public class AssinaturaRemota {
	
	private Long codSolicitacao;
	private String dataAssinatura;
	private String tipoDocumento;
	private String sistema;
	private String resumo;
	private String hash;
	private String assinatura;
	private String funcaoAssinante;
	
	public AssinaturaRemota(){
		super();//Nada a fazer.
	}

	public Long getCodSolicitacao() {
		return codSolicitacao;
	}

	public void setCodSolicitacao(Long codSolicitacao) {
		this.codSolicitacao = codSolicitacao;
	}

	public String getDataAssinatura() {
		return dataAssinatura;
	}

	public void setDataAssinatura(String dataAssinatura) {
		this.dataAssinatura = dataAssinatura;
	}

	public String getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(String tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public String getSistema() {
		return sistema;
	}

	public void setSistema(String sistema) {
		this.sistema = sistema;
	}

	public String getResumo() {
		return resumo;
	}

	public void setResumo(String resumo) {
		this.resumo = resumo;
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

	public String getFuncaoAssinante() {
		return funcaoAssinante;
	}

	public void setFuncaoAssinante(String funcaoAssinante) {
		this.funcaoAssinante = funcaoAssinante;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AssinaturaRemota [codSolicitacao=" + codSolicitacao + ", dataAssinatura=" + dataAssinatura
				+ ", tipoDocumento=" + tipoDocumento + ", sistema=" + sistema + ", resumo=" + resumo + ", hash=" + hash
				+ ", assinatura=" + assinatura + "]";
	}

}
