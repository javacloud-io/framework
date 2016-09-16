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
package com.appe.framework.resource.internal;

import java.beans.Introspector;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.text.MessageFormat;

import com.appe.framework.resource.ConfigBundle;
import com.appe.framework.util.Converter;
import com.appe.framework.util.Objects;
/**
 * Quick implementation using properties, make sure to be able to convert the message.
 * Make sure special method is not route to invoke()
 * 
 * @author ho
 *
 */
public class ConfigBundleHandler implements InvocationHandler {
	public ConfigBundleHandler() {
	}
	
	/**
	 * resolve the config value with given name
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	protected String resolveValue(String key, String defaultValue) {
		return defaultValue;
	};
	
	/**
	 * 1. Look for the annotation to figure KEY/DEFAULT value....
	 * 2. Convert the string value to the correct target one.
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		ConfigBundle.Entry entry = method.getAnnotation(ConfigBundle.Entry.class);
		String key, value;
		if(entry != null) {
			key = entry.key();
			value = entry.value();
		} else {
			key = null;
			value = null;
		}
		
		//USING METHOD NAME
		if(key == null || key.isEmpty()) {
			key = stripGetter(method.getName());
		}
		
		value = resolveValue(key, value);
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
	 * Simply strip off the get/is method to KEY
	 * 
	 * @param name
	 * @return
	 */
	protected String stripGetter(String name) {
		if(name.startsWith("get") || name.startsWith("set")) {
			Introspector.decapitalize(name.substring(3));
		} else if(name.startsWith("is")) {
			Introspector.decapitalize(name.substring(2));
		}
		return name;
	}
}
