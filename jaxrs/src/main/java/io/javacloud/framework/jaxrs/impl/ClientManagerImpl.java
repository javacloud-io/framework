package io.javacloud.framework.jaxrs.impl;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.client.Client;

import io.javacloud.framework.jaxrs.ClientManager;
import io.javacloud.framework.util.Objects;
import io.javacloud.framework.util.ProxyInvocationHandler;
/**
 * 
 * @author ho
 *
 */
@Singleton
public class ClientManagerImpl implements ClientManager {
	private final Client client;
	@Inject
	public ClientManagerImpl(Client client) {
		this.client = client;
	}
	
	/**
	 * return default client if compatible type
	 */
	@Override
	public <T> T getClient(Class<T> type) {
		if(type.isInstance(client)) {
			return Objects.cast(client);
		}
		
		//USING INVOCATION HANDLER
		ProxyInvocationHandler clientHandler = new ClientInvocationHandlerImpl(client);
		return ProxyInvocationHandler.newInstance(clientHandler, type);
	}
}
