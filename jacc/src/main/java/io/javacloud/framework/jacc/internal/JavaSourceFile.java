package io.javacloud.framework.jacc.internal;

import java.net.URI;

import io.javacloud.framework.jacc.JavaSource;
import io.javacloud.framework.util.Codecs;
/**
 * 
 * @author ho
 *
 */
public class JavaSourceFile implements JavaSource {
	private final URI uri;
	private final CharSequence source;
	/**
	 * 
	 * @param className
	 * @param source
	 */
	public JavaSourceFile(String className, CharSequence source) {
		if(className.endsWith(".java")) {
			className = className.substring(0, className.length() - 5);
		}
		this.uri = URI.create("src://" + className.replaceAll("\\.", "/") + ".java");
		this.source = source;
	}
	
	/**
	 * Read source file into memory prior to parsing
	 * @param className
	 * @param source
	 */
	public JavaSourceFile(String className, byte[] source) {
		this(className, Codecs.toUTF8(source));
	}
	
	/**
	 * return file location
	 */
	@Override
	public URI file() {
		return uri;
	}
	
	/**
	 * return an input stream for reading
	 */
	@Override
	public CharSequence asSequence() {
		return source;
	}
}
