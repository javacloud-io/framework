package javacloud.framework.util;

import org.junit.Assert;
import org.junit.Test;

import junit.framework.TestCase;
/**
 * 
 * @author ho
 *
 */
public class GenericExceptionTest extends TestCase {
	@Test
	public void testBasic() {
		Exception e = GenericException.of(new IllegalArgumentException());
		Assert.assertNotNull(GenericException.getCause(e, IllegalArgumentException.class));
		Assert.assertNotNull(GenericException.getCause(e, Exception.class));
		Assert.assertNotNull(GenericException.getCause(e, RuntimeException.class));
		Assert.assertNull(GenericException.getCause(e, ValidationException.class));
	}
}
