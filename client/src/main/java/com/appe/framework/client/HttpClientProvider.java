package com.appe.framework.client;

import java.util.List;

import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.glassfish.jersey.client.ClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appe.framework.hk2.ComponentFactory;

/**
 * Dynamically loading jersey client from META-INF/client-components.jersey
 * 
 * @author ho
 *
 */
@Singleton
public class HttpClientProvider implements Provider<Client> {
	private static final Logger logger = LoggerFactory.getLogger(HttpClientProvider.class);
	private Client client;
	public HttpClientProvider() {
	}
	
	/**
	 * Prepare a client if haven't got ONE.
	 */
	@Override
	public Client get() {
		if(client == null) {
			ClientConfig config = configure();
			this.client = ClientBuilder.newBuilder().withConfig(config).build();
		}
		return client;
	}
	
	/**
	 * Load & register components from resource file
	 * 
	 * @return
	 */
	public ClientConfig configure() {
		ClientConfig config = new ClientConfig();
		
		List<Class<?>> components = ComponentFactory.loadComponents("META-INF/client-components.jersey");
		logger.debug("Registering jersey client components: {}", components);
		for(Class<?> c: components) {
			config.register(c);
		}
		return config;
	}
}
