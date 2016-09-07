package com.appe.framework.server.internal;

import javax.inject.Inject;

import com.appe.framework.bundle.MessageBundle;
import com.appe.framework.bundle.ResourceBundleManager;

/**
 * The least caught exception handler, in case of the application doesn't have any better way to handler them.
 * 
 * @author ho
 *
 */
public final class GenericExceptionMapper extends DefaultExceptionMapper<Throwable> {
	private final MessageBundle bundle;
	@Inject
	public GenericExceptionMapper(ResourceBundleManager bundleManager) {
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
