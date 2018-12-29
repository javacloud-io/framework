package javacloud.framework.util;

import org.junit.Assert;
import org.junit.Test;

import junit.framework.TestCase;
/**
 * 
 * @author ho
 *
 */
public class ExceptionsTest extends TestCase {
	@Test
	public void testBasic() {
		Exception e = Exceptions.wrap(new IllegalArgumentException());
		Assert.assertNotNull(Exceptions.getCause(e, IllegalArgumentException.class));
		Assert.assertNotNull(Exceptions.getCause(e, Exception.class));
		Assert.assertNotNull(Exceptions.getCause(e, RuntimeException.class));
		Assert.assertNull(Exceptions.getCause(e, ValidationException.class));
	}
}
