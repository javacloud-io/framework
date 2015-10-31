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

import java.beans.Introspector;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.text.MessageFormat;

import com.appe.registry.AppeConfig;
import com.appe.util.Converter;
import com.appe.util.Objects;
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
		
		//USING METHOD NAME
		if(name == null || name.isEmpty()) {
			name = stripGetter(method.getName());
		}
		
		value = resolveValue(name, value);
		if(value == null || value.isEmpty()) {
			return null;
		}
		
		//ALWAYS RETURN STRING FORMATED ?
		if(args != null && args.length > 0) {
			return formatValue(value, args);
		}
		
		return convertValue(value, method.getReturnType());
	}
	
	/**
	 * Format value with arguments
	 * 
	 * @param value
	 * @param args
	 * @return
	 */
	protected String formatValue(String value, Object[] args) {
		return MessageFormat.format(value, args);
	}
	
	/**
	 * Handle the object conversion to target value
	 * 
	 * @param value
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected Object convertValue(String value, Class<?> type) {
		//NOTHING TO CONVERT AT ALL
		if(type == String.class) {
			return value;
		}
		
		//ENUM TYPE
		if(type.isEnum()) {
			return	Enum.valueOf(type.asSubclass(Enum.class), value);
		}
		
		//PRIMITIVES
		Converter<?> c = Objects.PRIMITIVES.get(type);
		if(c != null) {
			return c.convert(value);
		}
		
		//UNKNOW TYPE => RETURN STRING?
		return value;
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	protected String stripGetter(String name) {
		if(name.startsWith("get")) {
			Introspector.decapitalize(name.substring(3));
		} else if(name.startsWith("is")) {
			Introspector.decapitalize(name.substring(2));
		}
		return name;
	}
}
