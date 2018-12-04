package javacloud.framework.jacc;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;

import javacloud.framework.io.TextScanner;
import javacloud.framework.jacc.util.JavaTokenizer;
import javacloud.framework.util.Pair;
import javacloud.framework.util.ResourceLoader;
import junit.framework.TestCase;
/**
 * 
 * @author ho
 *
 */
public class JavaTokenizerTest extends TestCase {
	@Test
	public void testTokenizer() throws Exception {
		InputStream source = ResourceLoader.getClassLoader().getResourceAsStream("java-1.7.sablecc");
		TextScanner scanner= new TextScanner(new InputStreamReader(source));
		JavaTokenizer tokenizier = new JavaTokenizer(scanner);
		
		System.out.println("SABLECC TOKENS");
		while(tokenizier.hasMoreTokens()) {
			System.out.print(scanner.getLineNo() + ":" + scanner.getColumnNo() + "\t");
			
			Pair<JavaTokenizer.Type, String> token = tokenizier.nextToken();
			System.out.println(token.getKey() + (token.getKey() == JavaTokenizer.Type.COMMENT_B? "\n" : "\t") + token.getValue());
		}
	}
}
