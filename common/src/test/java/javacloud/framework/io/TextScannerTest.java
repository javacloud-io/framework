package javacloud.framework.io;

import org.junit.Assert;
import org.junit.Test;

import junit.framework.TestCase;
/**
 * 
 * @author ho
 *
 */
public class TextScannerTest extends TestCase {
	@Test
	public void testEmpty() {
		TextScanner scanner = new TextScanner("");
		Assert.assertFalse(scanner.hasMoreTokens());
		Assert.assertFalse(scanner.nextCharacter());
	}
}
