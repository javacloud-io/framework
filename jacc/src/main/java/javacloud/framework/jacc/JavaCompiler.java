package javacloud.framework.jacc;

import java.net.URI;

/**
 * Compile java source code from a stream and emit class to collector
 * 
 * @author ho
 *
 */
public interface JavaCompiler {
	//java source
	interface Source {
		/**
		 * return file location of java source code. The file name has to match main class
		 * 
		 * @return
		 */
		public URI file();
		
		/**
		 * return a reader to process content for reading
		 * 
		 * @return
		 */
		public CharSequence asChars();
	}
	
	//java compiler options
	interface Options {
		String classpath();
	}
	/**
	 * 
	 * @param sources
	 * @param collector
	 * @return
	 */
	public boolean compile(Iterable<Source> sources, Options options, ClassCollector collector);
}
