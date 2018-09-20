package io.javacloud.framework.jacc.internal;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.javacloud.framework.jacc.DiagnosticListener;
/**
 * Simple diagnostic
 * @author ho
 *
 */
public class DiagnosticCollector implements DiagnosticListener {
	//SIMPLE METRIC COLLECTION
	public static class Metric {
		public final int lineNo;
		public final int columnNo;
		public final String reason;
		public Metric(int lineNo, int columnNo, String reason) {
			this.lineNo = lineNo;
			this.columnNo = columnNo;
			this.reason = reason;
		}
		
		//COMPATIBLE WITH JAVA ERROR
		@Override
		public String toString() {
			return "[" + lineNo + "," + columnNo + "] " + reason;
		}
	}
	
	//KEEP METRICS PER FILE
	private final Map<URI, List<Metric>> metrics = new LinkedHashMap<>();
	public DiagnosticCollector() {
	}
	
	/**
	 * Simply store metrics in MEMORY
	 */
	@Override
	public void onFailure(URI file, int lineNo, int columnNo, String reason) {
		List<Metric> failures = metrics.get(file);
		if(failures == null) {
			failures = new ArrayList<Metric>();
			metrics.put(file, failures);
		}
		failures.add(new Metric(lineNo, columnNo, reason));
	}
	
	/**
	 * return resource which has failure
	 * 
	 * @return
	 */
	public Iterable<URI> getFailures() {
		return metrics.keySet();
	}
	
	/**
	 * return failure by source file
	 * 
	 * @param file
	 * @return
	 */
	public List<Metric> getMetrics(URI file) {
		return metrics.get(file);
	}
}
