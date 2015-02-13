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
import java.util.Set;

/**
 * Generic principal which can hold anything. Just implements principal for anything.
 * 
 * @author aimee
 *
 */
public abstract class Authorization implements Principal {
	protected Authorization() {
		
	}
	
	/**
	 * BY DEFAULT RETURN THE SAME as PRINCIPAL!
	 */
	@Override
	public String getName() {
		Principal principal = getPrincipal();
		return (principal == null? null : principal.getName());
	}

	/**
	 * return the principal if any associated besides me.
	 * @return
	 */
	public abstract Principal getPrincipal();
	
	/**
	 * return the permission set of this authentication, by default it will be NONE.
	 * ONLY GRANTED authentication should have roles.
	 * 
	 * @return
	 */
	public abstract Set<String> getPermissions();
	
	/**
	 * return true if authentication has all permission
	 * @param permission
	 * @return
	 */
	public boolean hasAllPermissions(String ...permissions) {
		//NOT ANY PERMISSIONs
		Set<String> _permissions = getPermissions();
		if(_permissions == null || _permissions.isEmpty()) {
			return false;
		}
				
		//HAS NOTHING
		if(permissions == null || permissions.length == 0) {
			return true;
		}
		
		//SCAN THEM ALL
		for(String name: permissions) {
			if(!_permissions.contains(name)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * return true if has any role which is the basic of all use cases.
	 * @param permissions
	 * @return
	 */
	public boolean hasAnyPermissions(String ...permissions) {
		//NOT ANY PERMISSIONS
		Set<String> _permissions = getPermissions();
		if(_permissions == null || _permissions.isEmpty()) {
			return false;
		}
				
		//NOTHING AT ALL
		if(permissions == null || permissions.length == 0) {
			return true;
		}
		
		//FIND ONE IS ENOUGH
		for(String name: permissions) {
			if(_permissions.contains(name)) {
				return true;
			}
		}
		return false;
	}
}
