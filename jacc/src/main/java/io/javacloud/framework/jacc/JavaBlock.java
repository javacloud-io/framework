package io.javacloud.framework.jacc;

/**
 * A piece of code block that can be: a file, class, method, constructor...
 * 
 * @author ho
 *
 */
public interface JavaBlock {
	/**
	 * return a reader to process content for reading
	 * 
	 * @return
	 */
	public CharSequence asSequence();
}
