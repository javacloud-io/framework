package io.javacloud.framework.util;


/**
 * Throws the exception if something already exist.
 * 
 * @author tobi
 * 
 */
public class AlreadyExistsException extends UncheckedException {
	private static final long serialVersionUID = -1725603370191594789L;
	
	public AlreadyExistsException(String subject) {
		super(subject);
	}
}