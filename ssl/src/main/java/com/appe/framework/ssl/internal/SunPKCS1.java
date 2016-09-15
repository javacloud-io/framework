package com.appe.framework.ssl.internal;

import java.io.IOException;
import java.math.BigInteger;
import java.security.spec.KeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;

import sun.security.util.DerInputStream;
import sun.security.util.DerValue;
/** 
 * 
 * RSAPrivateKey ::= SEQUENCE {
 *   version           Version, 
 *   modulus           INTEGER,  -- n
 *   publicExponent    INTEGER,  -- e
 *   privateExponent   INTEGER,  -- d
 *   prime1            INTEGER,  -- p
 *   prime2            INTEGER,  -- q
 *   exponent1         INTEGER,  -- d mod (p-1)
 *   exponent2         INTEGER,  -- d mod (q-1) 
 *   coefficient       INTEGER,  -- (inverse of q) mod p
 *   otherPrimeInfos   OtherPrimeInfos OPTIONAL 
 */
@SuppressWarnings("restriction")
public final class SunPKCS1 {
	private BigInteger version;
	private	KeySpec keySpec;
	
	/**
	 * 
	 * @param bytes
	 * @throws IOException 
	 */
	public SunPKCS1(byte[] bytes) throws IOException {
		//DECODE THE SEQUENCE 
		DerInputStream sequence = new DerValue(bytes).toDerInputStream();
		this.version = sequence.getBigInteger();
		
		BigInteger modulus = sequence.getBigInteger();
        BigInteger publicExp = sequence.getBigInteger();
        BigInteger privateExp = sequence.getBigInteger();
        BigInteger prime1 = sequence.getBigInteger();
        BigInteger prime2 = sequence.getBigInteger();
        BigInteger exp1 = sequence.getBigInteger();
        BigInteger exp2 = sequence.getBigInteger();
        BigInteger crtCoef = sequence.getBigInteger();
        
        //Construct keyspec
        this.keySpec = new RSAPrivateCrtKeySpec(modulus, publicExp, privateExp, prime1, prime2, exp1, exp2, crtCoef);
	}
	
	/**
	 * 
	 * @return
	 */
	public BigInteger getVersion() {
		return version;
	}
	
	/**
	 * 
	 * @return
	 */
	public KeySpec getKeySpec() {
		return	keySpec;
	}
}
