package javacloud.framework.config.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javacloud.framework.config.ConfigSource;
/**
 * Single source of configure with highest priority on the top
 * 
 * @author ho
 *
 */
public class ConfigSourceResolver implements ConfigSource {
	private final List<ConfigSource> sources = new ArrayList<>();
	
	public final ConfigSource cacheResolver = new ConfigSource() {
		final Map<String, String> cache = new WeakHashMap<>();
		
		@Override
		public synchronized String getProperty(String name) {
			String value = cache.get(name);
			if (value != null) {
				return value.isEmpty() ? null : value;
			}
			
			// get value from cache
			value = ConfigSourceResolver.this.getProperty(name);
			cache.putIfAbsent(name, value == null ? "" : value);
			return value;
		}
	};
	
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
		for(ConfigSource source: sources) {
			String value = source.getProperty(name);
			if(value != null) {
				return value;
			}
		}
		return null;
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
