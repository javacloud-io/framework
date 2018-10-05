package io.javaworld.framework.jaxrs;

import io.javacloud.framework.cdi.ServiceTest;
import io.javacloud.framework.jaxrs.ClientRegistry;

import javax.inject.Inject;
import javax.ws.rs.client.Client;

import org.junit.Assert;
import org.junit.Test;
/**
 * 
 * @author ho
 *
 */
public class ClientTest extends ServiceTest {
	@Inject
	private Client client;
	
	@Inject ClientRegistry clientRegistry;
	@Test
	public void testClient() {
		client.target("https://172.217.5.196").request().get();
		
		Client c = clientRegistry.getClient(Client.class);
		Assert.assertSame(client, c);
	}
}
