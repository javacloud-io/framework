package com.appe.framework.jwt;

import com.appe.framework.jwt.JwtCodecs;
import com.appe.framework.jwt.JwtSigner;
import com.appe.framework.jwt.JwtToken;

import junit.framework.TestCase;
/**
 * 
 * @author ho
 *
 */
public class JwtTokenTest extends TestCase {
	private static JwtSigner signer = new JwtSigner.HS256("a secret key".getBytes());
	
	public void testToken() {
		JwtToken token = new JwtToken("JWT", "{}".getBytes());
		String stoken = JwtCodecs.encodeJWT(token, signer);
		
		System.out.print(stoken);
		JwtToken tt = JwtCodecs.decodeJWT(stoken, signer);
		assertEquals(token.getType(), tt.getType());
		assertEquals(signer.getAlgorithm(), tt.getAlgorithm());
	}
}
