package io.javacloud.framework.i18n;

import java.nio.charset.Charset;

/**
 * 
 * @author ho
 *
 */
public interface CharsetContext {
	/**
	 * 
	 * @param charset
	 */
	public void set(Charset charset);
	
	/**
	 * 
	 * @param charset
	 */
	public void set(String charset);
	
	/**
	 * return the charset
	 * 
	 * @return
	 */
	public Charset get();
	
	/**
	 * 
	 */
	public void clear();
}
