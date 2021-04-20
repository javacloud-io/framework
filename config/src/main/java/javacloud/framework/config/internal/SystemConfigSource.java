package javacloud.framework.config.internal;

import javacloud.framework.util.Converters;
/**
 * Use in replace of System.getProperties() to inject runtime properties
 * 
 * @author tobiho
 *
 */
public final class SystemConfigSource extends StandardConfigSource {
	private static final SystemConfigSource SOURCE = new SystemConfigSource();
	
	public SystemConfigSource() {
		super(System.getProperties());
	}
	
	public static SystemConfigSource get() {
		return SOURCE;
	}
	
	public void setProperty(String name, Object value) {
		properties.put(name, Converters.toString(value));
	}
	
	public void setPropertyIfAbsent(String name, Object value) {
		properties.putIfAbsent(name, Converters.toString(value));
	}
}
