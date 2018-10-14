package io.javacloud.framework.tx.internal;

import java.util.logging.Logger;

import io.javacloud.framework.tx.Transactional;
import io.javacloud.framework.tx.spi.TxSessionManager;
import io.javacloud.framework.tx.spi.TxTransaction;
import io.javacloud.framework.tx.spi.TxTransactionManager;
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
		if(size > 0) {
			logger.warning("Session starting but still have " + size + " active transaction");
		}
		closeSession();
	}
	
	/**
	 * END UNIT OF WORK
	 */
	@Override
	public void endSession() {
		int size = unitOfWork.size();
		if(size > 0) {
			logger.warning("Session ending but still have " + size + " active transaction");
		}
		closeSession();
	}
	
	/**
	 * CLEAN UP, ROLLBACK EVERYTHING !!!
	 * @param commit
	 */
	protected void closeSession() {
		while(unitOfWork.size() > 0) {
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
		logger.fine("Begin transaction: " + tx);
		
		return unitOfWork.push(tx);
	}
	
	/**
	 * Ensure the transaction is totally off the MAP.
	 * @param tx
	 */
	protected void endTransaction(TxTransaction tx) {
		logger.fine("End transaction: " + tx);
		if(unitOfWork.peek() != tx) {
			logger.warning("Transaction: " + tx + " doesn't belong to the session");
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