package javacloud.framework.jaxrs.impl;

import javax.inject.Inject;
import jakarta.ws.rs.client.Client;
import javacloud.framework.cdi.internal.IntegrationTest;

import org.junit.Test;
/**
 * 
 * @author ho
 *
 */
public class ClientIT extends IntegrationTest {
	@Inject
	private Client client;
	
	@Test
	public void testClient() {
		client.target("https://www.google.com").request().get();
	}
}
