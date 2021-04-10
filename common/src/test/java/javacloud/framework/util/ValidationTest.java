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
		GenericException.assertId("hO@ya8.com", "good id");
	}
	
	/**
	 * 
	 */
	public void testGoodEmail() {
		GenericException.assertId("ho@ya8.com", "good email");
		GenericException.assertEmail("administrator@jwt.issuer", "good email");
	}
}
