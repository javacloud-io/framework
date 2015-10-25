package com.appe.security;

import java.util.Date;

/**
 * User access token, use on behalf of user credentials. System always lookup the user when such one is provided.
 * Just simply using type to enforce token used at the moment.
 * 
 * @author tobi
 *
 */
public class AccessToken implements Namespaceable {
	//TOKEN PURPOSE
	public static final int KIND_ACCESS_CODE 		= 0x00001;
	public static final int KIND_ACCESS_TOKEN 		= 0x00002;
	public static final int KIND_REFRESH_TOKEN		= 0x00004;
	public static final int KIND_RELAY_TOKEN		= 0x00008;
	
	//KIND OF GRANT
	public static final int KIND_UNTRUSTED_CLIENT	= 0x10000;
	public static final int KIND_TRUSTED_CLIENT		= 0x20000;
	public static final int KIND_RESOURCE_OWNER 	= 0x40000;
	
	private	String 	name;		//principal associated with token
	private int 	kind;		//type of token ~ scope
	private	Date 	expiry;		//when it's will be expired
	private	String 	namespace;	//at which space
	private	String 	value;		//base64 value of token encrypted
	public AccessToken() {
	}
	
	/**
	 * NOT SET ANY KIND
	 * @param name
	 */
	public AccessToken(String name) {
		this.name = name;
	}
	
	/**
	 * Using user name as principal.
	 */
	@Override
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getKind() {
		return kind;
	}
	public void setKind(int kind) {
		this.kind = kind;
	}

	/**
	 * 
	 * @return
	 */
	public Date getExpiry() {
		return expiry;
	}
	public void setExpiry(Date expiry) {
		this.expiry = expiry;
	}
	
	/**
	 * 
	 */
	@Override
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	/**
	 * Value of the token itself.
	 * 
	 * @return
	 */
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	
	/**
	 * return token encrypted string.
	 */
	@Override
	public String toString() {
		return value;
	}
	
	/**
	 * If expired if has expiry and too OLD.
	 * 
	 * @param token
	 * @return
	 */
	public static boolean isExpired(AccessToken token) {
		return (token.expiry != null
					&& token.expiry.getTime() < System.currentTimeMillis());
	}
}
