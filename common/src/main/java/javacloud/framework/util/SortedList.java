package javacloud.framework.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
/**
 * A duplicatable sorted list but behave like a TreeSet. Adding to the list will ensure the order specified by comparator.
 * CAUTION: only implemented method guarantee correct behavior
 * 
 * @author ho
 *
 * @param <E>
 */
public class SortedList<E> extends ArrayList<E> implements Comparator<E> {
	private static final long serialVersionUID = -7789345340867157420L;
	private final Comparator<E> comparator;
	
	/**
	 * 
	 * @param initialCapacity
	 * @param comparator
	 */
	public SortedList(int initialCapacity, Comparator<E> comparator) {
		super(initialCapacity);
		this.comparator = comparator;
	}
	
	/**
	 * initialize with default comparator
	 * @param initialCapacity
	 */
	public SortedList(int initialCapacity) {
		this(initialCapacity, null);
	}
	
	/**
	 * Default to 10 elements
	 */
	public SortedList() {
		this(10);
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
			super.add(index, e);//allows duplication
			return true;
		}
		super.add(-index - 1, e);
		return true;
	}
	
	/**
	 * Comparing a and b
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	@Override
	public int compare(E o1, E o2) {
		if (comparator != null) {
			return comparator.compare(o1, o2);
		}
		Comparable<E> e = Objects.cast(o1);
		return e.compareTo(o2);
	}
	
	/**
	 * return binary search
	 * 
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
		if (isEmpty()) {
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
		if (index >= 0) {
			remove(index);
			return true;
		}
		return false;
	}
	
	/**
	 * Optimize for range operation, get O(m + n) instead of O(log(m) * n)
	 */
	@Override
	public boolean addAll(Collection<? extends E> c) {
		if(c == null || c.isEmpty()) {
			return false;
		}
		int count = 0;
		if (c instanceof SortedList) {
			SortedList<E> sc = Objects.cast(c);
			int i = 0, j = 0;
			while (i < size() && j < sc.size()) {
				E e = sc.get(j);
				int cmp = compare(get(i), e);
				if (cmp < 0) {
					i ++;
				} else {
					if (cmp == 0) {
						if(insert(i, e)) {
							count ++;
						}
					} else {
						if(insert(-i - 1, e)) {
							count ++;
						}
					}
					j ++;
				}
			}
			
			//the remaining always bigger than last element => keep insert at the end.
			while(j < sc.size()) {
				if(insert(size(), sc.get(j))) {
					count ++;
				}
				j ++;
			}
		} else {
			for(E e: c) {
				if(add(e)) {
					count ++;
				}
			}
		}
		return (count > 0);
	}
	
	/**
	 * Optimize for range operation, get O(m + n) instead of O(log(m) * n)
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		if (c == null || c.isEmpty()) {
			return false;
		}
		if (c instanceof SortedList) {
			int count = 0;
			SortedList<E> sc = Objects.cast(c);
			int i = 0, j = 0;
			while (i < size() && j < sc.size()) {
				E e = sc.get(j);
				int cmp = compare(get(i), e);
				if(cmp < 0) {
					i ++;
				} else if(cmp == 0) {
					remove(i);
					count ++;
				} else {
					j ++;
				}
			}
			return (count > 0);
		}
		return super.removeAll(c);
	}
	
	/**
	 * Optimize for range operation, get O(m + n) instead of O(log(m) * n)
	 */
	@Override
	public boolean retainAll(Collection<?> c) {
		return super.retainAll(c);
	}
	
	/**
	 * Optimize for range operation, get O(m + n) instead of O(log(m) * n)
	 */
	@Override
	public boolean containsAll(Collection<?> c) {
		if (c == null || c.isEmpty()) {
			return true;
		}
		if (c instanceof SortedList) {
			SortedList<E> sc = Objects.cast(c);
			int i = 0, j = 0;
			while(i < size() && j < sc.size()) {
				E e = sc.get(j);
				int cmp = compare(get(i), e);
				if(cmp < 0) {
					i ++;
				} else if(cmp == 0) {
					j ++;
				}
			}
			return (j >= sc.size());
		}
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
	
	//NOT SUPPORT SPECIFIC INDEX
	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		return addAll(c);
	}
	
	/**
	 * ENSURE NON-DUPLICATED ELEMENT
	 * 
	 * @param <E>
	 */
	public static class Unique<E> extends SortedList<E> {
		private static final long serialVersionUID = -4619743868807763169L;
		
		public Unique() {
			super();
		}
		
		public Unique(int initialCapacity, Comparator<E> comparator) {
			super(initialCapacity, comparator);
		}

		public Unique(int initialCapacity) {
			super(initialCapacity);
		}
		
		//ENSURE NOT DUPLICATION
		@Override
		protected boolean insert(int index, E e) {
			if (index >= 0) {
				return false;
			}
			return super.insert(index, e);
		}
	}
}
