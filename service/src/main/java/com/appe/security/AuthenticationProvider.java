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

import java.security.Principal;

/**
 * 
 * 
 * @author ho
 *
 */
public interface AuthenticationProvider {
	/**
	 * Authenticate credentials and return new one if success which contains more fine grant system wide permissions.
	 * return null if credentials is not appropriated to handle.
	 * 
	 * @param credentials
	 * @return
	 * @throws AuthorizationException
	 */
	public Authorization authenticate(Principal credentials) throws AuthorizationException;
}
