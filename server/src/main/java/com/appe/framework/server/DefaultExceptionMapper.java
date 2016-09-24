package com.appe.framework.server;

import javax.inject.Inject;

import com.appe.framework.resource.MessageBundle;
import com.appe.framework.resource.ResourceManager;

/**
 * The least caught exception handler, in case of the application doesn't have any better way to handler them.
 * 
 * @author ho
 *
 */
public final class DefaultExceptionMapper extends GenericExceptionMapper<Throwable> {
	private final MessageBundle messageBundle;
	/**
	 * 
	 * @param bundleManager
	 */
	@Inject
	public DefaultExceptionMapper(ResourceManager resourceManager) {
		this.messageBundle = resourceManager.getMessageBundle(MessageBundle.class);
	}
	
	/**
	 * Using default message bundle to translate message
	 * 
	 */
	@Override
	protected String toLocalizedMessage(String message) {
		return messageBundle.getMessage(message);
	}
}
