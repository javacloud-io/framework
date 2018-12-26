package javacloud.framework.jacc.internal;

import java.util.LinkedHashSet;
import java.util.Set;

import javacloud.framework.jacc.JavaCompiler;
import javacloud.framework.util.Converters;
/**
 * 
 * @author ho
 *
 */
public class StandardOptions implements JavaCompiler.Options {
	private final String classpath;
	public StandardOptions(String classpath) {
		this.classpath = classpath;
	}
	
	@Override
	public String classpath() {
		return classpath;
	}
	
	/**
	 * By default, just combine runtime java.library.path and java.class.path for embedded compiling. 
	 * 
	 * @return
	 */
	public static final StandardOptions embedded() {
		Set<String> classpath = new LinkedHashSet<String>();
		String sep = System.getProperty("path.separator", ":");
		for(String path: Converters.toArray(System.getProperty("java.library.path"), sep, true)) {
			classpath.add(path);
		}
		for(String path: Converters.toArray(System.getProperty("java.class.path"), sep, true)) {
			classpath.add(path);
		}
		return new StandardOptions(Converters.toString(sep, (Object[])classpath.toArray(new String[classpath.size()])));
	}
}
