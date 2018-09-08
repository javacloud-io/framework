package io.javacloud.framework.util;

import io.javacloud.framework.util.Jvms;

import java.net.UnknownHostException;

import org.junit.Test;

import junit.framework.TestCase;
/**
 * 
 * @author ho
 *
 */
public class IPRangeTest extends TestCase {
	@Test
	public void testLocal() throws UnknownHostException {
		assertTrue(Jvms.isAddressInRange("127.1.1.1", "127.0.0.0/8"));
		assertFalse(Jvms.isAddressInRange("127.1.1.1", "127.0.0.0/9"));
		assertTrue(Jvms.isAddressInRange("127.9.1.1", "127.9.0.0/16"));
	}
}
