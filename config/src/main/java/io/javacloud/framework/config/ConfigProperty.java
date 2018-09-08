package io.javacloud.framework.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Each module should define configuration with with method annotate with ConfigProperty
 * public interface ConfigSettings {
 * 		@ConfigProperty
 * 		public String getXYZ();
 * }
 * @author ho
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ConfigProperty {
	/**
	 * Blank name or no annotation will use the method name
	 * @return
	 */
	public String name() default "";
	
	/**
	 * Any default value if specified
	 * @return
	 */
	public String value() default "";
}
