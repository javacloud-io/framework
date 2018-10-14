package io.javacloud.framework.tx.internal;

import com.google.inject.matcher.Matchers;

import io.javacloud.framework.cdi.internal.GuiceModule;
import io.javacloud.framework.tx.Transactional;

/**
 * TxSessionManager, TxTransactionManager, TxServiceManager need to be binding.
 * 
 * @author ho
 *
 */
public class TxTransactionalModule extends GuiceModule {
	@Override
	protected void configure() {
		//TxSessionManager, TxTransactionManager need to be binded.
		
		//TRANSACTIONAL INTERCEPTOR
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
