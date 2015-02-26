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
package com.appe.server.startup;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appe.data.AlreadyExistsException;
import com.appe.data.ValidationException;
import com.appe.data.NotFoundException;
import com.appe.i18n.MessageBundle;
import com.appe.registry.AppeRegistry;
import com.appe.security.AccessDeniedException;
import com.appe.security.AuthorizationException;
import com.appe.util.Dictionary;
import com.appe.util.Objects;
/**
 * To be able to handle basic error nicely which always in format of {error, description}
 * 
 * @author ho
 *
 */
public class DefaultExceptionMapper implements ExceptionMapper<Throwable> {
	private static final Logger logger = LoggerFactory.getLogger(DefaultExceptionMapper.class);
	private final MessageBundle bundle;
	/**
	 * 
	 */
	public DefaultExceptionMapper() {
		this.bundle = AppeRegistry.get().getConfig(MessageBundle.class);
	}
	
	/**
	 * 
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
		} else if(exception instanceof AuthorizationException) {
			if(exception instanceof AccessDeniedException) {
				status = Status.FORBIDDEN.getStatusCode();
			} else {
				status = Status.UNAUTHORIZED.getStatusCode();
			}
		} else if(exception instanceof AlreadyExistsException) {
			status = Status.CONFLICT.getStatusCode();
		} else if(exception instanceof NotFoundException
				|| exception instanceof java.io.FileNotFoundException) {
			status = Status.NOT_FOUND.getStatusCode();
		} else if(exception instanceof ValidationException
				|| exception instanceof IllegalArgumentException) {
			status = Status.BAD_REQUEST.getStatusCode();
		} else {
			status = Status.INTERNAL_SERVER_ERROR.getStatusCode();
		}
		
		//DEBUG MESSAGE
		if(status >= 500) {
			logger.error("HTTP status: " + status, exception);
		} else if (status >= 400) {
			logger.warn("HTTP status: " + status,  exception);
		} else {
			logger.debug("HTTP status: " + status, exception);
		}
		return Response.status(status).entity(toEntity(exception)).build();
	}
	
	/**
	 * Convert exception to nicely entity {error:code, description: message}
	 * 
	 * @param exception
	 * @return
	 */
	protected Dictionary toEntity(Throwable exception) {
		String error = exception.getMessage();
		if(Objects.isEmpty(error)) {
			error = exception.getMessage();
		}
		String message = bundle.getLocalizedMessage(error);
		return Objects.asDict("error", error, "message", message);
	}
}
