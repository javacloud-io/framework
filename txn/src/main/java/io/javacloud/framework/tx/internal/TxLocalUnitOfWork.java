package io.javacloud.framework.tx.internal;

import io.javacloud.framework.tx.spi.TxTransaction;

import java.util.Collections;
import java.util.Iterator;
import java.util.Stack;

/**
 * 
 * @author ho
 *
 */
public class TxLocalUnitOfWork implements Iterable<TxTransaction> {
	private static final ThreadLocal<Stack<TxTransaction>> unitOfWork = new ThreadLocal<Stack<TxTransaction>>();
	public TxLocalUnitOfWork() {
	}
	
	/**
	 * 
	 * @return
	 */
	@Override
	public Iterator<TxTransaction> iterator() {
		Stack<TxTransaction> stack = unitOfWork.get();
		if(stack != null) {
			return stack.iterator();
		}
		return Collections.emptyIterator();
	}
	
	/**
	 * 
	 * @return
	 */
	public int size() {
		Stack<TxTransaction> stack = unitOfWork.get();
		return (stack ==null? 0: stack.size());
	}
	
	/**
	 * Peek top transaction, NULL if NONE EXIST
	 * @return
	 */
	public TxTransaction peek() {
		Stack<TxTransaction> stack = unitOfWork.get();
		return (stack == null || stack.isEmpty()? null: stack.peek());
	}
	
	/**
	 * Push tx to stack
	 * @param tx
	 * @return
	 */
	public TxTransaction push(TxTransaction tx) {
		Stack<TxTransaction> stack = unitOfWork.get();
		if(stack == null) {
			stack = new Stack<>();
			unitOfWork.set(stack);
		}
		return	stack.push(tx);
	}
	
	/**
	 * Pop the tx of the stack
	 * @param tx
	 * @return
	 */
	public void remove(TxTransaction tx) {
		Stack<TxTransaction> stack = unitOfWork.get();
		if(stack != null && !stack.isEmpty()) {
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
