package com.appe.framework.ssl.internal;

import java.security.cert.X509Certificate;

import com.appe.framework.io.Dictionary;

/**
 * Simple envelop signature & it's certificate embedded for signing purpose.
 * 
 * @author tobi
 *
 */
public final class SIG {
	public static final String OID_EMAIL_ADDRESS 		= "1.2.840.113549.1.9.1";
	public static final String OID_UNSTRUCTURED_NAME 	= "1.2.840.113549.1.9.2";
	public static final String OID_CONTENT_TYPE 		= "1.2.840.113549.1.9.3";
	public static final String OID_MESSAGE_DIGEST 		= "1.2.840.113549.1.9.4";
	public static final String OID_SIGNING_TIME 		= "1.2.840.113549.1.9.5";
	public static final String OID_COUNTERSIGNATURE 	= "1.2.840.113549.1.9.6";
	public static final String OID_CHALLENGE_PASSWORD 	= "1.2.840.113549.1.9.7";
	public static final String OID_UNSTRUCTURED_ADDRESS = "1.2.840.113549.1.9.8";
	public static final String OID_EXTENDED_CERTIFICATE_ATTRIBUTES = "1.2.840.113549.1.9.9";
	public static final String OID_ISSUER_SERIALNUMBER	= "1.2.840.113549.1.9.10";
	
	//1.2.840.113549.1.9 [11], [12] are RSA DSI proprietary
	//public static final String OID_RSA_PROPRIETARY = "RSAProprietary";
	
	// [13] ==> signingDescription, S/MIME, not used anymore
	//public static final String SMIME_SIGNING_DESC 	= "1.2.840.113549.1.9.13";
	public static final String EXTENSION_REQUEST 		= "1.2.840.113549.1.9.14";
	public static final String SMIME_CAPABILITY 		= "1.2.840.113549.1.9.15";
	public static final String SIGNING_CERTIFICATE 		= "1.2.840.113549.1.9.16.2.12";
	public static final String SIGNATURE_TIMESTAMP_TOKEN= "1.2.840.113549.1.9.16.2.14";
	
	private X509Certificate[] signers;		//signer certificate
	private byte[] data;					//actual data
	private Dictionary attributes;			//authenticated attributed
	/**
	 * 
	 * @param message
	 */
	public SIG(byte[] data, X509Certificate...signers) {
		this.data = data;
		this.signers = signers;
	}
	
	/**
	 * 
	 * @param message
	 * @param attributes
	 * @param signers
	 */
	public SIG(byte[] data, Dictionary attributes, X509Certificate...signers) {
		this.data 	= data;
		this.attributes = attributes;
		this.signers 	= signers;
	}
	
	/**
	 * The actual message if any.
	 * @return
	 */
	public byte[] getData() {
		return data;
	}
	
	/**
	 * return all signer certificates
	 * @return
	 */
	public X509Certificate[] getSigners() {
		return	signers;
	}
	
	/**
	 * return all attributes
	 * @return
	 */
	public Dictionary getAttributes() {
		return attributes;
	}
}
