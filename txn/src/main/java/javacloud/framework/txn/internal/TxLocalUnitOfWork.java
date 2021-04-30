package javacloud.framework.txn.internal;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;

import javacloud.framework.txn.spi.TxTransaction;

/**
 * 
 * @author ho
 *
 */
public class TxLocalUnitOfWork implements Iterable<TxTransaction> {
	private static final ThreadLocal<Deque<TxTransaction>> unitOfWork = new ThreadLocal<Deque<TxTransaction>>();
	public TxLocalUnitOfWork() {
	}
	
	/**
	 * 
	 * @return
	 */
	@Override
	public Iterator<TxTransaction> iterator() {
		Deque<TxTransaction> stack = unitOfWork.get();
		if (stack != null) {
			return stack.iterator();
		}
		return Collections.emptyIterator();
	}
	
	/**
	 * 
	 * @return
	 */
	public int size() {
		Deque<TxTransaction> stack = unitOfWork.get();
		return (stack ==null? 0: stack.size());
	}
	
	/**
	 * Peek top transaction, NULL if NONE EXIST
	 * @return
	 */
	public TxTransaction peek() {
		Deque<TxTransaction> stack = unitOfWork.get();
		return (stack == null || stack.isEmpty()? null: stack.peek());
	}
	
	/**
	 * Push tx to stack
	 * @param tx
	 * @return
	 */
	public TxTransaction push(TxTransaction tx) {
		Deque<TxTransaction> stack = unitOfWork.get();
		if (stack == null) {
			stack = new ArrayDeque<>();
			unitOfWork.set(stack);
		}
		stack.push(tx);
		return tx;
	}
	
	/**
	 * Pop the tx of the stack
	 * @param tx
	 * @return
	 */
	public void remove(TxTransaction tx) {
		Deque<TxTransaction> stack = unitOfWork.get();
		if (stack != null && !stack.isEmpty()) {
			stack.remove(tx);
		}
	}
	
	/**
	 * Clean out unit of work
	 */
	public void clear() {
		unitOfWork.remove();
	}
}
