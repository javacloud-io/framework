package io.javacloud.framework.util;

import org.junit.Test;

import org.junit.Assert;
import junit.framework.TestCase;
/**
 * 
 * AND <Variable, Condition>  <Variable, Condition>
 * 
 * @author ho
 *
 */
public class OpConditionTest extends TestCase {
	@Test
	public void testEQ() {
		OpCondition<String> eq = OpCondition.EQ("124");
		Assert.assertTrue(eq.test("124"));
	}
}
