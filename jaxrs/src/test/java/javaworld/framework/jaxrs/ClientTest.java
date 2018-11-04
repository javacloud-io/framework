package javaworld.framework.jaxrs;

import javacloud.framework.cdi.test.ServiceTest;
import javacloud.framework.jaxrs.ClientManager;

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
	
	@Inject ClientManager clientManager;
	@Test
	public void testClient() {
		client.target("https://172.217.5.196").request().get();
		
		Client c = clientManager.getClient(Client.class);
		Assert.assertSame(client, c);
	}
}
