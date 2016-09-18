package com.appe.framework.security.jwt;

import com.appe.framework.util.Codecs;
import com.appe.framework.util.Objects;

/**
 * Encode/decode JWT token with signer support
 * 
 * @author ho
 *
 */
public final class JwtCodecs {
	private JwtCodecs() {
	}
	
	/**
	 * <base64 header>.<base64 payload>.<base64 signature>
	 * 
	 * @param token
	 * @return
	 */
	public static String encodeJWT(JwtToken token, JwtSigner signer) {
		String header = "{\"typ\":\"" + token.getType() + "\",\"alg\":\"" + signer.getAlgorithm() + "\"}";
		String payload= Codecs.encodeBase64(Codecs.decodeUTF8(header), true)
						+ "." + Codecs.encodeBase64(token.getClaims(), true);
		return payload + "." + signer.sign(Codecs.decodeUTF8(payload));
	}
	
	/**
	 * Given a token in format above, parse the token and validate correct signature
	 * 
	 * @param token
	 * @param signer
	 * @return
	 * 
	 * @throws JwtException
	 */
	public static JwtToken decodeJWT(String token, JwtSigner signer) throws JwtException {
		if(Objects.isEmpty(token)) {
			throw new JwtException();
		}
		int idot = token.lastIndexOf('.');
		if(idot < 0) {
			throw new JwtException();
		}
		
		//VALIDATE THE PAYLOAD BEFORE CONTINUE
		String payload  = token.substring(0, idot);
		String signature= token.substring(idot + 1);
		String sexpected= signer.sign(Codecs.decodeUTF8(payload));
		if(!sexpected.equals(signature)) {
			throw new JwtException();
		}
		
		//DECODE PAYLOAD
		idot = payload.indexOf('.');
		if(idot < 0) {
			throw new JwtException();
		}
		String header = Codecs.encodeUTF8(Codecs.decodeBase64(payload.substring(0, idot), true));
		byte[] claims = Codecs.decodeBase64(payload.substring(idot + 1), true);
		
		//Parsing type/algorithm
		String type = jsonValue(header, "\"typ\"");
		String algorithm = jsonValue(header, "\"alg\"");
		return new JwtToken(type, algorithm, claims);
	}
	
	/**
	 * Simple JSON PARSING
	 * 
	 * @param json
	 * @param name
	 * @return
	 */
	static final String jsonValue(String json, String name) {
		int idx = json.indexOf(name);
		if(idx < 0) {
			return null;
		}
		idx = json.indexOf(':', idx + name.length());
		if(idx < 0) {
			return null;
		}
		
		//Value is right after inside "<value>"
		idx = json.indexOf('"', idx + 1);
		if(idx < 0) {
			return null;
		}
		int ind = json.indexOf('"', idx + 1);
		if(ind < 0) {
			return null;
		}
		return json.substring(idx + 1, ind);
	}
}
