package com.appe.framework.data;

import com.appe.framework.data.DataCondition;

import junit.framework.TestCase;
/**
 * 
 * @author tobi
 *
 */
public class DataConditionTest extends TestCase {
	public void testOp() {
		assertTrue(DataCondition.EQ(1).test(1));
		assertFalse(DataCondition.EQ(2).test(1));
		
		assertTrue(DataCondition.LE(1).test(1));
		assertTrue(DataCondition.LT(2).test(1));
		
		assertTrue(DataCondition.IN(1, 2).test(1));
		assertTrue(DataCondition.BETWEEN(4, 6).test(5));
		
		assertTrue(DataCondition.EQ("id-0").test("id-0"));
	}
}
