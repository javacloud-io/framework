package io.javacloud.framework.tx;
/**
 * Compatible with javax transactional type
 * 
 * @author ho
 *
 */
public enum Propagation {
	//Support a current transaction, throw an exception if none exists.
	//If called outside a transaction context, a TransactionalException with a nested TransactionRequiredException must be thrown.
	MANDATORY,
	
	//Support a current transaction, create a new one if none exists.
	//If called outside a transaction context, the interceptor must begin a new JTA transaction, the managed bean method execution must then continue inside this transaction context, and the transaction must be completed by the interceptor.
	REQUIRED,
		
	//Create a new transaction, and suspend the current transaction if one exists.
	//If called outside a transaction context, the interceptor must begin a new JTA transaction, the managed bean method execution must then continue inside this transaction context, and the transaction must be completed by the interceptor.
	REQUIRES_NEW,
		
	//Execute non-transactionally, throw an exception if a transaction exists.
	//If called outside a transaction context, managed bean method execution must then continue outside a transaction context.
	NEVER,
	
	//Support a current transaction, execute non-transactionally if none exists.
	//If called outside a transaction context, managed bean method execution must then continue outside a transaction context.
	SUPPORTS,
	
	//Execute non-transactionally, suspend the current transaction if one exists.
	//If called outside a transaction context, managed bean method execution must then continue outside a transaction context.
	NOT_SUPPORTED,
}
