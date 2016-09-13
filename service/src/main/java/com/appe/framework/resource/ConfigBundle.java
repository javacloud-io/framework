package com.appe.framework.resource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A simple bundle with key/value pair for any configuration purpose.
 * 
 * @author ho
 *
 */
public interface ConfigBundle {
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
		public String  value() default "";
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
		public String key() default "";
		
		/**
		 * Any default value if specified
		 * @return
		 */
		public String value() default "";
	}
}
