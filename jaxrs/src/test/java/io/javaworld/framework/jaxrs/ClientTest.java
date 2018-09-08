package io.javaworld.framework.jaxrs;

import io.javacloud.framework.cdi.ServiceTest;
import io.javacloud.framework.jaxrs.ClientFactory;

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
	
	@Inject ClientFactory clientFactory;
	@Test
	public void testClient() {
		client.target("https://172.217.5.196").request().get();
		
		Client c = clientFactory.getClient(Client.class);
		Assert.assertSame(client, c);
	}
}
