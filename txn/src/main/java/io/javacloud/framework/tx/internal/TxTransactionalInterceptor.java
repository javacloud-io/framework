package io.javacloud.framework.tx.internal;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import io.javacloud.framework.tx.Transactional;
import io.javacloud.framework.tx.spi.TxTransactionManager;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * 
 * @author ho
 * 
 */
public class TxTransactionalInterceptor implements MethodInterceptor {
	@Inject
	private TxTransactionManager transactionManager;
	
	private final TxTransactionalInvocation invocation;
	public TxTransactionalInterceptor(TxTransactionalInvocation invocation) {
		this.invocation = invocation;
	}
	
	@Override
	public Object invoke(final MethodInvocation mi) throws Throwable {
		Transactional transactional = resolveTransactional(mi);
		if(transactional == null) {
			return mi.proceed();
		}
		return invocation.invoke(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				try {
					return mi.proceed();
				}catch(Exception ex) {
					throw ex;
				}catch(Throwable t) {
					throw new InvocationTargetException(t);
				}
			}
		}, transactionManager, transactional);
	}

	/**
	 * 1. Using method annotation 2. If not found, using class annotation
	 * 
	 * @param methodInvocation
	 * @return
	 */
	protected Transactional resolveTransactional(MethodInvocation mi) {
		Transactional transactional = mi.getMethod().getAnnotation(Transactional.class);
		if(transactional == null) {
			transactional = mi.getThis().getClass().getAnnotation(Transactional.class);
		}
		return transactional;
	}
}
