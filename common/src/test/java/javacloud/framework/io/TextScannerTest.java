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
	public void testToken() {
		TextScanner scanner = new TextScanner("a ab abc");
		Assert.assertTrue(scanner.hasMoreChars());
		Assert.assertTrue(scanner.currChar() == 'a');
		
		Assert.assertEquals("a", scanner.nextToken((ch) -> (Character.isLetterOrDigit(ch))));
		
		scanner.skipWhitespace();
		Assert.assertEquals("ab", scanner.nextToken((ch) -> (Character.isLetterOrDigit(ch))));
		scanner.skipWhitespace();
		Assert.assertEquals("abc", scanner.nextToken((ch) -> (Character.isLetterOrDigit(ch))));
	}
	
	@Test
	public void testComment() {
		TextScanner scanner = new TextScanner("/*zzz*/\n/*zzz**/");
		Assert.assertTrue(scanner.hasMoreChars());
		
		Assert.assertEquals("/*", scanner.nextToken((ch) -> ch == '/' || ch == '*'));
		Assert.assertEquals("zzz", scanner.nextToken('*', '/'));
		
		Assert.assertTrue(scanner.currChar() == '/' && scanner.nextChar());
		scanner.skipWhitespace();
		
		Assert.assertEquals("/*", scanner.nextToken((ch) -> ch == '/' || ch == '*'));
		Assert.assertEquals("zzz*", scanner.nextToken('*', '/'));
	}
	
	@Test
	public void testQuote() {
		TextScanner scanner = new TextScanner("'zzz'\n'zzz\\\\\\'*'");
		Assert.assertTrue(scanner.hasMoreChars());
		
		Assert.assertEquals("'", scanner.nextToken((ch) -> ch == '\''));
		Assert.assertEquals("zzz", scanner.nextToken('\\', '\'', false));
		
		Assert.assertTrue(scanner.currChar() == '\'' && scanner.nextChar());
		scanner.skipWhitespace();
		
		Assert.assertEquals("'", scanner.nextToken((ch) -> ch == '\''));
		Assert.assertEquals("zzz\\'*", scanner.nextToken('\\', '\'', true));
	}
}
