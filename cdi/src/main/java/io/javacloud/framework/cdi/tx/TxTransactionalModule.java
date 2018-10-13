package io.javacloud.framework.cdi.tx;

import com.google.inject.matcher.Matchers;

import io.javacloud.framework.cdi.internal.GuiceModule;
import io.javacloud.framework.tx.Transactional;

/**
 * 
 * @author ho
 *
 */
public abstract class TxTransactionalModule<Tx> extends GuiceModule {
	@Override
	protected void configure() {
		//BIND MANAGER CLASSES
		
		//TRANSACTIONAL PROCESSION
		TxTransactionalInterceptor<Tx> transactionalInterceptor = newTransactionalInterceptor();
		requestInjection(transactionalInterceptor);
		
		//class-level @Transactional
		bindInterceptor(Matchers.annotatedWith(Transactional.class), Matchers.any(), transactionalInterceptor);
		
		//method-level @Transactional
		bindInterceptor(Matchers.any(), Matchers.annotatedWith(Transactional.class), transactionalInterceptor);
	}
	
	/**
	 * 
	 * @return
	 */
	protected TxTransactionalInterceptor<Tx> newTransactionalInterceptor() {
		return new TxTransactionalInterceptor<Tx>(new TxTransactionalInvocation<Tx>());
	}
}
