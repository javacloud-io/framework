package javacloud.framework.server;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import javacloud.framework.security.AccessDeniedException;
import javacloud.framework.security.AuthenticationException;
import javacloud.framework.util.GenericException;
import javacloud.framework.util.Objects;
import javacloud.framework.util.ValidationException;

import com.fasterxml.jackson.core.JsonProcessingException;
/**
 * To be able to handle basic error nicely which always in format of {error, description}
 * 
 * @author ho
 *
 */
public class GenericExceptionMapper<E extends Throwable> implements ExceptionMapper<E> {
	private static final Logger logger = Logger.getLogger(GenericExceptionMapper.class.getName());
	public GenericExceptionMapper() {
	}
	
	/**
	 * Make sure to be able to map any exception to nicely JSON response.
	 * By default reason code & message name will be used.
	 * 
	 */
	@Override
	public Response toResponse(E exception) {
		int status;
		if(exception instanceof WebApplicationException) {
			Response resp = ((WebApplicationException)exception).getResponse();
			if(resp.getEntity() != null) {
				return resp;
			}
			status = resp.getStatus();
		} else {
			status = toStatus(exception);
		}
		
		//DEBUG DETAILS
		Map<String, Object> entity = toEntity(exception);
		if(status >= 500) {
			logger.log(Level.SEVERE, "HTTP status: " + status + ", details: " + entity, exception);
		}
		return Response.status(status).entity(entity).build();
	}
	
	/**
	 * Nicely translate exception to status code.
	 * 
	 */
	protected int toStatus(Throwable exception) {
		//AUTHZ
		if(exception instanceof AuthenticationException) {
			if(exception instanceof AccessDeniedException) {
				return	Status.FORBIDDEN.getStatusCode();
			} else {
				return	Status.UNAUTHORIZED.getStatusCode();
			}
		}
		//CLONFLICT
		if(exception instanceof ValidationException.Conflict) {
			return	Status.CONFLICT.getStatusCode();
		}
		//NOT FOUND
		if(exception instanceof ValidationException.NotFound
				|| exception instanceof java.io.FileNotFoundException) {
			return	Status.NOT_FOUND.getStatusCode();
		}
		//VALIDATION
		if(exception instanceof ValidationException
				|| exception instanceof javax.validation.ValidationException
				|| exception instanceof IllegalArgumentException
				|| exception instanceof JsonProcessingException) {
			return	Status.BAD_REQUEST.getStatusCode();
		}
		return	Status.INTERNAL_SERVER_ERROR.getStatusCode();
	}
	
	/**
	 * Convert exception to nicely entity {error:code, description: message}
	 * 
	 * @param exception
	 * @return
	 */
	protected Map<String, Object> toEntity(E exception) {
		//REASON ERROR
		String error = GenericException.getReason(exception);
		
		//DETAILS MESSAGE LOCALE
		String message;
		if (exception instanceof JsonProcessingException) {
			message = ((JsonProcessingException)exception).getOriginalMessage();
		} else {
			message = exception.getMessage();
		}
		
		if (Objects.isEmpty(message)) {
			message = exception.getClass().getName();
		}
		return Objects.asMap("error", error, "message", toLocalizedMessage(message));
	}
	
	/**
	 * Return the localized message if any found
	 * 
	 * @param message
	 * @return
	 */
	protected String toLocalizedMessage(String message) {
		return	message;
	}
}
