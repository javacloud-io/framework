package io.javacloud.framework.security.jwt;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import io.javacloud.framework.data.Converters;
import io.javacloud.framework.data.Dictionary;
import io.javacloud.framework.data.Externalizer;

import io.javacloud.framework.security.AccessDeniedException;
import io.javacloud.framework.security.AuthenticationException;
import io.javacloud.framework.security.IdParameters;
import io.javacloud.framework.security.claim.TokenGrant;
import io.javacloud.framework.security.claim.TokenValidator;

/**
 * Simple implementation of a JWT validation with a trusted signer, assuming sharing the signing key or public/private
 * 
 * @author ho
 *
 */
@Singleton
public class JwtTokenValidator implements TokenValidator {
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
		JwtToken jwtToken = jwtCodecs.decodeJWT(token, jwtVerifier);
		Dictionary claims = jwtToken.getClaims();
			
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
