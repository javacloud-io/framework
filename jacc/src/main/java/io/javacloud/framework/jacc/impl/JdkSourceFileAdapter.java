package io.javacloud.framework.jacc.impl;

import java.io.IOException;
import java.io.InputStream;

import javax.tools.SimpleJavaFileObject;

import io.javacloud.framework.internal.BytesInputStream;
import io.javacloud.framework.jacc.JavaSource;

/**
 * 
 * @author ho
 *
 */
@SuppressWarnings("restriction")
public class JdkSourceFileAdapter extends SimpleJavaFileObject {
	private final JavaSource source;
	/**
	 * 
	 * @param source
	 */
	public JdkSourceFileAdapter(JavaSource source) {
		super(source.file(), Kind.SOURCE);
		this.source = source;
	}
	
	/**
	 * Just wrap around the char sequence
	 */
	@Override
	public InputStream openInputStream() throws IOException {
		return new BytesInputStream(source.asSequence().toString());
	}

	/**
	 * return char content
	 */
	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
		return source.asSequence();
	}
}
