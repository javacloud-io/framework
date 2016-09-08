package com.appe.framework.server.internal;

import javax.inject.Inject;

import com.appe.framework.bundle.MessageBundle;
import com.appe.framework.bundle.ResourceBundleManager;
import com.appe.framework.server.GenericExceptionMapper;

/**
 * The least caught exception handler, in case of the application doesn't have any better way to handler them.
 * 
 * @author ho
 *
 */
public final class DefaultExceptionMapper extends GenericExceptionMapper<Throwable> {
	private final MessageBundle bundle;
	/**
	 * 
	 * @param bundleManager
	 */
	@Inject
	public DefaultExceptionMapper(ResourceBundleManager bundleManager) {
		this.bundle = bundleManager.getMessageBundle(MessageBundle.class);
	}
	
	/**
	 * Using default message bundle to translate message
	 * 
	 */
	@Override
	protected String toLocalizedMessage(String message) {
		return bundle.getString(message);
	}
}
