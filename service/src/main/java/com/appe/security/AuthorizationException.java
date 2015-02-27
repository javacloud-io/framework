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
package com.appe.security;

import com.appe.AppeException;
import com.appe.util.Objects;
/**
 * 
 * @author aimee
 *
 */
public class AuthorizationException extends AppeException {
	private static final long serialVersionUID = -3499627145582890978L;
	
	public static final String UNAUTHORIZED_CLIENT 	= "unauthorized_client";
	public static final String INVALID_CREDENTIALS 	= "invalid_credentials";
	public static final String INVALID_SCOPE 		= "invalid_scope";
	public static final String ACCESS_DENIED 		= "access_denied";
	/**
	 * 
	 * @param cause
	 */
	public AuthorizationException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * 
	 * @param message
	 */
	public AuthorizationException(String message) {
		super(message);
	}
	
	/**
	 * ALWAYS USING MESSAGE AS REASON CODE
	 */
	@Override
	public String getReason() {
		String message = super.getMessage();
		return Objects.isEmpty(message) ? super.getReason() : message;
	}
}
