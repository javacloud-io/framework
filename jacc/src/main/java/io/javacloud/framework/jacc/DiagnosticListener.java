package io.javacloud.framework.jacc;

import java.net.URI;

/**
 * 
 * @author ho
 *
 */
public interface DiagnosticListener {
	/**
	 * 
	 * @param file
	 * @param lineNo
	 * @param columnNo
	 * @param reason
	 */
	public void onFailure(URI file, int lineNo, int columnNo, String reason);
}
