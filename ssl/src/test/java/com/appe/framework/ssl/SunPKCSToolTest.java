package com.appe.framework.ssl;

import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.appe.framework.ssl.internal.Extension;
import com.appe.framework.ssl.KeyChain;
import com.appe.framework.ssl.internal.CSR;
import com.appe.framework.ssl.internal.DN;
import com.appe.framework.ssl.internal.SunPKCSTool;
import com.appe.framework.ssl.PKCSTool;

import junit.framework.TestCase;

/**
 * 
 * @author tobi
 *
 */
public class SunPKCSToolTest extends TestCase {
	public void testSubjectDN() throws Exception {
		DN subjectDN = new DN();
		subjectDN.put(DN._CN, "test");
		subjectDN.put(DN._UID, "ho");
		subjectDN.put(DN._EMAIL, "ho@yahoo.com");
		
		KeyPair keypair = PKCSTool.createKeyPair(1024);
		
		//CREATE SELF SIGN
		CSR scrt = new CSR(subjectDN.toX500Name(), new Date(System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(730, TimeUnit.DAYS)));
		Extension.KeyUsage keyUsage = new Extension.KeyUsage();
		keyUsage.setDigitalSignature(true);
		keyUsage.setKeyCertSign(true);
		scrt.setKeyUsage(keyUsage);
		
		Extension.SubjectAltName altName = new Extension.SubjectAltName();
		altName.setDNSName("jecdis.com");
		altName.setUPName("jecdis\\hh111111111o");
		scrt.setSubjectAltName(altName);
		
		Extension.ExtKeyUsage extUsage = new Extension.ExtKeyUsage();
		extUsage.setClientAuth(true);
		extUsage.setCritical(true);
		scrt.setExtKeyUsage(extUsage);
		scrt.setBasicConstraints(true, true, -1);
		
		X509Certificate cert = SunPKCSTool.createX509(new KeyChain(keypair.getPrivate()), keypair.getPublic(), scrt);
		SunPKCSTool.parseX509Extensions(cert);
		
		System.out.println(cert);
		PKCSTool.storePEM(new KeyChain(keypair.getPrivate(), cert), System.out);
		
		//CREATE CSR
		keyUsage.setKeyCertSign(true);
		byte[] certCSR = SunPKCSTool.createCSR(new KeyChain(keypair.getPrivate(), cert), scrt);
		SunPKCSTool.createX509(new KeyChain(keypair.getPrivate(), cert), certCSR, scrt.getExpired());
	}
}
