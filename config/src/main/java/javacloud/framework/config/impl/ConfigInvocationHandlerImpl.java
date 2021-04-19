package javacloud.framework.config.impl;

import javacloud.framework.config.ConfigSource;
import javacloud.framework.config.internal.ConfigInvocationHandler;
import javacloud.framework.util.Objects;
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
		return (Objects.isEmpty(svalue) ? value : svalue);
	}
}
