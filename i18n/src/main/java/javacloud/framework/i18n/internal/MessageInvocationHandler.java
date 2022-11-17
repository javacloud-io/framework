package javacloud.framework.i18n.internal;

import javacloud.framework.config.internal.ConfigInvocationHandler;
import javacloud.framework.i18n.LocaleContext;

import java.text.MessageFormat;

/**
 * Dynamic config handler to ensure property value is always invalidate at run time
 * 
 * @author ho
 *
 */
public abstract class MessageInvocationHandler extends ConfigInvocationHandler {
	protected final LocaleContext contextLocale;
	
	protected MessageInvocationHandler(LocaleContext contextLocale) {
		this.contextLocale = contextLocale;
	}
	
	/**
	 * Format value with arguments and locale
	 * 
	 * @param value
	 * @param args
	 * @return
	 */
	@Override
	protected String formatValue(String value, Object[] args) {
		MessageFormat mf = new MessageFormat(value, contextLocale.get());
		return mf.format(args);
	}
}
