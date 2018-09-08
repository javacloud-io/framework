package io.javacloud.framework.jaxrs;
/**
 * return a client with given type implementation
 * 
 * @author ho
 *
 */
public interface ClientFactory {
	/**
	 * return a client with default settings
	 * 
	 * @param type
	 * @return
	 */
	public <T> T getClient(Class<T> type);
}
