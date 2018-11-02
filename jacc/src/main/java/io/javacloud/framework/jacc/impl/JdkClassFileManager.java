package io.javacloud.framework.jacc.impl;

import io.javacloud.framework.jacc.ClassCollector;

import java.io.IOException;
import java.io.OutputStream;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;
/**
 * 
 * @author ho
 *
 */
public class JdkClassFileManager extends ForwardingJavaFileManager<JavaFileManager> {
	private final ClassCollector collector;
	public JdkClassFileManager(JavaFileManager fileManager, ClassCollector collector) {
		super(fileManager);
		this.collector = collector;
	}
	
	/**
	 * return writable object for class
	 */
	@Override
	public JavaFileObject getJavaFileForOutput(Location location, final String className, Kind kind, FileObject sibling)
			throws IOException {
		return new SimpleJavaFileObject(sibling.toUri(), kind) {
			@Override
			public OutputStream openOutputStream() throws IOException {
				return collector.asStream(className, uri);
			}
		};
	}
}
