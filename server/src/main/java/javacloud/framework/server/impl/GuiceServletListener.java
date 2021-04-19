package javacloud.framework.server.impl;

import javax.servlet.ServletContextEvent;

import javacloud.framework.cdi.ServiceBootstrapper;
import javacloud.framework.cdi.internal.GuiceRegistry;

import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

/**
 * Make sure to bind SERVLET with same context listener so FILTER/SERVLET & JPA WORKING.
 * 
 * <listener>
 *	   <listener-class>io.javacloud.framework.server.impl.GuiceServletListener</listener-class>
 * </listener>
 *
 * @author ho
 *
 */
public class GuiceServletListener extends GuiceServletContextListener {
	public GuiceServletListener() {
	}
	
	/**
	 * 
	 */
	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		super.contextInitialized(servletContextEvent);
		try {
			ServiceBootstrapper.get().startup();
		}catch(Exception ex) {
			throw new IllegalStateException(ex);
		}
	}
	
	/**
	 * 
	 */
	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		try {
			ServiceBootstrapper.get().shutdown();
		}catch(Exception ex) {
			throw new IllegalStateException(ex);
		} finally {
			super.contextDestroyed(servletContextEvent);
		}
	}
	
	/**
	 * return the Injector from registry.
	 */
	@Override
	protected Injector getInjector() {
		return GuiceRegistry.getInjector();
	}
}
