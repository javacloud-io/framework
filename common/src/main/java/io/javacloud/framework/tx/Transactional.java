package io.javacloud.framework.tx;

/**
 * 
 * @author ho
 *
 */
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
