package javacloud.framework.jacc;

import java.io.OutputStream;
import java.net.URI;
/**
 * Compiler interact with the collector to provide class byte code and report error if any.
 * 
 * @author ho
 *
 */
public interface ClassCollector extends DiagnosticListener {
	/**
	 * return a reader for writing class bytes code
	 * 
	 * @param className
	 * @param file
	 * @return
	 */
	public OutputStream asStream(String className, URI file);
}