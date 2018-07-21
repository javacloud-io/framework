package com.appe.framework.server;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appe.framework.AppeException;
import com.appe.framework.util.Objects;
import com.appe.framework.io.AlreadyExistsException;
import com.appe.framework.io.Dictionary;
import com.appe.framework.io.NotFoundException;
import com.appe.framework.io.ValidationException;
import com.appe.framework.security.AccessDeniedException;
import com.appe.framework.security.AuthenticationException;
import com.fasterxml.jackson.core.JsonProcessingException;
/**
 * To be able to handle basic error nicely which always in format of {error, description}
 * 
 * @author ho
 *
 */
public abstract class GenericExceptionMapper<E extends Throwable> implements ExceptionMapper<E> {
	private static final Logger logger = LoggerFactory.getLogger(GenericExceptionMapper.class);
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
		Dictionary entity = toEntity(exception);
		if(status >= 500) {
			logger.error("HTTP status: {}, details: {}", status, entity, exception);
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
		if(exception instanceof AlreadyExistsException) {
			return	Status.CONFLICT.getStatusCode();
		}
		//NOT FOUND
		if(exception instanceof NotFoundException
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
	protected Dictionary toEntity(E exception) {
		String error;
		//REASON ERROR
		if(exception instanceof AppeException) {
			error = ((AppeException)exception).getReason();
		} else {
			error = AppeException.findReason(exception);
		}
		
		//DETAILS MESSAGE LOCALE
		String message;
		if(exception instanceof JsonProcessingException) {
			message = ((JsonProcessingException)exception).getOriginalMessage();
		} else {
			message = exception.getMessage();
		}
		
		if(Objects.isEmpty(message)) {
			message = exception.getClass().getName();
		}
		return Objects.asDict("error", error, "message", toLocalizedMessage(message));
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
