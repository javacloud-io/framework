/** 
 * Copyright 2015 APPE, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.appe.server.ext;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appe.AppeException;
import com.appe.data.AlreadyExistsException;
import com.appe.data.ValidationException;
import com.appe.data.NotFoundException;
import com.appe.i18n.MessageBundle;
import com.appe.registry.AppeRegistry;
import com.appe.security.AccessDeniedException;
import com.appe.security.AuthorizationException;
import com.appe.util.Dictionary;
import com.appe.util.Objects;
import com.fasterxml.jackson.core.JsonProcessingException;
/**
 * To be able to handle basic error nicely which always in format of {error, description}
 * 
 * @author ho
 *
 */
@Provider
public class DefaultExceptionMapper implements ExceptionMapper<Throwable> {
	private static final Logger logger = LoggerFactory.getLogger(DefaultExceptionMapper.class);
	private final MessageBundle bundle;
	public DefaultExceptionMapper() {
		this.bundle = AppeRegistry.get().getConfig(MessageBundle.class);
	}
	
	/**
	 * Make sure to be able to map any exception to nicely JSON response.
	 * 
	 */
	@Override
	public Response toResponse(Throwable exception) {
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
		} else if (status >= 400) {
			logger.warn("HTTP status: {}, details: {}", status, entity, exception);
		} else {
			logger.debug("HTTP status: {}, details: {}", status, entity, exception);
		}
		return Response.status(status).entity(entity).build();
	}
	
	/**
	 * Nicely translate exception to status code.
	 * 
	 */
	protected int toStatus(Throwable exception) {
		if(exception instanceof AuthorizationException) {
			if(exception instanceof AccessDeniedException) {
				return	Status.FORBIDDEN.getStatusCode();
			} else {
				return	Status.UNAUTHORIZED.getStatusCode();
			}
		}
		
		if(exception instanceof AlreadyExistsException) {
			return	Status.CONFLICT.getStatusCode();
		}
		
		if(exception instanceof NotFoundException
				|| exception instanceof java.io.FileNotFoundException) {
			return	Status.NOT_FOUND.getStatusCode();
		}
		
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
	protected Dictionary toEntity(Throwable exception) {
		String error;
		//REASON ERROR
		if(exception instanceof AppeException) {
			error = ((AppeException)exception).getReason();
		} else {
			error = Integer.toHexString(exception.getClass().getName().hashCode());
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
		message = bundle.getLocalizedMessage(message);
		return Objects.asDict("error", error, "message", message);
	}
}
