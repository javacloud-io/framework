package io.javacloud.framework.jacc.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;

import javax.tools.SimpleJavaFileObject;

import io.javacloud.framework.internal.BytesChannelReader;
import io.javacloud.framework.jacc.JavaSource;

/**
 * 
 * @author ho
 *
 */
@SuppressWarnings("restriction")
public class JavaSourceFileAdapter extends SimpleJavaFileObject {
	private final JavaSource source;
	/**
	 * 
	 * @param source
	 */
	public JavaSourceFileAdapter(JavaSource source) {
		super(source.file(), Kind.SOURCE);
		this.source = source;
	}
	
	/**
	 * return input stream
	 */
	@Override
	public InputStream openInputStream() throws IOException {
		return Channels.newInputStream(source.asReader());
	}
	
	/**
	 * return char content
	 */
	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
		return BytesChannelReader.toBytesStream(source.asReader()).toString();
	}
}
