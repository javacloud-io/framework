package io.javacloud.framework.jacc.internal;

import java.net.URI;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import io.javacloud.framework.internal.BytesInputStream;
import io.javacloud.framework.jacc.JavaSource;
import io.javacloud.framework.util.Codecs;
/**
 * 
 * @author ho
 *
 */
public class JavaSourceFile implements JavaSource {
	private final URI uri;
	private final byte[] source;
	/**
	 * 
	 * @param className
	 * @param source
	 */
	public JavaSourceFile(String className, byte[] source) {
		if(className.endsWith(".java")) {
			className = className.substring(0, className.length() - 5);
		}
		this.uri = URI.create("src://" + className.replaceAll("\\.", "/") + ".java");
		this.source = source;
	}
	
	/**
	 * 
	 * @param className
	 * @param source
	 */
	public JavaSourceFile(String className, String source) {
		this(className, Codecs.toBytes(source));
	}
	
	/**
	 * 
	 */
	@Override
	public URI file() {
		return uri;
	}
	
	/**
	 * 
	 */
	@Override
	public ReadableByteChannel asReader() {
		return Channels.newChannel(new BytesInputStream(source));
	}
}
