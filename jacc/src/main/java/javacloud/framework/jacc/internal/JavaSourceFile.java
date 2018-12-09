package javacloud.framework.jacc.internal;

import java.net.URI;

import javacloud.framework.jacc.JavaSource;
import javacloud.framework.jacc.util.JavaTokenizer;
import javacloud.framework.util.Codecs;
import javacloud.framework.util.Objects;
import javacloud.framework.util.Pair;
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
		if(Objects.isEmpty(className) && Objects.isEmpty(className = resolveMainClass(source))) {
			className = "Main";
		} else if(className.endsWith(JAVA_EXTENSION)) {
			className = className.substring(0, className.length() - JAVA_EXTENSION.length());
		}
		this.uri = URI.create("file:///" + className.replaceAll("\\.", "/") + JAVA_EXTENSION);
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
	public static final String resolveMainClass(CharSequence source) {
		JavaTokenizer tokenizer = new JavaTokenizer(source);
		String packageName = null;
		String className   = null;
		
		//PACKAGE NAME
		while(tokenizer.hasMoreTokens()) {
			Pair<JavaTokenizer.Type, String> token = tokenizer.nextToken();
			if(token.getKey() == JavaTokenizer.Type.PACKAGE) {
				packageName = tokenizer.nextTokens((tt) -> (tt == JavaTokenizer.Type.IDENTIFIER || tt == JavaTokenizer.Type.DOT));
			} else if(token.getKey() == JavaTokenizer.Type.CLASS) {
				className = tokenizer.nextToken(JavaTokenizer.Type.IDENTIFIER);
				break;
			}
		}
		
		//NOT FOUND MAIN CLASS
		if(Objects.isEmpty(className)) {
			return null;
		} else if(Objects.isEmpty(packageName)) {
			return className;
		}
		return (packageName + "." + className);
	}
}
