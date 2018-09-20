package io.javacloud.framework.jacc;

import java.net.URI;

/**
 * A representation of a source file with URI
 * @author ho
 *
 */
public interface JavaSource extends JavaBlock {
	/**
	 * return file location of java source code
	 * @return
	 */
	public URI file();
}
