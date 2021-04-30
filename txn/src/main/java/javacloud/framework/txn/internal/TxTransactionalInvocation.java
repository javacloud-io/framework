package javacloud.framework.txn.internal;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javacloud.framework.txn.Propagation;
import javacloud.framework.txn.Transactional;
import javacloud.framework.txn.spi.TxTransaction;
import javacloud.framework.txn.spi.TxTransactionException;
import javacloud.framework.txn.spi.TxTransactionManager;

/**
 * Default handle transactional invocation
 * 
 * @author ho
 *
 */
public class TxTransactionalInvocation {
	private static final Logger logger = Logger.getLogger(TxTransactionalInvocation.class.getName());
	
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
		
		//THERE IS AN ACTIVE TRANSACTION
		if (tx != null && tx.isActive()) {
			if (propagation == Propagation.NEVER) {
				//DONT NEED ACTIVE TRANSACTION BUT HAVE ONE
				throw new TxTransactionException.NotRequired("An active transaction already exists");
			} else if(propagation == Propagation.NOT_SUPPORTED) {
				//FIXME: TO SUPPORT SUSPEND EXISTING TX AND RESUME AFTER DONE
				return callable.call();
			} else if(propagation == Propagation.REQUIRES_NEW) {
				//FIXME: TO SUPPORT SUSPEND EXISTING TX AND RESUME AFTER DONE
				return invokeNewTx(callable, tm, transactional);
			} else if(tx.getTransactional().readOnly() && !transactional.readOnly()) {
				logger.log(Level.FINE, "Existing transaction: {0} is readOnly but required readWrite", tx);
				//FIXME: FOR BETER PERFORMANCE WE SHOULD TRY TO CONVERT THE CURRENT TRANSACTION AND RESUME AFTER DONE
				return invokeNewTx(callable, tm, transactional);
			}
			return callable.call();
		} else if (propagation == Propagation.MANDATORY) {
			//NEED AN ACTIVE TRANSACTION BUT NOT FOUND ONE
			throw new TxTransactionException.Required("An active transaction does not exist");
		} else if (propagation == Propagation.NEVER
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
		} catch (Exception ex) {
			if (isRollbackOn(ex, transactional)) {
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
		for (Class<? extends Exception> eclazz: transactional.rollbackOn()) {
			if (eclazz.isInstance(ex)) {
				rollback = true;
				break;
			}
		}
		if (rollback) {
			for (Class<? extends Exception> eclazz: transactional.noRollbackOn()) {
				if (eclazz.isInstance(ex)) {
					rollback = false;
					break;
				}
			}
		}
		return rollback;
	}
}
