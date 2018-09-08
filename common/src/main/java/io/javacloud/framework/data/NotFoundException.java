package io.javacloud.framework.data;

import io.javacloud.framework.util.UncheckedException;

/**
 * Throw exception if something not found.
 * 
 * @author tobi
 *
 */
public class NotFoundException extends UncheckedException {
	private static final long serialVersionUID = -1655317657875278373L;
	
	public NotFoundException(String subject) {
		super(subject);
	}
}
