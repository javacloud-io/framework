package javacloud.framework.jacc.impl;

import java.util.Locale;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import javacloud.framework.jacc.DiagnosticListener;
/**
 * 
 * @author ho
 *
 */
public class JdkDiagnosticAdapter implements javax.tools.DiagnosticListener<JavaFileObject> {
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
	 * 
	 */
	@Override
	public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
		listener.onFailure(diagnostic.getSource().toUri(),
							(int)diagnostic.getLineNumber(),
							(int)diagnostic.getColumnNumber(),
							diagnostic.getMessage(locale));
	}
}
