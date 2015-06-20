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
 * Indication of a NAMESPACE attach, can be use to validate IMPERSONATE... in an authorization context, it possibly
 * change the native of the NAMESPACE CONTEXT.
 * 
 * @author ho
 *
 */
public interface Namespaceable extends Principal {
	/**
	 * 
	 * @return
	 */
	public String getNamespace();
}
