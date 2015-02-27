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
package com.appe;

/**
 * Unchecked exception to deal with most of the RUNTIME problem. Just to avoid alot of try cache.
 * @author aimee
 *
 */
public class AppeException extends RuntimeException {
	private static final long serialVersionUID = -7421569948910282662L;
	protected AppeException() {
	}
	/**
	 * 
	 * @param cause
	 */
	protected AppeException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * 
	 * @param message
	 */
	public AppeException(String message) {
		super(message);
	}
	
	/**
	 * 
	 * @param message
	 * @param cause
	 */
	public AppeException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * 
	 * @throws Throwable
	 */
	public void rethrow() throws Throwable {
		Throwable th = super.getCause();
		if(th != null) {
			throw th;
		}
	}
	
	/**
	 * return a reason code so we can identify the problem, make sure reason is consistent!!!
	 * 
	 * @return
	 */
	public String getReason() {
		return Integer.toHexString(getClass().getName().hashCode());
	}
	
	/**
	 * Try to find cause T and throw it
	 * 
	 * @param causedBy
	 * @throws T
	 */
	public <T extends Throwable> void rethrow(Class<T> causedBy) throws T {
		T t = findCause(this, causedBy);
		if(t != null) {
			throw t;
		}
	}
	
	/**
	 * Find the root cause of the exception at any KIND.
	 * 
	 * @param t
	 * @return
	 */
	public static final Throwable findRootCause(Throwable t) {
		Throwable cause = (t != null ? t.getCause() : null);
		while(cause != null) {
			t = cause;
			cause = t.getCause();
		}
		return t;
	}
	
	/**
	 * Find the root cause which is kind of causedBy.
	 * return NULL if NOT FOUND ANY.
	 * 
	 * @param t
	 * @param causedBy
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static final <T extends Throwable> T findCause(Throwable t, Class<T> causedBy) {
		Throwable cause = (t != null ? t.getCause() : null);
		while(cause != null) {
			//OK, found it...
			if(causedBy.isAssignableFrom(cause.getClass())) {
				return (T)cause;
			}
			//OK, recursive
			cause = cause.getCause();
		}
		return null;
	}
	
	/**
	 * Test to see if the exception is cause this exception 
	 * 
	 * @param t
	 * @param causeBy
	 * @return
	 */
	public static final boolean isCausedBy(Throwable t, Class<? extends Throwable> causedBy) {
		//BASIC CHECK
		if(t == null) {
			return false;
		}
		if(causedBy.isAssignableFrom(t.getClass())) {
			return true;
		}
		//Hunt it down.
		return (findCause(t, causedBy) != null);
	}
	
	/**
	 * Make sure to always return a single AppeException instead of stack of them.
	 * @param t
	 */
	public static final AppeException wrap(Throwable t) {
		if(t instanceof AppeException) {
			return	(AppeException)t;
		}
		return	new AppeException(t);
	}
}
