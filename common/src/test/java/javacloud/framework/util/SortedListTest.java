package javacloud.framework.util;

import junit.framework.TestCase;
/**
 * 
 * @author ho
 *
 */
public class SortedListTest extends TestCase {
	public void testAddAll() {
		SortedList<Integer> sl = new SortedList.Unique<>();
		sl.add(10);
		sl.add(5);
		sl.add(19);
		sl.add(45);
		sl.add(100);
		sl.add(1);
		sl.add(45);
		System.out.println(sl);
		
		SortedList<Integer> sl2 = new SortedList.Unique<>();
		sl2.add(1);
		sl2.add(30);
		sl2.add(15);
		System.out.println(sl2);
		
		//sl.addAll(sl2);
		sl.removeAll(sl2);
		System.out.println(sl);
	}
}
