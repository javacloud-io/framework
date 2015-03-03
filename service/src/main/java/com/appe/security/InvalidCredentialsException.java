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

import com.appe.security.AuthenticationException;
/**
 * 
 * @author tobi
 *
 */
public class InvalidCredentialsException extends AuthenticationException {
	private static final long serialVersionUID = 5341577128925410681L;
	/**
	 * 
	 */
	public InvalidCredentialsException() {
		this(INVALID_CREDENTIALS);
	}
	
	/**
	 * 
	 * @param message
	 */
	public InvalidCredentialsException(String message) {
		super(message);
	}
}
