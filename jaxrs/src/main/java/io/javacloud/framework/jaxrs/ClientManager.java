package io.javacloud.framework.jaxrs;
/**
 * return a client with given type implementation. The default javax.ws.rs.client.Client is injectable directly.
 * 
 * @author ho
 *
 */
public interface ClientManager {
	/**
	 * return a client with default settings
	 * 
	 * @param type
	 * @return
	 */
	public <T> T getClient(Class<T> type);
}
