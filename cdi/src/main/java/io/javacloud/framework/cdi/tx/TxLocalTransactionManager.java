package io.javacloud.framework.cdi.tx;

import java.util.Stack;

import io.javacloud.framework.tx.Transactional;
/**
 * 
 * @author ho
 *
 */
public abstract class TxLocalTransactionManager<Tx> implements TxTransactionManager<Tx>, TxSessionManager {
	protected final ThreadLocal<Stack<TxTransaction<Tx>>> unitOfWork = new ThreadLocal<Stack<TxTransaction<Tx>>>();
	/**
	 * BEGIN UNIT OF WORK
	 */
	@Override
	public void beginSession() {
		Stack<TxTransaction<Tx>> stack = unitOfWork.get();
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
		Stack<TxTransaction<Tx>> stack = unitOfWork.get();
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
		Stack<TxTransaction<Tx>> stack = unitOfWork.get();
		if(stack != null) {
			try {
				while(!stack.isEmpty()) {
					TxTransaction<Tx> tx = stack.peek();
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
	public TxTransaction<Tx> getTransaction() {
		Stack<TxTransaction<Tx>> stack = unitOfWork.get();
		if(stack == null || stack.isEmpty()) {
			return null;
		}
		return stack.peek();
	}

	/**
	 * ENHANCE THE TRANSACTION TO CORRECTLY HANDLE ROLLBACK/COMMIT
	 */
	@Override
	public TxTransaction<Tx> beginTransaction(Transactional transactional) {
		final TxTransaction<Tx> tx = newTransaction(transactional);
		TxTransaction<Tx> txn = new TxTransaction<Tx>() {
			@Override
			public Tx get() {
				return tx.get();
			}
			
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
		Stack<TxTransaction<Tx>> stack = unitOfWork.get();
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
	protected void clearTransaction(TxTransaction<Tx> tx) {
		Stack<TxTransaction<Tx>> stack = unitOfWork.get();
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
	protected abstract TxTransaction<Tx> newTransaction(Transactional transactional);
}
