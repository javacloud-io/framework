package com.appe.framework.server.hk2;

import javax.servlet.ServletContextEvent;

import com.appe.framework.internal.GuiceFactory;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

/**
 * Make sure to bind SERVLET with same context listener so FILTER/SERVLET & JPA WORKING.
 * 
 * <listener>
 *	   <listener-class>com.appe.framework.hk2.GuiceServletListener</listener-class>
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
	}
	
	/**
	 * return the Injector from registry.
	 */
	@Override
	protected Injector getInjector() {
		return GuiceFactory.registryInjector();
	}
}
