package com.appe.framework.security.jwt;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appe.framework.json.Externalizer;
import com.appe.framework.jwt.JwtCodecs;
import com.appe.framework.jwt.JwtException;
import com.appe.framework.jwt.JwtToken;
import com.appe.framework.jwt.JwtVerifier;
import com.appe.framework.security.AccessDeniedException;
import com.appe.framework.security.AuthenticationException;
import com.appe.framework.security.IdParameters;
import com.appe.framework.security.InvalidCredentialsException;
import com.appe.framework.security.claim.TokenGrant;
import com.appe.framework.security.claim.TokenValidator;
import com.appe.framework.util.Dictionary;

/**
 * Simple implementation of a JWT validation with a trusted signer, assuming sharing the signing key or public/private
 * 
 * @author ho
 *
 */
@Singleton
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
	
	private JwtCodecs  	jwtCodecs;
	private JwtVerifier	jwtVerifier;
	/**
	 * 
	 * @param externalizer
	 * @param jwtVerifier
	 */
	@Inject
	public JwtTokenValidator(@Named(Externalizer.JSON) Externalizer externalizer, JwtVerifier jwtVerifier) {
		this.jwtCodecs = new JwtCodecs(externalizer);
		this.jwtVerifier  = jwtVerifier;
	}
	
	/**
	 * Validate token signature to make sure no tempering.
	 * 
	 */
	@Override
	public TokenGrant validateToken(String token) throws AuthenticationException {
		Dictionary claims;
		try {
			JwtToken jwtToken = jwtCodecs.decodeJWT(token, jwtVerifier);
			claims = jwtToken.getClaims();
		} catch (JwtException ex) {
			logger.warn("Problem decoding JWT Token: {}", token, ex);
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
