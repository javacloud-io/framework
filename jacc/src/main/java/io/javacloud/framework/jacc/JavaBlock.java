package io.javacloud.framework.jacc;

import java.nio.channels.ReadableByteChannel;

/**
 * A piece of code block that can be: a file, class, method, constructor...
 * 
 * @author ho
 *
 */
public interface JavaBlock {
	/**
	 * return a reader to process content
	 * 
	 * @return
	 */
	public ReadableByteChannel asReader();
}
