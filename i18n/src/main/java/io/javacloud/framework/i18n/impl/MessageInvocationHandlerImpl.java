package io.javacloud.framework.i18n.impl;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import io.javacloud.framework.i18n.ContextLocale;
import io.javacloud.framework.i18n.internal.MessageBundlesControl;
import io.javacloud.framework.i18n.internal.MessageInvocationHandler;
import io.javacloud.framework.util.Objects;
import io.javacloud.framework.util.ResourceLoader;
/**
 * 
 * @author ho
 *
 */
public class MessageInvocationHandlerImpl extends MessageInvocationHandler {
	private final MessageBundlesControl bundlesControl;
	protected MessageInvocationHandlerImpl(ContextLocale contextLocale, MessageBundlesControl bundlesControl) {
		super(contextLocale);
		this.bundlesControl = bundlesControl;
	}
	
	/**
	 * 
	 */
	@Override
	protected String resolveValue(String name, String value) {
		ResourceBundle bundle = ResourceBundle.getBundle("", contextLocale.get(), ResourceLoader.getClassLoader(), bundlesControl);
		try {
			return	bundle.getString(name);
		} catch(MissingResourceException ex) {
			//ASSUMING NOT FOUND
		}
		return Objects.isEmpty(value) ? name: value;
	}
}
