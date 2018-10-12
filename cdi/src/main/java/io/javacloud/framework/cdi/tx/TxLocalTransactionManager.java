package io.javacloud.framework.cdi.tx;

import java.util.Stack;

import io.javacloud.framework.tx.Transactional;
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
		clearSession();
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
		clearSession();
	}
	
	/**
	 * CLEAN UP, ROLLBACK EVERYTHING
	 * @param commit
	 */
	protected void clearSession() {
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
	 * ENHANCE THE TRANSACTION TO CORRECTLY HANDLE ROLLBACK/COMMIT
	 */
	@Override
	public TxTransaction beginTransaction(Transactional transactional) {
		final TxTransaction tx = newTransaction(transactional);
		TxTransaction txn = new TxTransaction() {
			@Override
			public Transactional getTransactional() {
				return tx.getTransactional();
			}

			@Override
			public boolean isActive() {
				return tx.isActive();
			}

			@Override
			public void commit() {
				try {
					tx.commit();
				} finally {
					clearTransaction(this);
				}
			}

			@Override
			public void rollback() {
				try {
					tx.rollback();
				} finally {
					clearTransaction(this);
				}
			}
		};
		
		//ADD TO UNIT OF WORK
		Stack<TxTransaction> stack = unitOfWork.get();
		if(stack == null) {
			stack = new Stack<>();
			unitOfWork.set(stack);
		}
		stack.push(txn);
		return txn;
	}
	
	/**
	 * 
	 * @param tx
	 */
	protected void clearTransaction(TxTransaction tx) {
		Stack<TxTransaction> stack = unitOfWork.get();
		if(stack == null || stack.isEmpty()) {
			//FIXME: WARNING IF TX NOT BELONG TO UOW
		} else {
			stack.remove(tx);
		}
	}
	
	/**
	 * 
	 * @param transactional
	 * @return
	 */
	protected abstract TxTransaction newTransaction(Transactional transactional);
}
