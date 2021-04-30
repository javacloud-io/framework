package javacloud.framework.util;

import org.junit.Assert;
import org.junit.Test;

import junit.framework.TestCase;
/**
 * 
 * @author ho
 *
 */
public class InternalExceptionTest extends TestCase {
	@Test
	public void testBasic() {
		Exception e = InternalException.of(new IllegalArgumentException());
		Assert.assertNotNull(InternalException.getCause(e, IllegalArgumentException.class));
		Assert.assertNotNull(InternalException.getCause(e, Exception.class));
		Assert.assertNotNull(InternalException.getCause(e, RuntimeException.class));
		Assert.assertNull(InternalException.getCause(e, ValidationException.class));
	}
}
