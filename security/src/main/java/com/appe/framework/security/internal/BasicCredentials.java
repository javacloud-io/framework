package com.appe.framework.security.internal;

import com.appe.framework.security.Credentials;
import com.appe.framework.util.Codecs;
import com.appe.framework.util.Dictionary;
/**
 * Simple authentication request, just principal & credentials. There are remoteAddress field help to identify original.
 * 
 * @author tobi
 *
 */
public class BasicCredentials implements Credentials {
	private String 		name;
	private	String 		secret;
	private Dictionary 	attributes;
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
		String stoken = Codecs.toUTF8(Codecs.decodeBase64(base64Token, false));
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
			attributes = new Dictionary();
		}
		attributes.set(name, value);
		return this;
	}
	
	/**
	 * Get the context value of given thing
	 * 
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getAttribute(String name) {
		return (T)(attributes == null? null : attributes.get(name));
	}
}
