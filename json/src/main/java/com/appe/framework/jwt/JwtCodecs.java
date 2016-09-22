package com.appe.framework.jwt;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

import javax.crypto.spec.SecretKeySpec;

import com.appe.framework.util.Codecs;
import com.appe.framework.util.Hmacs;
import com.appe.framework.util.Objects;

/**
 * Encode/decode JWT token with signer support
 * 
 * @author ho
 *
 */
public final class JwtCodecs {
	private static final String SHA256withRSA = "SHA256withRSA";
	/**
	 * NONE
	 */
	public static class NONE implements JwtSigner, JwtVerifier {
		@Override
		public String getAlgorithm() {
			return ALG_NONE;
		}

		@Override
		public String sign(String payload) {
			return "";
		}
		
		@Override
		public boolean verify(String payload, String signature) {
			return (signature != null && signature.isEmpty());
		}
	}
	
	/**
	 *	HMAC SHA-256
	 */
	public final static class HS256 implements JwtSigner, JwtVerifier {
		private Key secret;
		public HS256(byte[] secret) {
			this.secret = new SecretKeySpec(secret, Hmacs.HmacSHA2);
		}

		@Override
		public String getAlgorithm() {
			return ALG_HS256;
		}
		@Override
		public String sign(String payload) {
			return Codecs.encodeBase64(Hmacs.digest(secret, Codecs.toBytes(payload)), true);
		}

		@Override
		public boolean verify(String payload, String signature) {
			String sexpected = sign(payload);
			return sexpected.equals(signature);
		}
	}
	
	/**
	 * RS256S
	 */
	public static final class RS256S implements JwtSigner {
		private PrivateKey privateKey;
		public RS256S(PrivateKey privateKey) {
			this.privateKey = privateKey;
		}
		
		@Override
		public String getAlgorithm() {
			return ALG_RS256;
		}

		@Override
		public String sign(String payload) {
			try {
				Signature sig = Signature.getInstance(SHA256withRSA);
				sig.initSign(privateKey);
				sig.update(Codecs.toBytes(payload));
				return Codecs.encodeBase64(sig.sign(), true);
			} catch(InvalidKeyException | NoSuchAlgorithmException | SignatureException ex) {
				throw new JwtException();
			}
		}
	}
	
	/**
	 * RS256V
	 */
	public static final class RS256V implements JwtVerifier {
		private PublicKey publicKey;
		public RS256V(PublicKey publicKey) {
			this.publicKey = publicKey;
		}
		@Override
		public boolean verify(String payload, String signature) {
			try {
				Signature sig = Signature.getInstance(SHA256withRSA);
				sig.initVerify(publicKey);
				sig.update(Codecs.toBytes(payload));
				return sig.verify(Codecs.decodeBase64(signature, true));
			} catch(InvalidKeyException | NoSuchAlgorithmException | SignatureException ex) {
				throw new JwtException();
			}
		}
	}
	
	//PROTECTED
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
		String payload= Codecs.encodeBase64(Codecs.toBytes(header), true)
						+ "." + Codecs.encodeBase64(token.getClaims(), true);
		return payload + "." + signer.sign(payload);
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
	public static JwtToken decodeJWT(String token, JwtVerifier verifier) throws JwtException {
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
		if(!verifier.verify(payload, signature)) {
			throw new JwtException();
		}
		
		//DECODE PAYLOAD
		idot = payload.indexOf('.');
		if(idot < 0) {
			throw new JwtException();
		}
		String header = Codecs.toUTF8(Codecs.decodeBase64(payload.substring(0, idot), true));
		byte[] claims = Codecs.decodeBase64(payload.substring(idot + 1), true);
		
		//Parsing type/algorithm
		String type = jsonValue(header, "\"typ\"");
		String algorithm = jsonValue(header, "\"alg\"");
		return new JwtToken(type, algorithm, claims);
	}
	
	/**
	 * Simple JSON PARSING field value
	 * 
	 * @param json
	 * @param field
	 * @return
	 */
	private static String jsonValue(String json, String field) {
		int idx = json.indexOf(field);
		if(idx < 0) {
			return null;
		}
		idx = json.indexOf(':', idx + field.length());
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
