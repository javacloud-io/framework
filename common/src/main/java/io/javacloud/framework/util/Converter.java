package io.javacloud.framework.util;

/**
 * Converting from something to something else. String to bytes[] will automatically assuming base64 encoded!!!
 * 
 * @author tobi
 * @param <T>
 */
public interface Converter<T> {
	/**
	 * Convert an object v to any type T, value is string or sub type of T.
	 * @param value
	 * @return
	 */
	public T to(Object value);
}
