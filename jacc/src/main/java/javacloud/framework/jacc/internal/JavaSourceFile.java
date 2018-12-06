package javacloud.framework.jacc.internal;

import java.net.URI;

import javacloud.framework.jacc.JavaSource;
import javacloud.framework.jacc.util.JavaTokenizer;
import javacloud.framework.util.Codecs;
import javacloud.framework.util.Objects;
/**
 * 
 * @author ho
 *
 */
public class JavaSourceFile implements JavaSource {
	public static final String JAVA_EXTENSION = ".java";
	private final URI uri;
	private final CharSequence source;
	/**
	 * @param source
	 * @param className
	 */
	public JavaSourceFile(CharSequence source, String className) {
		if(Objects.isEmpty(className)) {
			className = resolveClassName(source);
		} else if(className.endsWith(JAVA_EXTENSION)) {
			className = className.substring(0, className.length() - JAVA_EXTENSION.length());
		}
		this.uri = URI.create("src://" + className.replaceAll("\\.", "/") + JAVA_EXTENSION);
		this.source = source;
	}
	
	/**
	 * Read source file into memory prior to parsing
	 * @param source
	 * @param className
	 */
	public JavaSourceFile(byte[] source, String className) {
		this(Codecs.toUTF8(source), className);
	}
	
	/**
	 * return file location
	 */
	@Override
	public URI file() {
		return uri;
	}
	
	/**
	 * return an input stream for reading
	 */
	@Override
	public CharSequence asChars() {
		return source;
	}
	
	/**
	 * Super efficient way to detect main class name
	 * 
	 * @param source
	 * @return
	 */
	public static final String resolveClassName(CharSequence source) {
		JavaTokenizer tokenizer = new JavaTokenizer(source);
		String packageName = null;
		String className   = null;
		//PACKAGE NAME
		if(tokenizer.nextToken(JavaTokenizer.Type.PACKAGE) != null) {
			packageName = tokenizer.nextTokens((tt) -> (tt == JavaTokenizer.Type.IDENTIFIER || tt == JavaTokenizer.Type.DOT));
		}
		
		//CLASS NAME
		if(tokenizer.nextToken(JavaTokenizer.Type.CLASS) != null) {
			className = tokenizer.nextToken(JavaTokenizer.Type.IDENTIFIER);
		}
		
		//FULL CLASS NAME
		if(Objects.isEmpty(className)) {
			className = "Main";
		}
		return (Objects.isEmpty(packageName)? className : packageName + "." + className);
	}
}
