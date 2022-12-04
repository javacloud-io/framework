package javacloud.framework.security.jwt;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.spec.SecretKeySpec;

import javacloud.framework.io.Externalizer;
import javacloud.framework.json.JsonConverter;
import javacloud.framework.util.Codecs;
import javacloud.framework.util.Hmacs;
import javacloud.framework.util.Objects;

/**
 * Encode/decode JWT token with signer support:
 * https://www.rfc-editor.org/rfc/rfc7519
 * 
 * @author ho
 *
 */
public final class JwtCodecs {
	private static final Logger logger = Logger.getLogger(JwtCodecs.class.getName());
	private static final String SHA256withRSA = "SHA256withRSA";
	
	private final JsonConverter converter;
	
	public JwtCodecs(Externalizer externalizer) {
		if (externalizer instanceof JsonConverter) {
			this.converter = (JsonConverter)externalizer;
		} else {
			this.converter = new JsonConverter(externalizer);
		}
	}
	
	/**
	 * <base64 header>.<base64 payload>.<base64 signature>
	 * 
	 * @param token
	 * @param supplier
	 * 
	 * @return
	 * @throws JwtException
	 */
	public String encodeJWT(JwtToken token, JwtSigner.Supplier supplier) throws JwtInvalidException {
		try {
			JwtSigner signer = supplier.get();
			if (signer == null) {
				logger.warning("Not found signer");
				throw new JwtInvalidException();
			}
			byte[] header = converter.toBytes(
				Objects.asMap(JwtToken.HEADER_TYPE, token.getType(),
							  JwtToken.HEADER_ALGO, signer.algorithm(),
							  JwtToken.HEADER_KEYID, signer.keyId())
			);
			
			byte[] claims = converter.toBytes(token.getClaims());
			String payload = Codecs.Base64Encoder.apply(header, true) + "." + Codecs.Base64Encoder.apply(claims, true);
			String signature = Codecs.Base64Encoder.apply(signer.sign(Codecs.toBytes(payload)), true);
			return payload + "." + signature;
		} catch (IOException ex) {
			logger.log(Level.WARNING, "Problem encode JWT token", ex);
			throw new JwtInvalidException();
		}
	}
	
	/**
	 * Given a token in format above, parse the token and validate correct signature
	 * 
	 * @param token
	 * @param supplier
	 * @return
	 * 
	 * @throws JwtException
	 */
	public JwtToken decodeJWT(String token, JwtVerifier.Supplier supplier) throws JwtInvalidException {
		if (Objects.isEmpty(token)) {
			throw new JwtInvalidException();
		}
		int idot = token.lastIndexOf('.');
		if (idot < 0) {
			throw new JwtInvalidException();
		}
		
		//VALIDATE THE PAYLOAD BEFORE CONTINUE
		String payload  = token.substring(0, idot);
		String signature= token.substring(idot + 1);
		
		// header.claims
		idot = payload.indexOf('.');
		if (idot < 0) {
			throw new JwtInvalidException();
		}
		String pheader = payload.substring(0, idot);
		String pclaims = payload.substring(idot + 1);
		try {
			Map<String, Object> header = converter.toObject(Codecs.Base64Decoder.apply(pheader, true),  Map.class);
			String kid = (String)header.get(JwtToken.HEADER_KEYID);
			JwtVerifier verifier = supplier.get(kid);
			if (verifier == null) {
				logger.warning("Not found verifier for key: " + kid);
				throw new JwtInvalidException();
			}
			
			// verify signature
			if (!verifier.verify(Codecs.toBytes(payload), Codecs.Base64Decoder.apply(signature, true))) {
				throw new JwtInvalidException();
			}
			
			// decode claims
			Map<String, Object> claims = converter.toObject(Codecs.Base64Decoder.apply(pclaims, true), Map.class);
			return new JwtToken((String)header.get(JwtToken.HEADER_TYPE), (String)header.get(JwtToken.HEADER_ALGO), claims);
		} catch (IOException ex) {
			logger.log(Level.WARNING, "Problem decode JWT token", ex);
			throw new JwtInvalidException();
		}
	}
	
	/**
	 * NONE
	 */
	public static class NONE implements JwtSigner, JwtVerifier {
		@Override
		public String algorithm() {
			return ALG_NONE;
		}

		@Override
		public byte[] sign(byte[] payload) {
			return payload;
		}
		
		@Override
		public boolean verify(byte[] payload, byte[] signature) {
			return Arrays.equals(payload, signature);
		}
	}
	
	/**
	 *	HMAC SHA-256
	 */
	public final static class HS256 implements JwtSigner, JwtVerifier {
		private final Key secret;
		
		public HS256(byte[] secret) {
			this(new SecretKeySpec(secret, Hmacs.HmacSHA2));
		}
		
		public HS256(Key secret) {
			this.secret = secret;
		}
		
		@Override
		public String algorithm() {
			return ALG_HS256;
		}
		
		@Override
		public byte[] sign(byte[] payload) {
			return Hmacs.digest(secret, payload);
		}

		@Override
		public boolean verify(byte[] payload, byte[] signature) {
			byte[] expected = sign(payload);
			return Arrays.equals(expected, signature);
		}
	}
	
	/**
	 * RS256S
	 */
	public static final class RS256S implements JwtSigner {
		private final PrivateKey privateKey;
		
		public RS256S(PrivateKey privateKey) {
			this.privateKey = privateKey;
		}
		
		@Override
		public String algorithm() {
			return ALG_RS256;
		}

		@Override
		public byte[] sign(byte[] payload) {
			try {
				Signature sig = Signature.getInstance(SHA256withRSA);
				sig.initSign(privateKey);
				sig.update(payload);
				return sig.sign();
			} catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException ex) {
				throw new JwtInvalidException();
			}
		}
	}
	
	/**
	 * RS256V
	 */
	public static final class RS256V implements JwtVerifier {
		private final PublicKey publicKey;
		
		public RS256V(PublicKey publicKey) {
			this.publicKey = publicKey;
		}
		
		@Override
		public boolean verify(byte[] payload, byte[] signature) {
			try {
				Signature sig = Signature.getInstance(SHA256withRSA);
				sig.initVerify(publicKey);
				sig.update(payload);
				return sig.verify(signature);
			} catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException ex) {
				throw new JwtInvalidException();
			}
		}
		
		// instance of modulus & exponent
		public static RS256V of(String n, String e) throws InvalidKeySpecException, NoSuchAlgorithmException {
			return of(Codecs.Base64Decoder.apply(n, true), Codecs.Base64Decoder.apply(e, true));
		}
		
		public static RS256V of(byte[] n, byte[] e) throws InvalidKeySpecException, NoSuchAlgorithmException {
			BigInteger modulus = new BigInteger(1, n);
			BigInteger exponent = new BigInteger(1, e);
			RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(modulus, exponent);
			return new RS256V(KeyFactory.getInstance("RSA").generatePublic(publicKeySpec));
		}
	}
}
