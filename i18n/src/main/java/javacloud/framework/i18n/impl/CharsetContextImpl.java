package javacloud.framework.i18n.impl;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.logging.Logger;

import javax.inject.Singleton;

import javacloud.framework.i18n.CharsetContext;
import javacloud.framework.util.Objects;
/**
 * 
 * @author ho
 *
 */
@Singleton
public class CharsetContextImpl implements CharsetContext {
	private static final Logger logger = Logger.getLogger(CharsetContextImpl.class.getName());
	private static final ThreadLocal<Charset> CHARSET = new ThreadLocal<Charset>();
	
	public CharsetContextImpl() {
	}
	
	/**
	 * 
	 */
	@Override
	public void set(Charset charset) {
		CHARSET.set(charset);
	}
	
	/**
	 * Set charset from string: UTF-8
	 */
	@Override
	public void set(String charset) {
		Charset cs = null;
		if (!Objects.isEmpty(charset)) {
			try {
				cs = Charset.forName(charset);
			} catch(UnsupportedCharsetException ex) {
				logger.warning("Unsupported charset: " + charset);
			}
		}
		set(cs);
	}
	
	/**
	 * return default charset
	 */
	@Override
	public Charset get() {
		Charset charset = CHARSET.get();
		return (charset == null? Charset.defaultCharset() : charset);
	}
	
	/**
	 * 
	 */
	@Override
	public void clear() {
		CHARSET.remove();
	}
}
