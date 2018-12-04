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
		Assert.assertFalse(scanner.hasMoreChars());
		Assert.assertFalse(scanner.nextChar());
	}
	
	@Test
	public void test3Tokens() {
		TextScanner scanner = new TextScanner("a ab abc");
		Assert.assertTrue(scanner.hasMoreChars());
		Assert.assertTrue(scanner.currChar() == 'a');
		
		Assert.assertEquals("a", scanner.nextToken((ch) -> (Character.isLetterOrDigit(ch))));
		
		scanner.skipToken((ch) -> (Character.isWhitespace(ch)));
		Assert.assertEquals("ab", scanner.nextToken((ch) -> (Character.isLetterOrDigit(ch))));
		scanner.skipToken((ch) -> (Character.isWhitespace(ch)));
		Assert.assertEquals("abc", scanner.nextToken((ch) -> (Character.isLetterOrDigit(ch))));
	}
}
