package javacloud.framework.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
/**
 * A none duplicated sorted list but behave like a TreeSet. Add to the list will ensure the order.
 * CAUTION: only implemented method guarantee correct behavior
 * @author ho
 *
 * @param <E>
 */
public class SortedList<E> extends ArrayList<E> {
	private static final long serialVersionUID = -7789345340867157420L;
	private Comparator<E> comparator;
	
	/**
	 * 
	 * @param initialCapacity
	 */
	public SortedList(int initialCapacity, Comparator<E> comparator) {
		super(initialCapacity);
		if(comparator != null) {
			this.comparator = comparator;
		}
	}
	
	/**
	 * 
	 * @param initialCapacity
	 */
	public SortedList(int initialCapacity) {
		this(initialCapacity, null);
	}
	
	/**
	 * insert the element at index
	 * 
	 * @param index
	 * @param e
	 * @return
	 */
	protected boolean insert(int index, E e) {
		if(index >= 0) {
			super.set(index, e);//replacement
			return false;
		}
		super.add(-index - 1, e);
		return true;
	}
	
	/**
	 * 
	 * return binary search
	 */
	@Override
	public final int indexOf(Object o) {
		return Collections.binarySearch(this, Objects.cast(o), comparator);
	}
	
	/**
	 * return false if item already existed.
	 */
	@Override
	public boolean add(E e) {
		if(isEmpty()) {
			return super.add(e);
		}
		//insert at correct location
		int index = indexOf(e);
		return insert(index, e);
	}
	
	/**
	 * Remove by object
	 */
	@Override
	public boolean remove(Object o) {
		int index = indexOf(o);
		if(index >= 0) {
			remove(index);
			return true;
		}
		return false;
	}
	
	/**
	 * FIXME: Optimize for range operation
	 */
	@Override
	public boolean addAll(Collection<? extends E> c) {
		if(c == null || c.isEmpty()) {
			return false;
		}
		int count = 0;
		for(E e: c) {
			if(add(e)) {
				count ++;
			}
		}
		return (count > 0);
	}
	
	/**
	 * 
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		return super.removeAll(c);
	}
	
	/**
	 * 
	 */
	@Override
	public boolean retainAll(Collection<?> c) {
		return super.retainAll(c);
	}
	
	/**
	 * 
	 */
	@Override
	public boolean containsAll(Collection<?> c) {
		return super.containsAll(c);
	}
	
	//
	//CAUTION: UNEXPECTED BEHAVIOR OPERATIONS ENSURE LIST IS STILL SORTED IF INVOKE
	//
	/**
	 * 
	 */
	@Override
	public int lastIndexOf(Object o) {
		return indexOf(o);
	}
	
	//PRETENT LIKE ADD
	@Override
	public E set(int index, E e) {
		E o = get(index);
		add(e);
		return o;
	}
	//NOT SUPPORT SPECIFIC INDEX
	@Override
	public void add(int index, E e) {
		add(e);
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		return addAll(c);
	}
}
