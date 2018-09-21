package io.javacloud.framework.security.jwt;

import io.javacloud.framework.security.AccessDeniedException;
import io.javacloud.framework.security.AuthenticationException;
import io.javacloud.framework.security.IdParameters;
import io.javacloud.framework.security.InvalidCredentialsException;
import io.javacloud.framework.security.claim.TokenGrant;
import io.javacloud.framework.security.claim.TokenValidator;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import io.javacloud.framework.data.Converters;
import io.javacloud.framework.data.Dictionary;
import io.javacloud.framework.data.Externalizer;
import io.javacloud.framework.json.JwtException;
import io.javacloud.framework.json.JwtVerifier;
import io.javacloud.framework.json.internal.JwtToken;
import io.javacloud.framework.json.internal.JwtCodecs;

/**
 * Simple implementation of a JWT validation with a trusted signer, assuming sharing the signing key or public/private
 * 
 * @author ho
 *
 */
@Singleton
public class JwtTokenValidator implements TokenValidator {
	private static final Logger logger = Logger.getLogger(JwtTokenValidator.class.getName());
	
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
			logger.log(Level.WARNING, "Problem decoding JWT Token: " + token, ex);
			throw new InvalidCredentialsException();
		}
		
		//PARSE TOKEN & MAKE SURE IT's STILL GOOD
		TokenGrant grantToken = new TokenGrant();
		grantToken.setId(token);
		grantToken.setType(IdParameters.GrantType.valueOf((String)claims.get(JWT_TYPE)));
		grantToken.setSubject((String)claims.get(JWT_SUBJECT));
		grantToken.setAudience((String)claims.get(JWT_AUDIENCE));
		grantToken.setScope((String)claims.get(JWT_SCOPE));
		grantToken.setRoles((String)claims.get(JWT_ROLES));
		
		grantToken.setExpireAt(new Date(Converters.LONG.to(claims.get(JWT_EXPIRATION))));
		grantToken.setIssuedAt(new Date(Converters.LONG.to(claims.get(JWT_ISSUEDAT))));
		if(TokenGrant.isExpired(grantToken)) {
			throw new AccessDeniedException(AccessDeniedException.EXPIRED_CREDENTIALS);
		}
		return grantToken;
	}
}
