package javacloud.framework.jacc.internal;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javacloud.framework.jacc.DiagnosticListener;
import javacloud.framework.util.Objects;
/**
 * Simple diagnostic
 * @author ho
 *
 */
public class DiagnosticCollector implements DiagnosticListener {
	//SIMPLE METRIC COLLECTION
	public static class Diagnostic {
		private int	lineNo;
		private int columnNo;
		private String message;
		public Diagnostic(int lineNo, int columnNo, String message) {
			this.lineNo   = lineNo;
			this.columnNo = columnNo;
			this.message  = message;
		}
		
		public int getLineNo() {
			return lineNo;
		}
		public int getColumnNo() {
			return columnNo;
		}
		public String getMessage() {
			return message;
		}
		
		//COMPATIBLE WITH JAVA ERROR
		@Override
		public String toString() {
			return "[" + lineNo + "," + columnNo + "] " + message;
		}
	}
	
	//KEEP METRICS PER FILE
	private boolean succeeded = true;
	private final Map<URI, List<Diagnostic>> failures = new LinkedHashMap<>();
	public DiagnosticCollector() {
	}
	
	/**
	 * Simply store metrics in MEMORY
	 */
	@Override
	public void onFailure(URI file, int lineNo, int columnNo, String reason) {
		List<Diagnostic> diagnostics = failures.get(file);
		if(diagnostics == null) {
			diagnostics = new ArrayList<Diagnostic>();
			failures.put(file, diagnostics);
		}
		diagnostics.add(new Diagnostic(lineNo, columnNo, reason));
	}
	
	/**
	 * FIXME: COLLECT WARNING METRICS
	 */
	@Override
	public void onWarning(URI file, int lineNo, int columnNo, String reason) {
	}
	
	/**
	 * return resource which has failure
	 * 
	 * @return
	 */
	public Iterable<URI> getFailures() {
		return failures.keySet();
	}
	
	/**
	 * return failure by source file
	 * 
	 * @param file
	 * @return
	 */
	public List<Diagnostic> getFailures(URI file) {
		return failures.get(file);
	}
	
	/**
	 * return true if actual has failures
	 * 
	 * @return
	 */
	public boolean isSucceeded() {
		return succeeded && Objects.isEmpty(failures);
	}
}
