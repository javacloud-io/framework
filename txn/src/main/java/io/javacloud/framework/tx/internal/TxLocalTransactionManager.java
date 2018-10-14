package io.javacloud.framework.tx.internal;

import java.util.Stack;

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
	private static final ThreadLocal<Stack<TxTransaction>> unitOfWork = new ThreadLocal<Stack<TxTransaction>>();
	/**
	 * BEGIN UNIT OF WORK
	 */
	@Override
	public void beginSession() {
		Stack<TxTransaction> stack = unitOfWork.get();
		if(stack != null && !stack.isEmpty()) {
			//FIXME: WARNING ABOUT TRANSACTIONS LEFT OVER
		}
		closeSession();
	}
	
	/**
	 * END UNIT OF WORK
	 */
	@Override
	public void endSession() {
		Stack<TxTransaction> stack = unitOfWork.get();
		if(stack != null && !stack.isEmpty()) {
			//FIXME: WARNING ABOUT TRANSACTIONS LEFT OVER
		}
		closeSession();
	}
	
	/**
	 * CLEAN UP, ROLLBACK EVERYTHING !!!
	 * @param commit
	 */
	protected void closeSession() {
		Stack<TxTransaction> stack = unitOfWork.get();
		if(stack != null) {
			try {
				while(!stack.isEmpty()) {
					TxTransaction tx = stack.peek();
					tx.rollback();
				}
			}finally {
				unitOfWork.remove();
			}
		}
	}
	
	/**
	 * 
	 */
	@Override
	public TxTransaction getTransaction() {
		Stack<TxTransaction> stack = unitOfWork.get();
		if(stack == null || stack.isEmpty()) {
			return null;
		}
		return stack.peek();
	}

	/**
	 * ASSUMING THE TRANSACTION CORRECTLY ENHANCED on ROLLBACK/COMMIT by calling endTransaction()
	 */
	@Override
	public TxTransaction beginTransaction(Transactional transactional) {
		TxTransaction tx = newTransaction(transactional);
		Stack<TxTransaction> stack = unitOfWork.get();
		if(stack == null) {
			stack = new Stack<>();
			unitOfWork.set(stack);
		}
		stack.push(tx);
		return tx;
	}
	
	/**
	 * Ensure the transaction is totally off the MAP.
	 * @param tx
	 */
	protected void endTransaction(TxTransaction tx) {
		Stack<TxTransaction> stack = unitOfWork.get();
		if(stack == null || stack.isEmpty()) {
			//FIXME: WARNING IF TX NOT BELONG TO UOW
		} else {
			stack.remove(tx);
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
