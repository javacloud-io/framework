package javacloud.framework.security.internal;

import java.util.Map;

import javacloud.framework.security.Credentials;
import javacloud.framework.util.Codecs;
import javacloud.framework.util.Objects;
/**
 * Simple authentication request, just principal & credentials. There are remoteAddress field help to identify original.
 * 
 * @author tobi
 *
 */
public class BasicCredentials implements Credentials {
	private String 		name;
	private	String 		secret;
	private Map<String, Object> attributes;
	/**
	 * 
	 * @param name
	 * @param secret
	 */
	public BasicCredentials(String name, String secret) {
		this.name 	= name;
		this.secret = secret;
	}
	
	/**
	 * return the NAME
	 */
	@Override
	public String getName() {
		return name;
	}
	
	/**
	 * Passing base64(principal:credentials)
	 * 
	 * @param base64Token
	 */
	public BasicCredentials(String base64Token) {
		String stoken = Codecs.toUTF8(Codecs.Base64Decoder.apply(base64Token, false));
		int index = stoken.indexOf(':');
		if(index >= 0) {
			this.name = stoken.substring(0, index);
			this.secret = stoken.substring(index + 1);
		} else {
			this.name = stoken;
		}
	}
	
	/**
	 * return the credentials secret
	 * 
	 * @return
	 */
	public String getSecret() {
		return secret;
	}
	
	/**
	 * Shortcut to authentication context, can be any arbitrary thing.
	 * @param name
	 * @param value
	 * @return
	 */
	public BasicCredentials withAttribute(String name, Object value) {
		//CREATE ONE IF NOT EXIST
		if(attributes == null) {
			attributes = Objects.asMap(name, value);
		} else {
			attributes.put(name, value);
		}
		return this;
	}
	
	/**
	 * Get the context value of given thing
	 * 
	 * @param name
	 * @return
	 */
	public <T> T getAttribute(String name) {
		return Objects.cast((attributes == null? null : attributes.get(name)));
	}
}
