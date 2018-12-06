package javacloud.framework.jacc;

import java.net.URI;

/**
 * A representation of a source file with URI
 * 
 * @author ho
 *
 */
public interface JavaSource {
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
