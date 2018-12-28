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
	 * report on failure only
	 */
	@Override
	public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
		String message = diagnostic.getMessage(locale);
		if(diagnostic.getKind() == Diagnostic.Kind.ERROR) {
			listener.onFailure(diagnostic.getSource().toUri(),
					(int)diagnostic.getLineNumber(), (int)diagnostic.getColumnNumber(), message);
		} else if(diagnostic.getKind() == Diagnostic.Kind.WARNING
				|| diagnostic.getKind() == Diagnostic.Kind.MANDATORY_WARNING) {
			listener.onWarning(diagnostic.getSource().toUri(),
					(int)diagnostic.getLineNumber(), (int)diagnostic.getColumnNumber(), message);
		}
	}
}
