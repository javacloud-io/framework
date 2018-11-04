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
package javacloud.framework.config.internal;

import javacloud.framework.config.ConfigProperty;
import javacloud.framework.util.Converters;
import javacloud.framework.util.Objects;
import javacloud.framework.util.ProxyInvocationHandler;

import java.beans.Introspector;
import java.lang.reflect.Method;
import java.text.MessageFormat;

/**
 * Dynamic config handler to ensure property value is always invalidate at run time
 * 
 * @author ho
 *
 */
public abstract class ConfigInvocationHandler extends ProxyInvocationHandler {
	protected ConfigInvocationHandler() {
	}
	
	/**
	 * DYNAMIC IMPL USING ANNOTATION
	 * 
	 * @param method
	 * @param args
	 * @return
	 * @throws Throwable
	 */
	@Override
	protected Object invoke(Method method, Object[] args) throws Throwable {
		ConfigProperty definition = method.getAnnotation(ConfigProperty.class);
		String name, value;
		if(definition != null) {
			name  = definition.name();
			value = definition.value();
		} else {
			name = null;
			value = null;
		}
		
		//USING METHOD NAME AS REPLACEMENT
		if(Objects.isEmpty(name)) {
			name = propertyName(method);
		}
		
		value = resolveValue(name, value);
		if(value == null || value.isEmpty()) {
			return null;
		}
		
		//ALWAYS RETURN STRING FORMATED ?
		if(args != null && args.length > 0) {
			value = formatValue(value, args);
		}
		
		return convertValue(value, method.getReturnType());
	}
	
	/**
	 * return property value for given name with default value if missing
	 * 
	 * @param name
	 * @param value
	 * @return
	 */
	protected abstract String resolveValue(String name, String value);
	
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
	protected Object convertValue(String value, Class<?> type) {
		return Converters.toObject(value, type);
	}
	
	/**
	 * return <className>.<methodName> as key to lookup the property value
	 * 
	 * @param method
	 * @return
	 */
	protected String propertyName(Method method) {
		String name = method.getName();
		if(name.startsWith("get") || name.startsWith("set")) {
			name = Introspector.decapitalize(name.substring(3));
		} else if(name.startsWith("is")) {
			name = Introspector.decapitalize(name.substring(2));
		}
		return method.getDeclaringClass().getName() + "." + name;
	}
}
