package com.appe.framework.security.internal;

import java.io.IOException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appe.framework.io.BytesInputStream;
import com.appe.framework.json.Externalizer;
import com.appe.framework.jwt.JwtCodecs;
import com.appe.framework.jwt.JwtException;
import com.appe.framework.jwt.JwtSigner;
import com.appe.framework.jwt.JwtToken;
import com.appe.framework.security.AccessDeniedException;
import com.appe.framework.security.AuthenticationException;
import com.appe.framework.security.IdParameters;
import com.appe.framework.security.InvalidCredentialsException;
import com.appe.framework.security.claim.TokenGrant;
import com.appe.framework.security.claim.TokenValidator;
import com.appe.framework.util.Dictionary;

/**
 * Simple implementation of a JWT validation with a trusted signer, assuming sharing the signing key for now
 * 
 * @author ho
 *
 */
public class JwtTokenValidator implements TokenValidator {
	private static final Logger logger = LoggerFactory.getLogger(JwtTokenValidator.class);
	
	public static final String JWT_ISSUER 		= "iss";
	public static final String JWT_SUBJECT 		= "sub";
	public static final String JWT_AUDIENCE		= "aud";
	public static final String JWT_SCOPE 		= "scope";
	public static final String JWT_EXPIRATION 	= "exp";
	public static final String JWT_ISSUEDAT 	= "iat";
	
	//custom fields
	public static final String JWT_TYPE 		= "jtt";	//type
	public static final String JWT_ROLES 		= "roles";	//subject roles
	
	protected Externalizer  externalizer;
	protected JwtSigner 	jwtSigner;
	/**
	 * 
	 * @param externalizer
	 * @param jwtSigner
	 */
	public JwtTokenValidator(Externalizer externalizer, JwtSigner 	jwtSigner) {
		this.externalizer = externalizer;
		this.jwtSigner	  = jwtSigner;
	}
	
	/**
	 * Deserializing the token and unwrap correctly
	 */
	@Override
	public TokenGrant validateToken(String token) throws AuthenticationException {
		Dictionary claims;
		try {
			JwtToken jwtToken = JwtCodecs.decodeJWT(token, jwtSigner);
			claims = externalizer.unmarshal(new BytesInputStream(jwtToken.getClaims()), Dictionary.class);
		} catch (JwtException ex) {
			logger.warn("Problem decoding JWT Token: {}", token, ex);
			throw new InvalidCredentialsException();
		} catch(IOException ex) {
			logger.error("Problem decoding JWT Token: {}", token, ex);
			throw new InvalidCredentialsException();
		}
		
		//PARSE TOKEN & MAKE SURE IT's STILL GOOD
		TokenGrant grantToken = new TokenGrant();
		grantToken.setId(token);
		grantToken.setType(IdParameters.GrantType.valueOf(claims.getString(JWT_TYPE)));
		grantToken.setSubject(claims.getString(JWT_SUBJECT));
		grantToken.setAudience(claims.getString(JWT_AUDIENCE));
		grantToken.setScope(claims.getString(JWT_SCOPE));
		grantToken.setRoles(claims.getString(JWT_ROLES));
		
		grantToken.setExpireAt(new Date(claims.getLong(JWT_EXPIRATION)));
		grantToken.setIssuedAt(new Date(claims.getLong(JWT_ISSUEDAT)));
		if(TokenGrant.isExpired(grantToken)) {
			throw new AccessDeniedException(AccessDeniedException.EXPIRED_CREDENTIALS);
		}
		return grantToken;
	}
}
