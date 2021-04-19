package javacloud.framework.i18n;

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
	void set(Charset charset);
	
	/**
	 * 
	 * @param charset
	 */
	void set(String charset);
	
	/**
	 * return the charset
	 * 
	 * @return
	 */
	Charset get();
	
	/**
	 * 
	 */
	void clear();
}
