package io.javacloud.framework.jacc;

import java.net.URI;
import java.nio.channels.WritableByteChannel;
/**
 * Compiler interact with the collector to provide class byte code and report error if any.
 * 
 * @author ho
 *
 */
public interface ClassCollector extends DiagnosticListener {
	/**
	 * return a reader for writing class bytes code
	 * @param className
	 * @param file
	 * @return
	 */
	public WritableByteChannel asWriter(String className, URI file);
}