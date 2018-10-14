package io.javacloud.framework.tx;
/**
 * Compatible with javax transactional attributes:
 * https://docs.oracle.com/javaee/6/api/javax/ejb/TransactionAttributeType.html
 * 
 * @author ho
 *
 */
public enum Propagation {
	//If a client invokes the enterprise bean's method while the client is associated with a transaction context, the container invokes the enterprise bean's method in the client's transaction context.
	//If there is no existing transaction, an exception is thrown.
	MANDATORY,
	
	//If a client invokes the enterprise bean's method while the client is associated with a transaction context, the container invokes the enterprise bean's method in the client's transaction context.
	//If the client invokes the enterprise bean's method while the client is not associated with a transaction context, the container automatically starts a new transaction before delegating a method call to the enterprise bean method.
	REQUIRED,
		
	//The container must invoke an enterprise bean method whose transaction attribute is set to REQUIRES_NEW with a new transaction context.
	//If the client invokes the enterprise bean's method while the client is not associated with a transaction context, the container automatically starts a new transaction before delegating a method call to the enterprise bean business method.
	//If a client calls with a transaction context, the container suspends the association of the transaction context with the current thread before starting the new transaction and invoking the method. The container resumes the suspended transaction association after the method and the new transaction have been completed.
	REQUIRES_NEW,
		
	//If a client invokes the enterprise bean's method while the client is associated with a transaction context, the container invokes the enterprise bean's method in the client's transaction context.
	//If there is no existing transaction, an exception is thrown.
	NEVER,
	
	//If the client calls with a transaction context, the container performs the same steps as described in the REQUIRED case.
	//If the client calls without a transaction context, the container performs the same steps as described in the NOT_SUPPORTED case.
	//The SUPPORTS transaction attribute must be used with caution. This is because of the different transactional semantics provided by the two possible modes of execution. Only enterprise beans that will execute correctly in both modes should use the SUPPORTS transaction attribute.
	SUPPORTS,
	
	//The container invokes an enterprise bean method whose transaction attribute NOT_SUPPORTED with an unspecified transaction context.
	//If a client calls with a transaction context, the container suspends the association of the transaction context with the current thread before invoking the enterprise bean's business method. The container resumes the suspended association when the business method has completed.
	NOT_SUPPORTED,
}
