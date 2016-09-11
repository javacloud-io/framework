package com.appe.framework.resource.impl;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.appe.framework.AppeLocale;

/**
 * 
 * @author ho
 *
 */
public abstract class MessageBundleHandler extends ConfigBundleHandler {
	private static final Logger logger = LoggerFactory.getLogger(MessageBundleHandler.class);
	
	protected AppeLocale appeLocale;
	public MessageBundleHandler(AppeLocale appeLocale) {
		this.appeLocale = appeLocale;
	}
	
	/**
	 * Always make a special treatment for universal getMessage() function
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		//DYNAMIC getString(name, args)
		if("getMessage".equals(method.getName()) && args != null && args.length > 0) {
			String message = resolveValue(String.valueOf(args[0]), null);
			
			//ALL ARGS will be in second
			if(args.length > 1) {
				message = formatValue(message, (Object[])args[1]);
			}
			return message;
		}
		//ALREADY FORMAT CORRECTLY
		return super.invoke(proxy, method, args);
	}
	
	/**
	 * format message with current locale
	 */
	@Override
	protected String formatValue(String value, Object[] args) {
		MessageFormat mf = new MessageFormat(value, appeLocale.get());
		return mf.format(args);
	}
	
	/**
	 * Always using value from the BUNDLE
	 */
	@Override
	protected String resolveValue(String key, String defaultValue) {
		String message;
		try {
			ResourceBundle bundle = resolveBundle();
			message = bundle.getString(key);
		}catch(MissingResourceException ex) {
			message = defaultValue;
			logger.debug(ex.getMessage());
		}
		return (message == null? key: message);
	}
	
	/**
	 * 
	 * @param callerLoader
	 * @return
	 * @throws MissingResourceException
	 */
	protected abstract ResourceBundle resolveBundle() throws MissingResourceException;
}
