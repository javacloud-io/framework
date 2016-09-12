package com.appe.framework.server.hk2;

import javax.inject.Inject;
import javax.ws.rs.core.FeatureContext;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import com.appe.framework.security.AccessGrant;
/**
 * If the registry is GuiceRegistry => try to find it and register with the system
 * 
 * @author ho
 *
 */
public class GuiceHK2Feature extends com.appe.framework.hk2.GuiceHK2Feature {
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
		return true;
	}
}
