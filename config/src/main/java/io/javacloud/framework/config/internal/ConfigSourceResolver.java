package io.javacloud.framework.config.internal;

import java.util.ArrayList;
import java.util.List;

import io.javacloud.framework.config.ConfigSource;
/**
 * Single source of configure with highest priority on the top
 * 
 * @author ho
 *
 */
public class ConfigSourceResolver implements ConfigSource {
	private final List<ConfigSource> sources = new ArrayList<>();
	public ConfigSourceResolver() {
	}
	
	/**
	 * 
	 * @param source
	 */
	public ConfigSourceResolver(ConfigSource source) {
		sources.add(source);
	}
	
	/**
	 * Resolve the property from highest to lowest priority.
	 */
	@Override
	public String getProperty(String name) {
		String value = null;
		for(ConfigSource source: sources) {
			value = source.getProperty(name);
			if(value != null) {
				break;
			}
		}
		return value;
	}
	
	/**
	 * Highest priority source always at the top
	 * 
	 * @param source
	 * @return
	 */
	public ConfigSourceResolver overrideBy(ConfigSource source) {
		sources.add(0, source);
		return this;
	}
}
