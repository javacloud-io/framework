package javacloud.framework.util;

import junit.framework.TestCase;
/**
 * 
 * @author ho
 *
 */
public class ValidationTest extends TestCase {
	/**
	 * 
	 */
	public void testGoodID() {
		Exceptions.assertId("hO@ya8.com", "good id");
	}
	
	/**
	 * 
	 */
	public void testGoodEmail() {
		Exceptions.assertId("ho@ya8.com", "good email");
		Exceptions.assertEmail("administrator@jwt.issuer", "good email");
	}
}
