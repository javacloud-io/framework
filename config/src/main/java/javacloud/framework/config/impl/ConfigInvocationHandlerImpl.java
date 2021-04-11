package javacloud.framework.config.impl;

import javacloud.framework.config.internal.ConfigInvocationHandler;
import javacloud.framework.config.internal.ConfigSource;
/**
 * Default configuration handler using single source to look up the value
 * 
 * @author ho
 *
 */
public class ConfigInvocationHandlerImpl extends ConfigInvocationHandler {
	private final ConfigSource source;
	
	public ConfigInvocationHandlerImpl(ConfigSource source) {
		this.source = source;
	}
	
	/**
	 * 
	 */
	@Override
	protected String resolveValue(String name, String value) {
		String svalue = source.getProperty(name);
		if(svalue != null) {
			return svalue;
		}
		return value;
	}
}
