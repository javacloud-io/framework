package io.javacloud.framework.cdi.tx;

import java.util.concurrent.Callable;

import io.javacloud.framework.tx.Propagation;
import io.javacloud.framework.tx.TransactionException;
import io.javacloud.framework.tx.Transactional;

/**
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
	public <T> T invoke(Callable<T> callable, TxTransactionManager tm, Transactional transactional) throws Exception {
		Propagation propagation = transactional.propagation();
		TxTransaction tx = tm.getTransaction();
		if(tx != null && tx.isActive()) {
			if(propagation == Propagation.NEVER) {
				throw new TransactionException("An active transaction already exists");
			} else if(propagation == Propagation.NOT_SUPPORTED) {
				tx.rollback();
				return callable.call();
			} else if(propagation == Propagation.REQUIRES_NEW) {
				tx.rollback();
				return invokeNewTx(callable, tm, transactional);
			} else if(tx.getTransactional().readOnly() && !transactional.readOnly()) {
				//CUREENT TRANSACTION IS NOT SUITABLE FOR UPDATE => AUTO NEW ONE
				return invokeNewTx(callable, tm, transactional);
			}
			return callable.call();
		} else if(propagation == Propagation.MANDATORY) {
			throw new TransactionException("An active transaction does not exist");
		} else if(propagation == Propagation.NEVER || propagation == Propagation.NOT_SUPPORTED || propagation == Propagation.SUPPORTS) {
			//DONT NEED AN TRANSACTION
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
	protected <T> T invokeNewTx(Callable<T> callable, TxTransactionManager tm, Transactional transactional) throws Exception {
		TxTransaction tx = tm.beginTransaction(transactional);
		T result;
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
