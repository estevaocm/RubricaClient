package br.gov.serpro.rubrica.client;

import org.demoiselle.signer.jnlp.factory.FactoryException;

/**
 * 
 * @author Estêvão Monteiro
 * @since 16/08/16
 *
 */
public class ControladorJanelaFactory {
	
	@SuppressWarnings("rawtypes")
	public static ControladorJanela factory(String className) {
    	ControladorJanela instance = null;
        Class clazz = null;
        try {
            clazz = Class.forName(className);
        } 
        catch (Throwable error) {
            throw new FactoryException("Class [" + className + "] not found", error);
        }
        if (clazz != null) {
            try {
                instance = (ControladorJanela) clazz.newInstance();
            } 
            catch (Throwable error) {
                throw new FactoryException("incompatible Class [" + clazz.getCanonicalName() + "]", error);
            }
        }
        return instance;
    }

}
