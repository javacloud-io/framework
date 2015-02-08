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
package com.appe.registry;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Basic configuration exposes using concept of bundle/name/value. The ideal is, just a quick interface annotation
 * with all and will have it in place.
 * 
 * @Bundle("xyz")
 * public interface MyConfig {
 * 		@Key(value="xyz", defaultValue="13456")
 * 		int numberOfServer();
 * }
 * 
 * TODO:
 * -Need a way inject the config automatically, workaround today is to using a providers.
 * -Support multiple bundles overriding
 * 
 * @author ho
 *
 */
public interface AppeConfig {
	/**
	 * Which bundle to load the configuration, by default it will look from bundle name of CLASSPATH.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface Bundle {
		/**
		 * Blank bundle will use value directly from system properties
		 * 
		 * @return
		 */
		public String  name() default "";
		
		/**
		 * True if loading i18n bundle instead of config
		 * @return
		 */
		public boolean i18n()  default false;
	}
	
	/**
	 * Key to lookup for value and defaultValue if not provided from properties file.
	 * 
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface Entry {
		/**
		 * Blank name or no annotation will use the method name
		 * @return
		 */
		public String name() default "";
		
		/**
		 * Any default value if specified
		 * @return
		 */
		public String defaultValue() default "";
	}
	
	/**
	 * return a configuration annotated with any kind, just need @Bundle
	 * 
	 * @param config
	 * @return
	 */
	public <T> T get(Class<T> config);
}
