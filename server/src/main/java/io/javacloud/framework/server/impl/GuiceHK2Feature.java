package io.javacloud.framework.server.impl;

import javax.inject.Inject;
import javax.ws.rs.core.FeatureContext;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ContainerException;
import org.glassfish.jersey.server.spi.Container;
import org.glassfish.jersey.server.spi.ContainerLifecycleListener;

import io.javacloud.framework.cdi.ServiceRunlist;
import io.javacloud.framework.security.AccessGrant;
/**
 * 1. If the registry is GuiceRegistry => try to find it and register with the system
 * 2. Hook container lifecycle with runlist.
 * 
 * @author ho
 *
 */
public class GuiceHK2Feature extends io.javacloud.framework.jaxrs.impl.GuiceHK2Feature {
	/**
	 * 
	 * @param serviceLocator
	 */
	@Inject
	public GuiceHK2Feature(ServiceLocator serviceLocator) {
		super(serviceLocator);
	}
	
	/**
	 * Trying to find the injector already initialize somewhere and register if FOUND
	 * 
	 */
	@Override
	public boolean configure(FeatureContext context) {
		super.configure(context);
		
		//BIND AUTHORIZATION
		context.register(new AbstractBinder() {
            @Override
            protected void configure() {
            	bindFactory(SecurityHK2Factory.class).to(AccessGrant.class);
            }
        });
		
		//REGISTER RUNLIST
		context.register(new ContainerLifecycleListener() {
			@Override
			public void onStartup(Container container) {
				try {
					ServiceRunlist.get().startServices();
				}catch(Exception ex) {
					throw new ContainerException(ex);
				}
			}
			
			@Override
			public void onShutdown(Container container) {
				try {
					ServiceRunlist.get().stopServices();
				}catch(Exception ex) {
					throw new ContainerException(ex);
				}
			}
			
			@Override
			public void onReload(Container container) {
			}
		});
		return true;
	}
}
