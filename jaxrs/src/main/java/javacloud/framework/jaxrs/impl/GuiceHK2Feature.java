package javacloud.framework.jaxrs.impl;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import org.glassfish.hk2.api.ServiceLocator;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;

import com.google.inject.Injector;

import javacloud.framework.cdi.internal.GuiceRegistry;
/**
 * If the registry is GuiceRegistry => try to find it and register with the system
 * 
 * @author ho
 *
 */
public class GuiceHK2Feature implements Feature {
	private static final Logger logger = Logger.getLogger(GuiceHK2Feature.class.getName());
	private ServiceLocator serviceLocator;
	/**
	 * 
	 * @param serviceLocator
	 */
	@Inject
	public GuiceHK2Feature(ServiceLocator serviceLocator) {
		this.serviceLocator = serviceLocator;
	}
	
	/**
	 * Trying to find the injector already initialize somewhere and register if FOUND
	 * 
	 */
	@Override
	public boolean configure(FeatureContext context) {
		Injector injector = GuiceRegistry.getInjector();
		if(injector != null) {
			logger.info("Register Guice bridge to HK2");
			
			GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);
			GuiceIntoHK2Bridge guiceBridge = serviceLocator.getService(GuiceIntoHK2Bridge.class);
			guiceBridge.bridgeGuiceInjector(injector);
		}
		return true;
	}
}
