package javacloud.framework.jaxrs;

import javacloud.framework.config.ConfigProperty;

/**
 * Default settings for http client
 * 
 * @author ho
 *
 */
public interface ClientSettings {
	/**
	 * 
	 * @return
	 */
	@ConfigProperty(name="javacloud.jaxrs.ssl.ignoreHostnameVerification", value="false")
	public boolean ignoreHostnameVerification();
}
