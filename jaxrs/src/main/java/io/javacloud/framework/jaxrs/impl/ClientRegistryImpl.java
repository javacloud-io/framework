package io.javacloud.framework.jaxrs.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.client.Client;

import io.javacloud.framework.jaxrs.ClientRegistry;
import io.javacloud.framework.util.Objects;
import io.javacloud.framework.util.ResourceLoader;
/**
 * 
 * @author ho
 *
 */
@Singleton
public class ClientRegistryImpl implements ClientRegistry {
	private final Client client;
	@Inject
	public ClientRegistryImpl(Client client) {
		this.client = client;
	}
	
	/**
	 * return default client if compatible type
	 */
	@Override
	public <T> T getClient(Class<T> type) {
		if(type.isAssignableFrom(Client.class)) {
			return Objects.cast(client);
		}
		
		//USING INVOCATION HANDLER
		InvocationHandler clientHandler = new ClientInvocationHandlerImpl(client);
		return Objects.cast(Proxy.newProxyInstance(ResourceLoader.getClassLoader(), new Class<?>[]{ type }, clientHandler));
	}
}
