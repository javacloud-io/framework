package javacloud.framework.jacc.util;

import java.util.function.Predicate;

import javacloud.framework.io.TextScanner;
import javacloud.framework.util.Pair;

/**
 * 
 * 
 * @author ho
 *
 */
public class JavaSourceTokenizer {
	public static enum Type {
		ABSTRACT,	//KEYWORDS
		ASSERT,
		BOOLEAN,
		BREAK,
		BYTE,
		CASE,
		CATCH,
		CHAR,
		CLASS,
		CONST,
		CONTINUE,
		DEFAULT,
		DO,
		DOUBLE,
		ELSE,
		ENUM,
		EXTENDS,
		FINAL,
		FINALLY,
		FLOAT,
		FOR,
		IF,
		GOTO,
		IMPLEMENTS,
		IMPORT,
		INSTANCEOF,
		INT,
		INTERFACE,
		LONG,
		NATIVE,
		NEW,
		PACKAGE,
		PRIVATE,
		PROTECTED,
		PUBLIC,
		RETURN,
		SHORT,
		STATIC,
		STRICTFP,
		SUPER,
		SWITCH,
		SYNCHRONIZED,
		THIS,
		THROW,
		THROWS,
		TRANSIENT,
		TRY,
		VOID,
		VOLATILE,
		WHILE,
		
		IDENTIFIER,	//letter_or_digit_dot*
		
		COMMENT_L,	//single line comment
		COMMENT_B,	//block comment
		QUOTE_S,	//single quote
		QUOTE_D,	//double quote
		
		UNKNOWN;	//not recognized yet
		
		public boolean isKeyword() {
			return this.ordinal() < IDENTIFIER.ordinal();
		}
	}
	
	//STOPWORD MATCHER
	private static final Predicate<Character> WHITESPACE_MATCHER = (ch) -> (Character.isWhitespace(ch));
	private static final Predicate<Character> IDENTIFIER_MATCHER = (ch) -> (ch == '.' || ch == '*' || Character.isJavaIdentifierPart(ch));
	
	private final TextScanner scanner;
	public JavaSourceTokenizer(CharSequence source) {
		this.scanner = new TextScanner(source);
	}
	
	/**
	 * Token always start with none-whitespace
	 * 
	 * @return
	 */
	public boolean hasMoreTokens() {
		if (!scanner.hasMoreTokens()) {
			return false;
		}
		//MOVE TO NEXT NON-SPACE
		return scanner.skipToken(WHITESPACE_MATCHER);
	}
	
	/**
	 * return token type and value associated
	 * 
	 * @return
	 */
	public Pair<Type, String> nextToken() {
		if(!hasMoreTokens()) {
			return null;
		}
		
		//LOOK AHEAD CHARACTER
		char ch = scanner.getCharacter();
		if(IDENTIFIER_MATCHER.test(ch)) {
			return nextIdentifier();
		} else if(ch == '/') {
			if(scanner.nextCharacter()) {
				char nc = scanner.getCharacter();
				if(nc == '/' || nc == '*') {
					return nextComment();
				}
			}
			return new Pair<>(Type.UNKNOWN, String.valueOf(ch));
		} else if(ch == '\"' || ch == '\'') {
			return nextQuote();
		}
		
		//UNKNWON TOKEN
		scanner.nextCharacter();
		return new Pair<>(Type.UNKNOWN, String.valueOf(ch));
	}
	
	/**
	 * return next token match type
	 * @param type
	 * @return
	 */
	public String nextToken(Type type) {
		while(true) {
			Pair<Type, String> token = nextToken();
			if(token == null) {
				break;
			} else if(token.getKey() == type) {
				return token.getValue();
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getLineNo() {
		return scanner.getLineNo();
	}
	
	/**
	 * 
	 * @return
	 */
	public int getColumnNo() {
		return scanner.getColumnNo();
	}
	
	/**
	 * return next identifier
	 * 
	 * @return
	 */
	protected Pair<Type, String> nextIdentifier() {
		String identifier = scanner.nextToken(IDENTIFIER_MATCHER);
		try {
			Type type = Type.valueOf(identifier.toUpperCase());
			if(type.isKeyword() && type.name().toLowerCase().equals(identifier)) {
				return new Pair<>(type, identifier); 
			}
		}catch(IllegalArgumentException ex) {
			//ASSUMING NOT A KEYWORD
		}
		return new Pair<>(Type.IDENTIFIER, identifier); 
	}
	
	/**
	 * return next comment
	 * @return
	 */
	protected Pair<Type, String> nextComment() {
		Type type = (scanner.getCharacter() == '*'? Type.COMMENT_B : Type.COMMENT_L);
		String comment;
		//SKIP * or /
		if(!scanner.nextCharacter()) {
			comment = "";
		} else {
			if(type == Type.COMMENT_L) {
				comment = scanner.nextLine();
			} else {
				comment = scanner.nextToken('*', '/');
				//ASSERT scanner.getCharacter() == '/'
				scanner.nextCharacter();
			}
		}
		return new Pair<>(type, comment); 
	}
	
	/**
	 * return string inside quote
	 * 
	 * @return
	 */
	protected Pair<Type, String> nextQuote() {
		Type type = (scanner.getCharacter() == '"'? Type.QUOTE_D : Type.QUOTE_S);
		String qoute;
		//SKIP " or '
		if(!scanner.nextCharacter()) {
			//EXPECTING END QUOTE
			qoute = "";
		} else {
			if(type == Type.QUOTE_D) {
				qoute = scanner.nextQuote('"', '\\');
				//ASSERT scanner.getCharacter() == '"'
				scanner.nextCharacter();
			} else {
				qoute = scanner.nextQuote('\'', '\\');
				//ASSERT scanner.getCharacter() == '\''
				scanner.nextCharacter();
			}
		}
		return new Pair<>(type, qoute); 
	}
}
