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
package com.appe.data;

import java.util.regex.Pattern;

import com.appe.AppeException;
import com.appe.data.DataCondition;

/**
 * Better exception for anything related to validation...
 * 
 * @author tobi
 *
 */
public class ConstraintException extends AppeException {
	private static final long serialVersionUID = 8992210783233564530L;
	//DOMAIN & EMAIL
	public static final String REGEX_DOMAIN = "^[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*$";
	public static final String REGEX_EMAIL 	= "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	//User input ID, alpha numeric
	public static final String REGEX_ID 	= "^[_\\p{L}0-9-@\\.:]+$";
	
	private String	name;
	private DataCondition condition;
	/**
	 * A policy with its name if applicable.
	 * 
	 * @param name
	 * @param message
	 */
	public ConstraintException(String name, DataCondition condition) {
		super(condition.toString());
		this.name 	= name;
		this.condition = condition;
	}
	
	/**
	 * return a single policy name.
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * 
	 * @return
	 */
	public DataCondition getCondition() {
		return condition;
	}
	
	/**
	 * return constraint condition
	 * @param name
	 * @param value
	 * @param condition
	 */
	public static void assertCondition(String name, Object value, DataCondition condition) {
		if(!condition.test(value)) {
			throw new ConstraintException(name, condition);
		}
	}
	
	/**
	 * Make sure string value meet minimum length.
	 * @param name
	 * @param value
	 * @param minLength
	 */
	public static void assertMinLength(String name, String value, int minLength) {
		if(value == null || value.length() < minLength) {
			throw new ConstraintException(name, DataCondition.GE(minLength));
		}
	}
	
	/**
	 * Make sure string value meet maximum length
	 * @param name
	 * @param value
	 * @param maxLength
	 */
	public static void assertMaxLength(String name, String value, int maxLength) {
		if(value != null && value.length() > maxLength) {
			throw new ConstraintException(name, DataCondition.LE(maxLength));
		}
	}
	
	/**
	 * Make sure the value matching the pattern, throw exception if not match.
	 * @param name
	 * @param value
	 * @param pattern
	 */
	public static void assertRegex(String name, String value, String pattern) {
		if(value == null || !Pattern.matches(pattern, value)) {
			throw new ConstraintException(name, DataCondition.NE(pattern));
		}
	}
	
	/**
	 * Make sure the ID is valid
	 * @param name
	 * @param value
	 */
	public static void assertId(String name, String value) {
		assertRegex(name, value, REGEX_ID);
	}
	
	/**
	 * Make sure domain is valid
	 * 
	 * @param name
	 * @param value
	 */
	public static void assertDomain(String name, String value) {
		assertRegex(name, value, REGEX_DOMAIN);
	}
}
