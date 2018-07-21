package com.appe.framework.ssl.internal;

import java.util.Date;

import com.appe.framework.io.Dictionary;

/**
 * Simple certificate information request
 * @author tobi
 *
 */
public final class CSR {
	private	String 	subjectDN;		//subject DN
	private	Date	expired;		//date in which it will expired.
	
	private Dictionary	extensions;	//extensions OID -> Extension map
	public CSR(String subjectDN, Date	expired) {
		this.subjectDN = subjectDN;
		this.expired   = expired;
	}
	
	public String getSubjectDN() {
		return subjectDN;
	}
	
	public Date getExpired() {
		return expired;
	}
	
	/**
	 * return all extensions with OID
	 * @return
	 */
	public Dictionary getExtensions() {
		if(extensions == null) {
			extensions = new Dictionary();
		}
		return extensions;
	}
	
	/**
	 * 
	 * @param keyUsage
	 */
	public void setKeyUsage(Extension.KeyUsage keyUsage) {
		getExtensions().set(Extension.KeyUsage.OID, keyUsage);
	}
	public Extension.KeyUsage getKeyUsage() {
		return getExtensions().get(Extension.KeyUsage.OID);
	}
	
	/**
	 * 
	 * @return
	 */
	public Extension.SubjectAltName getSubjectAltName() {
		return getExtensions().get(Extension.SubjectAltName.OID);
	}

	public void setSubjectAltName(Extension.SubjectAltName subjectAltName) {
		getExtensions().set(Extension.SubjectAltName.OID, subjectAltName);
	}
	
	/**
	 * 
	 * @return
	 */
	public Extension.ExtKeyUsage getExtKeyUsage() {
		return getExtensions().get(Extension.ExtKeyUsage.OID);
	}

	public void setExtKeyUsage(Extension.ExtKeyUsage extKeyUsage) {
		getExtensions().set(Extension.ExtKeyUsage.OID, extKeyUsage);
	}
	
	/**
	 * 
	 * @return
	 */
	public Extension.BasicConstraints getBasicConstraints() {
		return getExtensions().get(Extension.BasicConstraints.OID);
	}
	/**
	 * 
	 * @param basicConstraints
	 */
	public void setBasicConstraints(Extension.BasicConstraints basicConstraints) {
		getExtensions().set(Extension.BasicConstraints.OID, basicConstraints);
	}
	
	//SHORT CUT TO SET CA
	public void setBasicConstraints(boolean cA, boolean critical, int depth) {
		Extension.BasicConstraints basicConstraints = new Extension.BasicConstraints();
		basicConstraints.setCA(cA);
		basicConstraints.setCritical(critical);
		basicConstraints.setDepth(depth);
		setBasicConstraints(basicConstraints);
	}
}
