package io.javacloud.framework.tx;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * 
 * @author ho
 *
 */
@Inherited
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Transactional {
	/**
	 * Name of transaction for debugging
	 * @return
	 */
	String value() default "txn";
	
	/**
	 * 
	 * @return
	 */
	Propagation propagation() default Propagation.REQUIRED;
	
	/**
	 * 
	 * @return
	 */
	boolean readOnly() default false;
	
	/**
	 * 
	 * @return
	 */
	Class<? extends Exception>[] rollbackOn() default RuntimeException.class;
	
	/**
	 * 
	 * @return
	 */
	Class<? extends Exception>[] noRollbackOn() default {};
}
