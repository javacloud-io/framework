package javacloud.framework.txn.internal;

import java.util.logging.Level;
import java.util.logging.Logger;

import javacloud.framework.txn.Transactional;
import javacloud.framework.txn.spi.SessionManager;
import javacloud.framework.txn.spi.Transaction;
import javacloud.framework.txn.spi.TransactionManager;
import javacloud.framework.util.Objects;
/**
 * 
 * @author ho
 *
 */
public abstract class LocalTransactionManager implements TransactionManager, SessionManager {
	private static final Logger logger = Logger.getLogger(LocalTransactionManager.class.getName());
	private final LocalUnitOfWork unitOfWork = new LocalUnitOfWork();
	
	/**
	 * BEGIN UNIT OF WORK
	 */
	@Override
	public void beginSession() {
		int size = unitOfWork.size();
		if (size > 0) {
			logger.log(Level.WARNING, "Session started but still have {0} active transaction", size);
		}
	}
	
	/**
	 * END UNIT OF WORK
	 */
	@Override
	public void endSession() {
		int size = unitOfWork.size();
		if (size > 0) {
			logger.log(Level.WARNING, "Session ended but still have {0} active transaction", size);
			for (Transaction tx: unitOfWork) {
				Objects.closeQuietly(() -> tx.rollback());
			}
		}
		unitOfWork.clear();
	}
	
	/**
	 * FIXME: SUPPORTS SUSPENDED TRANSACTION
	 */
	@Override
	public Transaction getTransaction() {
		Transaction tx = unitOfWork.peek();
		return tx;
	}

	/**
	 * ASSUMING THE TRANSACTION CORRECTLY ENHANCED on ROLLBACK/COMMIT by calling endTransaction()
	 */
	@Override
	public Transaction beginTransaction(Transactional transactional) {
		Transaction tx = newTransaction(transactional);
		logger.log(Level.FINE, "Begin transaction: {0}", tx);
		
		return unitOfWork.push(tx);
	}
	
	/**
	 * Ensure the transaction is totally off the MAP.
	 * 
	 * @param tx
	 */
	protected boolean endTransaction(Transaction tx) {
		logger.log(Level.FINE, "End transaction: {0}", tx);
		if (unitOfWork.peek() != tx) {
			logger.log(Level.WARNING, "Transaction: {0} doesn't belong to the session", tx);
		}
		return unitOfWork.remove(tx);
	}
	
	/**
	 * 
	 * @param transactional
	 * @return a transaction which commit & rollback should always invoke endTransaction!!!
	 */
	protected abstract Transaction newTransaction(Transactional transactional);
}
