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
package com.appe.registry.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.text.MessageFormat;

import com.appe.registry.AppeConfig;
/**
 * Quick implementation using properties, make sure to be able to convert the message.
 * 
 * @author ho
 *
 */
public class ConfigHandlerImpl implements InvocationHandler {
	/**
	 * resolve the config value with given name
	 * 
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	protected String resolveValue(String name, String defaultValue) {
		return defaultValue;
	};
	
	/**
	 * 1. Look for the annotation to figure KEY/DEFAULT value....
	 * 2. Convert the string value to the correct target one.
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		AppeConfig.Entry entry = method.getAnnotation(AppeConfig.Entry.class);
		String name, value;
		if(entry != null) {
			name = entry.name();
			value = entry.defaultValue();
		} else {
			name = null;
			value = null;
		}
		
		//TODO: support strip of getXXX ?
		if(name == null || name.isEmpty()) {
			name = method.getName();
		}
		
		value = resolveValue(name, value);
		if(value == null || value.isEmpty()) {
			return null;
		}
		
		//ALWAYS RETURN STRING FORMATED ?
		if(args != null && args.length > 0) {
			return MessageFormat.format(value, args);
		}
		
		return convertValue(value, method.getReturnType());
	}
	
	/**
	 * Handle the object conversion to target value
	 * 
	 * @param value
	 * @param type
	 * @return
	 */
	protected Object convertValue(String value, Class<?> type) {
		//NOTHING TO CONVERT AT ALL
		if(type == String.class) {
			return value;
		}
		//AUTO BOXING to use valueOf
		if(type == boolean.class || type == Boolean.class) {
			return	Boolean.valueOf(value);
		}
		if(type == byte.class || type == Byte.class) {
			return	Byte.valueOf(value);
		}
		if(type == char.class || type == Character.class) {
			return	Character.valueOf(value.charAt(0));
		}
		if(type == short.class || type == Short.class) {
			return	Short.valueOf(value);
		}
		if(type == int.class || type == Integer.class) {
			return Integer.valueOf(value);
		}
		if(type == long.class || type == Long.class) {
			return Long.valueOf(value);
		}
		if(type == float.class || type == Float.class) {
			return Float.valueOf(value);
		}
		if(type == double.class || type == Double.class) {
			return Double.valueOf(value);
		}
		
		//UNKNOW TYPE => RETURN STRING?
		return value;
	}
}
