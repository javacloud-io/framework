package javacloud.framework.i18n.impl;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javacloud.framework.i18n.LocaleContext;
import javacloud.framework.i18n.internal.MessageBundlesControl;
import javacloud.framework.i18n.internal.MessageInvocationHandler;
import javacloud.framework.util.Objects;
import javacloud.framework.util.ResourceLoader;
/**
 * 
 * @author ho
 *
 */
public class MessageInvocationHandlerImpl extends MessageInvocationHandler {
	private final MessageBundlesControl bundlesControl;
	protected MessageInvocationHandlerImpl(LocaleContext contextLocale, MessageBundlesControl bundlesControl) {
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
