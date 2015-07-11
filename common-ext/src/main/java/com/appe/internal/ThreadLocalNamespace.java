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
package com.appe.internal;

import javax.inject.Singleton;

import com.appe.AppeNamespace;

/**
 * Using thread local as implementation, be aware of not access directly from anywhere!
 * HOW MUCH OVER HEAD THIS CAN BE? IF WE USE NAMESPACE WITHOUT NAMESPACE?
 * 
 * @author tobi
 */
@Singleton
public class ThreadLocalNamespace implements AppeNamespace {
	private static final ThreadLocal<String> LOCAL = new ThreadLocal<String>();	//NAMESPACE
	
	/**
	 * 
	 */
	public ThreadLocalNamespace() {
	}
	
	/**
	 * Make sure to RESET the NAMESPACE HASH WHEN CHANGING NAMESPACE.
	 * TODO: SHOULD NOT MAKE CHANGE IF THEY ARE THE SAME
	 */
	@Override
	public void set(String namespace) {
		LOCAL.set(namespace);
	}
	
	/**
	 * return current NAMESPACE.
	 */
	@Override
	public String get() {
		return LOCAL.get();
	}
	
	/**
	 * 
	 */
	@Override
	public void clear() {
		LOCAL.remove();
	}
}
