package io.javacloud.framework.tx.internal;

import java.util.concurrent.Callable;

import io.javacloud.framework.tx.Propagation;
import io.javacloud.framework.tx.Transactional;
import io.javacloud.framework.tx.spi.TxTransaction;
import io.javacloud.framework.tx.spi.TxTransactionException;
import io.javacloud.framework.tx.spi.TxTransactionManager;

/**
 * Default handle transactional invocation
 * 
 * @author ho
 *
 */
public class TxTransactionalInvocation {
	/**
	 * 
	 * @param callable
	 * @param tm
	 * @param transactional
	 * @return
	 * @throws Exception
	 */
	public <V> V invoke(Callable<V> callable, TxTransactionManager tm, Transactional transactional) throws Exception {
		Propagation propagation = transactional.propagation();
		TxTransaction tx = tm.getTransaction();
		if(tx != null && tx.isActive()) {
			if(propagation == Propagation.NEVER) {
				//DONT NEED ACTIVE TRANSACTION
				throw new TxTransactionException("An active transaction already exists");
			} else if(propagation == Propagation.NOT_SUPPORTED) {
				//SUSPEND EXISTING TX
				tx.rollback();
				return callable.call();
			} else if(propagation == Propagation.REQUIRES_NEW) {
				//SUSPEND EXISTING TX
				tx.rollback();
				return invokeNewTx(callable, tm, transactional);
			} else if(tx.getTransactional().readOnly() && !transactional.readOnly()) {
				//CUREENT TRANSACTION IS NOT SUITABLE FOR UPDATE => AUTO NEW ONE
				return invokeNewTx(callable, tm, transactional);
			}
			return callable.call();
		} else if(propagation == Propagation.MANDATORY) {
			//NEED AN ACTIVE TRANSACTION
			throw new TxTransactionException("An active transaction does not exist");
		} else if(propagation == Propagation.NEVER
				|| propagation == Propagation.NOT_SUPPORTED
				|| propagation == Propagation.SUPPORTS) {
			//DON'T NEED A TRANSACTION
			return callable.call();
		}
		return invokeNewTx(callable, tm, transactional);
	}
	
	/**
	 * 
	 * @param callable
	 * @param tm
	 * @param transactional
	 * @return
	 * @throws Exception
	 */
	protected <V> V invokeNewTx(Callable<V> callable, TxTransactionManager tm, Transactional transactional) throws Exception {
		TxTransaction tx = tm.beginTransaction(transactional);
		V result;
		try {
			result = callable.call();
		} catch(Exception ex) {
			if(isRollbackOn(ex, transactional)) {
				tx.rollback();
			} else {
				tx.commit();
			}
			throw ex;
		}
		tx.commit();
		return result;
	}
	
	/**
	 * 
	 * @param ex
	 * @param transactional
	 * @return
	 */
	protected boolean isRollbackOn(Exception ex, Transactional transactional) {
		boolean rollback = false;
		for(Class<? extends Exception> eclazz: transactional.rollbackOn()) {
			if(eclazz.isInstance(ex)) {
				rollback = true;
				break;
			}
		}
		if(rollback) {
			for(Class<? extends Exception> eclazz: transactional.noRollbackOn()) {
				if(eclazz.isInstance(ex)) {
					rollback = false;
					break;
				}
			}
		}
		return rollback;
	}
}
