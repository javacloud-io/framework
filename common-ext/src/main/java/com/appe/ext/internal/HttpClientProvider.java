package com.appe.ext.internal;

import java.io.IOException;
import java.util.List;

import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.glassfish.jersey.client.ClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appe.registry.AppeLoader;

/**
 * 
 * @author ho
 *
 */
@Singleton
public class HttpClientProvider implements Provider<Client> {
	private static final Logger logger = LoggerFactory.getLogger(HttpClientProvider.class);
	private Client client;
	
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
	 * return client configuration, can be use for good testing
	 * 
	 * @return
	 */
	public ClientConfig configure() {
		ClientConfig config = new ClientConfig();
		
		//LOAD DEFAULT CONFIG
		try {
			List<Class<?>> components = AppeLoader.loadClasses("META-INF/client-components.jersey");
			logger.debug("Register jersey components: {}", components);
			for(Class<?> c: components) {
				config.register(c);
			}
		} catch(IOException | ClassNotFoundException ex) {
			//DON'T RE-THROW EXCEPTION B/C IT's NOT SOLVING ANY REAL PROBLEM
			logger.error("Unable to load jersey client components", ex);
		}
		return config;
	}
}
