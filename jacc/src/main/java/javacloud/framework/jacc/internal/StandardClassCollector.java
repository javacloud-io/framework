package javacloud.framework.jacc.internal;

import javacloud.framework.io.BytesOutputStream;
import javacloud.framework.jacc.ClassCollector;

import java.io.OutputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.security.AccessControlException;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Predicate;

/**
 * Capture the class byte code in memory for just in time lookup.
 * 
 * @author ho
 *
 */
public class StandardClassCollector extends DiagnosticCollector implements ClassCollector {
	private final Map<String, BytesOutputStream> bytesCode = new HashMap<>();
	private String mainClass;
	/**
	 * 
	 * @param mainClass
	 */
	public StandardClassCollector(String mainClass) {
		this.mainClass = mainClass;
	}
	
	/**
	 * 
	 */
	public StandardClassCollector() {
		this(null);
	}
	
	/**
	 * return main class name if has ONE
	 * 
	 * @return
	 */
	public String getMainClass() {
		return mainClass;
	}
	
	/**
	 * return a writer byte stream
	 */
	@Override
	public OutputStream asStream(String className, URI file) {
		BytesOutputStream bytes = new BytesOutputStream(1024);
		bytesCode.put(className, bytes);
		
		//ASSUMING FIST CLASS IS MAIN
		if(mainClass == null) {
			mainClass = className;
		}
		return bytes;
	}
	
	/**
	 * return byte code for the class
	 * 
	 * @param className
	 * @return
	 */
	public ByteBuffer asBytes(String className) {
		BytesOutputStream out = bytesCode.get(className);
		return (out == null? null: out.byteBuffer());
	}
	
	/**
	 * Exception will through if class is blacklisted
	 * 
	 * @param parent
	 * @param blacklisted
	 * @return
	 */
	public ClassLoader asClassLoader(ClassLoader parent, Predicate<String> blacklisted) {
		return new ClassLoader(parent) {
			@Override
			protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
				if(blacklisted.test(name)) {
					throw new AccessControlException(name + " is blacklisted");
				}
				return super.loadClass(name, resolve);
			}

			/**
			 * loadClass() always delegate to find class from PARENT first, if no such class found it will invoke this class.
			 */
			@Override
			protected Class<?> findClass(String name) throws ClassNotFoundException {
				ByteBuffer buf = asBytes(name);
				if(buf == null) {
					throw new ClassNotFoundException(name);
				}
				return defineClass(name, buf, null);
			}
		};
	}
	
	/**
	 * Allows all the class to be accessible
	 * 
	 * @param parent
	 * @return
	 */
	public ClassLoader asClassLoader(ClassLoader parent) {
		return asClassLoader(parent, (name) -> {return false;});
	}
}
