package javacloud.framework.txn.internal;

import java.util.logging.Level;
import java.util.logging.Logger;

import javacloud.framework.txn.Transactional;
import javacloud.framework.txn.spi.TxSessionManager;
import javacloud.framework.txn.spi.TxTransaction;
import javacloud.framework.txn.spi.TxTransactionManager;
/**
 * 
 * @author ho
 *
 */
public abstract class TxLocalTransactionManager implements TxTransactionManager, TxSessionManager {
	private static final Logger logger = Logger.getLogger(TxLocalTransactionManager.class.getName());
	private final TxLocalUnitOfWork unitOfWork = new TxLocalUnitOfWork();
	
	/**
	 * BEGIN UNIT OF WORK
	 */
	@Override
	public void beginSession() {
		int size = unitOfWork.size();
		if (size > 0) {
			logger.log(Level.WARNING, "Session starting but still have {0} active transaction", size);
		}
		closeSession();
	}
	
	/**
	 * END UNIT OF WORK
	 */
	@Override
	public void endSession() {
		int size = unitOfWork.size();
		if (size > 0) {
			logger.log(Level.WARNING, "Session ending but still have {0} active transaction", size);
		}
		closeSession();
	}
	
	/**
	 * CLEAN UP, ROLLBACK EVERYTHING !!!
	 * @param commit
	 */
	protected void closeSession() {
		while (unitOfWork.size() > 0) {
			TxTransaction tx = unitOfWork.peek();
			tx.rollback();
		}
	}
	
	/**
	 * FIXME: SUPPORTS SUSPENDED TRANSACTION
	 */
	@Override
	public TxTransaction getTransaction() {
		TxTransaction tx = unitOfWork.peek();
		return tx;
	}

	/**
	 * ASSUMING THE TRANSACTION CORRECTLY ENHANCED on ROLLBACK/COMMIT by calling endTransaction()
	 */
	@Override
	public TxTransaction beginTransaction(Transactional transactional) {
		TxTransaction tx = newTransaction(transactional);
		logger.log(Level.FINE, "Begin transaction: {0}", tx);
		
		return unitOfWork.push(tx);
	}
	
	/**
	 * Ensure the transaction is totally off the MAP.
	 * @param tx
	 */
	protected void endTransaction(TxTransaction tx) {
		logger.log(Level.FINE, "End transaction: {0}", tx);
		if (unitOfWork.peek() != tx) {
			logger.log(Level.WARNING, "Transaction: {0} doesn't belong to the session", tx);
		} else {
			unitOfWork.remove(tx);
		}
	}
	
	/**
	 * return a transaction which commit & rollback should always invoke endTransaction!!!
	 * 
	 * @param transactional
	 * @return
	 */
	protected abstract TxTransaction newTransaction(Transactional transactional);
}
