package javacloud.framework.jaxrs.impl;

import javacloud.framework.config.ConfigManager;
import javacloud.framework.jaxrs.ClientApplication;
import javacloud.framework.jaxrs.ClientSettings;
import javacloud.framework.ssl.BlindTrustProvider;
import javacloud.framework.util.LazySupplier;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

/**
 * Dynamically loading jersey client from META-INF/javacloud.client.components
 * 
 * @author ho
 *
 */
@Singleton
public class HttpClientProvider extends LazySupplier<Client> implements Provider<Client> {
	private static final Logger logger = Logger.getLogger(HttpClientProvider.class.getName());
	
	private final ClientApplication application;
	private final ClientSettings settings;
	
	@Inject
	public HttpClientProvider(ClientApplication application, ConfigManager configManager) {
		this(application, configManager.getConfig(ClientSettings.class));
	}
	
	protected HttpClientProvider(ClientApplication application, ClientSettings settings) {
		this.application = application;
		this.settings = settings;
	}
	
	@Override
	protected Client newInstance() {
		ClientBuilder builder = ClientBuilder.newBuilder();
		if(settings.ignoreHostnameVerification()) {
			builder.hostnameVerifier(BlindTrustProvider.HOSTNAME_VERIFIER);
		}
		configure(builder);
		return builder.build();
	}
	
	/**
	 * Load & register components from resource file with default configuration
	 * 
	 * @return
	 */
	protected void configure(ClientBuilder builder) {
		List<?> components = application.clientComponents();
		logger.log(Level.FINE, "Registering client components: {0}", components);
		
		for(Object c: components) {
			if(c instanceof Class) {
				builder.register((Class<?>)c);
			} else {
				builder.register(c);
			}
		}
	}
}
