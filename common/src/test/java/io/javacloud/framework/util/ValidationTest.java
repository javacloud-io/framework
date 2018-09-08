package io.javacloud.framework.util;

import io.javacloud.framework.data.ValidationException;
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
		ValidationException.assertId("hO@ya8.com", "good id");
	}
	
	/**
	 * 
	 */
	public void testGoodEmail() {
		ValidationException.assertId("ho@ya8.com", "good email");
		ValidationException.assertEmail("administrator@jwt.issuer", "good email");
	}
}
