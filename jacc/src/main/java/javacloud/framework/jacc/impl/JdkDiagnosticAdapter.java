package javacloud.framework.jacc.impl;

import java.util.Locale;
import java.util.logging.Logger;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import javacloud.framework.jacc.DiagnosticListener;
/**
 * 
 * @author ho
 *
 */
public class JdkDiagnosticAdapter implements javax.tools.DiagnosticListener<JavaFileObject> {
	private static final Logger logger = Logger.getLogger(JdkDiagnosticAdapter.class.getName());
	private final DiagnosticListener listener;
	private final Locale locale;
	/**
	 * 
	 * @param listener
	 * @param locale
	 */
	public JdkDiagnosticAdapter(DiagnosticListener listener, Locale locale) {
		this.listener = listener;
		this.locale = locale;
	}
	
	/**
	 * report on failure only
	 */
	@Override
	public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
		String message = diagnostic.getMessage(locale);
		if(diagnostic.getKind() == Diagnostic.Kind.ERROR) {
			listener.onFailure(diagnostic.getSource().toUri(),
					(int)diagnostic.getLineNumber(), (int)diagnostic.getColumnNumber(), message);
		} else {
			logger.fine(message);
		}
	}
}
