package com.appe.framework.ssl.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import com.appe.framework.util.Dictionary;

/**
 * Use to construct DN, it's just have a single VALUE. It's representation in nature ORDER.
 * @author tobi
 *
 */
public final class DN extends Dictionary {
	public static final String CN 		= "CN";	//common name
	public static final String ORG  	= "O";	//organization
	public static final String OU  		= "OU";	//organization unit
	public static final String CITY 	= "L";	//city name
	public static final String STATE	= "S";	//state name
	public static final String COUNTRY  = "C";	//country
	
	public static final String UID	 	= "UID";
	public static final String EMAIL	= "EMAIL";
	
	public DN() {
		super(new LinkedHashMap<String, Object>());
	}
	
	/**
	 * Assuming using simple NAME
	 * @param name
	 * @throws InvalidNameException 
	 */
	public DN(String name) throws InvalidNameException {
		this();
		
		//TRANSLATE TO MAP (REVERED)
		LdapName ldapName = new LdapName(name);
		for(int i = ldapName.size() - 1; i >=0; i--) {
			Rdn rdn = ldapName.getRdn(i);
			put(rdn.getType(), rdn.getValue());
		}
	}
	
	/**
	 * Convert back to LDAP name, in order of the map ENTRIEs
	 * @throws InvalidNameException 
	 */
	public String toX500Name() throws InvalidNameException {
		List<Rdn> rdns = new ArrayList<Rdn>(size());
		for(Map.Entry<String, Object> entry: entrySet()) {
			rdns.add(new Rdn(entry.getKey(), entry.getValue()));
		}
		Collections.reverse(rdns);
		return new LdapName(rdns).toString();
	}
}
