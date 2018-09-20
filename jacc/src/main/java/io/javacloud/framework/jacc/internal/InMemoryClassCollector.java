package io.javacloud.framework.jacc.internal;

import io.javacloud.framework.internal.BytesOutputStream;
import io.javacloud.framework.jacc.ClassCollector;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.HashMap;
import java.util.Map;
/**
 * Capture the class byte code in memory for just in time lookup.
 * 
 * @author ho
 *
 */
public class InMemoryClassCollector extends DiagnosticCollector implements ClassCollector {
	private final Map<String, BytesOutputStream> bytesCode = new HashMap<>();
	public InMemoryClassCollector() {
	}
	
	/**
	 * return a writer byte stream
	 */
	@Override
	public WritableByteChannel asWriter(String className, URI file) {
		BytesOutputStream bytes = new BytesOutputStream(1024);
		bytesCode.put(className, bytes);
		return Channels.newChannel(bytes);
	}
	
	/**
	 * return byte code for the class
	 * @param className
	 * @return
	 */
	public ByteBuffer getBytes(String className) {
		BytesOutputStream out = bytesCode.get(className);
		return (out == null? null: out.byteBuffer());
	}
}
