package javacloud.framework.i18n.impl;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

import javacloud.framework.i18n.LocaleContext;
import javacloud.framework.i18n.MessageManager;
import javacloud.framework.i18n.internal.MessageBundlesControl;
import javacloud.framework.util.Exceptions;
import javacloud.framework.util.ProxyInvocationHandler;
import javacloud.framework.util.ResourceLoader;
/**
 * 
 * @author ho
 *
 */
@Singleton
public class MessageManagerImpl implements MessageManager {
	private static final Logger logger = Logger.getLogger(MessageManagerImpl.class.getName());
	
	private final LocaleContext contextLocale;
	private final MessageBundlesControl bundlesControl;
	/**
	 * 
	 * @param contextLocale
	 */
	@Inject
	public MessageManagerImpl(LocaleContext contextLocale) {
		this.contextLocale = contextLocale;
		this.bundlesControl= new MessageBundlesControl(contextLocale);
		//FIXME: bundle should be discovers using right class loader
		discoverBundles();
	}
	
	/**
	 * Re-discover resource bundles
	 */
	protected void discoverBundles() {
		try {
			bundlesControl.discoverBundles(ResourceLoader.getClassLoader());
			logger.log(Level.FINE, "Discovered i18n resource bundles: {}", bundlesControl.getBundleNames());
		}catch(IOException ex) {
			throw Exceptions.asUnchecked(ex);
		}
	}
	
	/**
	 * Lookup local string message from all bundles
	 */
	@Override
	public String getString(String key, Object... args) {
		ResourceBundle bundle = ResourceBundle.getBundle("", contextLocale.get(), ResourceLoader.getClassLoader(), bundlesControl);
		String message = bundle.getString(key);
		
		//RE-FORMAT THE MESSAGE
		if(args != null && args.length > 0) {
			message = new MessageFormat(message).format(args);
		}
		return message;
	}
	
	/**
	 * return the type safe bundle
	 */
	@Override
	public <T> T getBundle(Class<T> type) {
		ProxyInvocationHandler messageHandler = new MessageInvocationHandlerImpl(contextLocale, bundlesControl);
		return ProxyInvocationHandler.newInstance(messageHandler, type);
	}
}
