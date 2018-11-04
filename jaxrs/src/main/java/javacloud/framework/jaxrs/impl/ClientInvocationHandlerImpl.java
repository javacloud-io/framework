package javacloud.framework.jaxrs.impl;

import java.lang.reflect.Method;

import javax.ws.rs.client.Client;

import javacloud.framework.jaxrs.internal.ClientInvocationHandler;
/**
 * TO IMPLEMENT THE JAXRS ARGS -> CLIENT
 * 
 * @author ho
 *
 */
public class ClientInvocationHandlerImpl extends ClientInvocationHandler {
	private final Client client;
	/**
	 * 
	 * @param client
	 */
	public ClientInvocationHandlerImpl(Client client) {
		this.client = client;
	}
	
	/**
	 * 
	 */
	@Override
	protected Object invoke(Method method, Object[] args) throws Throwable {
		throw new UnsupportedOperationException("NOT IMPLEMENT REDIRECT TO JAXRS CLIENT: " + client);
	}
}
