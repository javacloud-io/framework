package javacloud.framework.txn.internal;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;

import javacloud.framework.txn.spi.Transaction;

/**
 * 
 * @author ho
 *
 */
public class LocalUnitOfWork implements Iterable<Transaction> {
	private static final ThreadLocal<Deque<Transaction>> unitOfWork = new ThreadLocal<Deque<Transaction>>();
	
	public LocalUnitOfWork() {
	}
	
	/**
	 * 
	 * @return
	 */
	@Override
	public Iterator<Transaction> iterator() {
		Deque<Transaction> stack = unitOfWork.get();
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
		Deque<Transaction> stack = unitOfWork.get();
		return (stack ==null? 0: stack.size());
	}
	
	/**
	 * Peek top transaction, NULL if NONE EXIST
	 * @return
	 */
	public Transaction peek() {
		Deque<Transaction> stack = unitOfWork.get();
		return (stack == null || stack.isEmpty()? null: stack.peek());
	}
	
	/**
	 * Push tx to stack
	 * @param tx
	 * @return
	 */
	public Transaction push(Transaction tx) {
		Deque<Transaction> stack = unitOfWork.get();
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
	public boolean remove(Transaction tx) {
		Deque<Transaction> stack = unitOfWork.get();
		if (stack != null && !stack.isEmpty()) {
			return	stack.remove(tx);
		}
		return false;
	}
	
	/**
	 * Clean out unit of work
	 */
	public void clear() {
		unitOfWork.remove();
	}
}
