package com.appe.framework.impl;

import javax.inject.Singleton;

import com.appe.framework.AppeNamespace;

/**
 * Using thread local as implementation, be aware of not access directly from anywhere!
 * HOW MUCH OVER HEAD THIS CAN BE? IF WE USE NAMESPACE WITHOUT NAMESPACE?
 * 
 * @author tobi
 */
@Singleton
public class AppeNamespaceImpl implements AppeNamespace {
	private static final ThreadLocal<String> LOCAL = new ThreadLocal<String>();	//NAMESPACE
	
	/**
	 * 
	 */
	public AppeNamespaceImpl() {
	}
	
	/**
	 * Make sure to RESET the NAMESPACE HASH WHEN CHANGING NAMESPACE.
	 * TODO: SHOULD NOT MAKE CHANGE IF THEY ARE THE SAME
	 */
	@Override
	public void set(String namespace) {
		LOCAL.set(namespace);
	}
	
	/**
	 * return current NAMESPACE.
	 */
	@Override
	public String get() {
		return LOCAL.get();
	}
	
	/**
	 * 
	 */
	@Override
	public void clear() {
		LOCAL.remove();
	}
}
