package io.javacloud.framework.jaxrs.impl;

import io.javacloud.framework.config.ConfigManager;
import io.javacloud.framework.jaxrs.ClientSettings;
import io.javacloud.framework.jaxrs.internal.ComponentBuilder;
import io.javacloud.framework.ssl.BlindTrustProvider;
import io.javacloud.framework.util.ResourceLoader;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.glassfish.jersey.client.ClientConfig;

/**
 * Dynamically loading jersey client from META-INF/javacloud.client.components
 * 
 * @author ho
 *
 */
@Singleton
public class HttpClientProvider implements Provider<Client> {
	private static final String CLIENT_COMPONENTS = ResourceLoader.META_INF + "javacloud.client.components";
	private static final Logger logger = Logger.getLogger(HttpClientProvider.class.getName());
	
	//DEFAULT CLIENT
	private Client client;
	private final ClientSettings settings;
	
	@Inject
	public HttpClientProvider(ConfigManager configManager) {
		this.settings = configManager.getConfig(ClientSettings.class);
	}
	
	/**
	 * FIXME: Enable custom trusted SSL validation
	 */
	@Override
	public Client get() {
		if(client == null) {
			this.client = newBuilder().build();
		}
		return client;
	}
	
	/**
	 * return default builder with configuration
	 * 
	 * @return
	 */
	protected ClientBuilder newBuilder(){
		ClientConfig config = configure();
		ClientBuilder builder = ClientBuilder.newBuilder().withConfig(config);
		if(settings.ignoreHostnameVerification()) {
			builder.hostnameVerifier(BlindTrustProvider.HOSTNAME_VERIFIER);
		}
		return builder;
	}
	
	/**
	 * Load & register components from resource file with default configuration
	 * 
	 * @return
	 */
	protected ClientConfig configure() {
		List<?> components = new ComponentBuilder().build(CLIENT_COMPONENTS, ResourceLoader.getClassLoader());
		logger.log(Level.FINE, "Registering client components: {0}", components);
		
		ClientConfig config = new ClientConfig();
		for(Object c: components) {
			if(c instanceof Class) {
				config.register((Class<?>)c);
			} else {
				config.register(c);
			}
		}
		return config;
	}
}
