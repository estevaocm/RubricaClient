package br.gov.serpro.rubrica.client;

import java.util.List;

/**
 * 
 * @author Estêvão Monteiro
 * @since 10/01/2017
 *
 */
public class SelecaoAssinaturaRemota {
	
	private String cpfAssinante;
	private List<AssinaturaRemota> assinaturasRemotas;
	
	public String getCpfAssinante() {
		return cpfAssinante;
	}
	public void setCpfAssinante(String cpfAssinante) {
		this.cpfAssinante = cpfAssinante;
	}
	public List<AssinaturaRemota> getAssinaturasRemotas() {
		return assinaturasRemotas;
	}
	public void setAssinaturasRemotas(List<AssinaturaRemota> assinaturasRemotas) {
		this.assinaturasRemotas = assinaturasRemotas;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SelecaoAssinaturaRemota [cpfAssinante=" + cpfAssinante + ", assinaturasRemotas=" + assinaturasRemotas + "]";
	}

}
