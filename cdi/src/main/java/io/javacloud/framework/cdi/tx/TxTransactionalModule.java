package io.javacloud.framework.cdi.tx;

import com.google.inject.matcher.Matchers;

import io.javacloud.framework.cdi.internal.GuiceModule;
import io.javacloud.framework.tx.Transactional;

/**
 * 
 * @author ho
 *
 */
public abstract class TxTransactionalModule extends GuiceModule {
	@Override
	protected void configure() {
		//BIND MANAGER CLASSES
		
		//TRANSACTIONAL PROCESSION
		TxTransactionalInterceptor transactionalInterceptor = newTransactionalInterceptor();
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
	protected TxTransactionalInterceptor newTransactionalInterceptor() {
		return new TxTransactionalInterceptor(new TxTransactionalInvocation());
	}
}
