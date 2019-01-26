package javacloud.framework.jacc.util;

import java.util.Map;
import java.util.function.Predicate;

import javacloud.framework.io.TextScanner;
import javacloud.framework.util.Pair;

/**
 * Simple java source tokenizer to extracting important info without complicated parsing.
 * 
 * @author ho
 *
 */
public class JavaTokenizer {
	public static enum Type {
		ABSTRACT,		//KEYWORDS in sorted order
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
		
		IDENTIFIER,		//letter_or_digit_dot*
		NUMBER,			//
		
		COMMENT_L,		//single line comment
		COMMENT_B,		//block comment
		QUOTE_S,		//single quote character
		QUOTE_D,		//double quote string
		
		DOT,			//'.'
		DOT_DOT,		//'..'
		DOT_DOT_DOT,	//'...'
		
		PLUS,			//'+'
		PLUS_PLUS,		//'++'
		PLUS_ASSIGN,	//'+='
		
		MINUS,			//'-'
		MINUS_MINUS,	//'--'
		MINUS_ASSIGN,	//'-='
		ARROW_R,		//'->'
		
		STAR,			//'*'
		STAR_ASSIGN,	//*='
		
		SLASH,			//'/'
		SLASH_ASSIGN,	//'/='
		
		PERCENT,		//'%'
		PERCENT_ASSIGN,	//'%='
		
		ASSIGN,			//'='
		EQ,				//'=='
		
		EMARK,			//'!'
		NEQ,			//'!='
		
		AMP,			//'&'
		AMP_AMP,		//'&&'
		AMP_ASSIGN,		//'&='
		
		BAR,			//'|'
		BAR_BAR,		//'||'
		BAR_ASSIGN,		//'|='
		  
		CARET,			//'^'
		CARET_ASSIGN,	//'^='
		
		LT,				//'<'
		LTEQ,			//'<='
		SHL,			//'<<'
		SHL_ASSIGN,		//'<<='
		
		GT,				//'>'
		GTEQ,			//'>='
		SHR,			//'>>'
		SHR_ASSIGN,		//'>>='
		USHR,			//'>>>'
		USHR_ASSIGN,	//'>>>='
		
		TILDE,			//'~'
		QMARK,			//'?'
		COLON,			//':'
		COMMA,			//','
		SEMI,			//';'
		AT,				//'@'
		
		LPAREN,			//'([{'
		RPAREN,			//')]}'
		
		TEXT;		//not recognized yet
		
		public boolean isKeyword() {
			return this.ordinal() < IDENTIFIER.ordinal();
		}
	}
	
	private final TextScanner scanner;
	public JavaTokenizer(CharSequence source) {
		this(new TextScanner(source));
	}
	
	/**
	 * 
	 * @param scanner
	 */
	public JavaTokenizer(TextScanner scanner) {
		this.scanner = scanner;
	}
	
	/**
	 * Token always start with none-whitespace
	 * 
	 * @return
	 */
	public boolean hasMoreTokens() {
		if (!scanner.hasMoreChars()) {
			return false;
		}
		//MOVE TO NEXT NON-SPACE
		return scanner.skipChars((ch) -> (Character.isWhitespace(ch)));
	}
	
	/**
	 * return token type and value associated
	 * 
	 * @return
	 */
	public Map.Entry<Type, String> nextToken() {
		//LOOK AHEAD ONE CHARACTER
		char ch = scanner.currChar();
		if(Character.isDigit(ch)) {
			return nextNumber();
		} else if (Character.isJavaIdentifierStart(ch)) {
			return nextIdentifier();
		} else if(ch == '\"' || ch == '\'') {
			return nextQuote();
		} else if(ch == '.') {
			if(scanner.nextChar()) {
				char nc = scanner.currChar();
				if(nc == '.') {
					if(scanner.nextChar() && scanner.currChar() == '.') {
						scanner.nextChar();
						return new Pair<>(Type.DOT_DOT_DOT, "...");
					}
					return new Pair<>(Type.DOT_DOT, "..");
				}
			}
			return new Pair<>(Type.DOT, String.valueOf(ch));
		} else if(ch == '+') {
			if(scanner.nextChar()) {
				char nc = scanner.currChar();
				if(nc == '+') {
					scanner.nextChar();
					return new Pair<>(Type.PLUS_PLUS, "++");
				} else if(nc == '=') {
					scanner.nextChar();
					return new Pair<>(Type.PLUS_ASSIGN, "+=");
				}
			}
			return new Pair<>(Type.PLUS, String.valueOf(ch));
		} else if(ch == '-') {
			if(scanner.nextChar()) {
				char nc = scanner.currChar();
				if(nc == '-') {
					scanner.nextChar();
					return new Pair<>(Type.MINUS_MINUS, "--");
				} else if(nc == '=') {
					scanner.nextChar();
					return new Pair<>(Type.MINUS_ASSIGN, "-=");
				} else if(nc == '>') {
					scanner.nextChar();
					return new Pair<>(Type.ARROW_R, "->");
				}
				
			}
			return new Pair<>(Type.MINUS, String.valueOf(ch));
		} else if(ch == '*') {
			if(scanner.nextChar()) {
				char nc = scanner.currChar();
				if(nc == '=') {
					scanner.nextChar();
					return new Pair<>(Type.STAR_ASSIGN, "*=");
				}
			}
			return new Pair<>(Type.STAR, String.valueOf(ch));
		} else if(ch == '/') {
			if(scanner.nextChar()) {
				char nc = scanner.currChar();
				if(nc == '/' || nc == '*') {
					return nextComment();
				} else if(nc == '=') {
					scanner.nextChar();
					return new Pair<>(Type.SLASH_ASSIGN, "/=");
				}
			}
			return new Pair<>(Type.SLASH, String.valueOf(ch));
		} else if(ch == '%') {
			if(scanner.nextChar()) {
				char nc = scanner.currChar();
				if(nc == '=') {
					scanner.nextChar();
					return new Pair<>(Type.PERCENT_ASSIGN, "%=");
				}
			}
			return new Pair<>(Type.PERCENT, String.valueOf(ch));
		} else if(ch == '=') {
			if(scanner.nextChar()) {
				char nc = scanner.currChar();
				if(nc == '=') {
					scanner.nextChar();
					return new Pair<>(Type.EQ, "==");
				}
			}
			return new Pair<>(Type.ASSIGN, String.valueOf(ch));
		} else if(ch == '!') {
			if(scanner.nextChar()) {
				char nc = scanner.currChar();
				if(nc == '=') {
					scanner.nextChar();
					return new Pair<>(Type.NEQ, "!=");
				}
			}
			return new Pair<>(Type.EMARK, String.valueOf(ch));
		} else if(ch == '&') {
			if(scanner.nextChar()) {
				char nc = scanner.currChar();
				if(nc == '&') {
					scanner.nextChar();
					return new Pair<>(Type.AMP_AMP, "&&");
				} else if(nc == '=') {
					scanner.nextChar();
					return new Pair<>(Type.AMP_ASSIGN, "&=");
				}
			}
			return new Pair<>(Type.AMP, String.valueOf(ch));
		} else if(ch == '|') {
			if(scanner.nextChar()) {
				char nc = scanner.currChar();
				if(nc == '|') {
					scanner.nextChar();
					return new Pair<>(Type.BAR_BAR, "||");
				} else if(nc == '=') {
					scanner.nextChar();
					return new Pair<>(Type.BAR_ASSIGN, "|=");
				}
			}
			return new Pair<>(Type.BAR, String.valueOf(ch));
		} else if(ch == '^') {
			if(scanner.nextChar()) {
				char nc = scanner.currChar();
				if(nc == '=') {
					scanner.nextChar();
					return new Pair<>(Type.CARET_ASSIGN, "^=");
				}
			}
			return new Pair<>(Type.CARET, String.valueOf(ch));
		} else if(ch == '<') {
			if(scanner.nextChar()) {
				char nc = scanner.currChar();
				if(nc == '=') {
					scanner.nextChar();
					return new Pair<>(Type.LTEQ, "<=");
				} else if(nc == '<') {
					if(scanner.nextChar() && scanner.currChar() == '=') {
						scanner.nextChar();
						return new Pair<>(Type.SHL_ASSIGN, "<<=");
					}
					return new Pair<>(Type.SHL, "<<");
				}
			}
			return new Pair<>(Type.LT, String.valueOf(ch));
		} else if(ch == '>') {
			if(scanner.nextChar()) {
				char nc = scanner.currChar();
				if(nc == '=') {
					scanner.nextChar();
					return new Pair<>(Type.GTEQ, ">=");
				} else if(nc == '>') {
					if(scanner.nextChar()) {
						char ncc = scanner.currChar();
						if(ncc == '=') {
							scanner.nextChar();
							return new Pair<>(Type.SHR_ASSIGN, ">>=");
						} else if(ncc == '>') {
							if(scanner.nextChar() && scanner.currChar() == '=') {
								scanner.nextChar();
								return new Pair<>(Type.USHR_ASSIGN, ">>>=");
							}
							return new Pair<>(Type.USHR, ">>>");
						}
					}
					return new Pair<>(Type.SHR, ">>");
				}
			}
			return new Pair<>(Type.GT, String.valueOf(ch));
		}
		
		//FINALLY SINGLE CONTROL CHAR, DOT is PART OF IDENTIFIER
		Type type;
		if(ch == '~') {
			type = Type.TILDE;
		} else if(ch == '?') {
			type = Type.QMARK;
		} else if(ch == ';') {
			type = Type.SEMI;
		} else if(ch == ':') {
			type = Type.COLON;
		} else if(ch == ',') {
			type = Type.COMMA;
		} else if(ch == '@') {
			type = Type.AT;
		} else if(ch == '(' || ch == '[' || ch == '{') {
			type = Type.LPAREN;
		} else if(ch == ')' || ch == ']' || ch == '}') {
			type = Type.RPAREN;
		} else {
			type = Type.TEXT;
		}
		
		//ADVANCE NEXT TOKEN
		scanner.nextChar();
		return new Pair<>(type, String.valueOf(ch));
	}
	
	/**
	 * Next token matches the matcher
	 * 
	 * @param matcher
	 * @return
	 */
	public String nextToken(Predicate<Type> matcher) {
		while(hasMoreTokens()) {
			Map.Entry<Type, String> token = nextToken();
			if(matcher.test(token.getKey())) {
				return token.getValue();
			}
		}
		return null;
	}
	
	/**
	 * return next token match type
	 * 
	 * @param type
	 * @return
	 */
	public String nextToken(Type type) {
		return nextToken((tt) -> (tt == type));
	}
	
	/**
	 * return next combined tokens if still matches
	 * 
	 * @param matcher
	 * @return
	 */
	public String nextTokens(Predicate<Type> matcher) {
		StringBuilder sb = new StringBuilder();
		while(hasMoreTokens()) {
			Map.Entry<Type, String> token = nextToken();
			if(!matcher.test(token.getKey())) {
				break;
			}
			sb.append(token.getValue());
		}
		return sb.toString();
	}
	
	/**
	 * return identifier that assuming a NUMBER
	 * 
	 * @return
	 */
	protected Map.Entry<Type, String> nextNumber() {
		String identifier = scanner.nextToken((ch) -> ch == '.' || Character.isLetterOrDigit(ch));
		return new Pair<>(Type.NUMBER, identifier); 
	}
	
	/**
	 * return next identifier
	 * 
	 * @return
	 */
	protected Map.Entry<Type, String> nextIdentifier() {
		String identifier = scanner.nextToken((ch) -> Character.isJavaIdentifierPart(ch));
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
	protected Map.Entry<Type, String> nextComment() {
		Type type = (scanner.currChar() == '*'? Type.COMMENT_B : Type.COMMENT_L);
		String comment;
		//SKIP * or /
		if(!scanner.nextChar()) {
			comment = "";
		} else {
			if(type == Type.COMMENT_L) {
				comment = scanner.nextLine();
			} else {
				comment = scanner.nextToken('*', '/');
				//ASSERT scanner.currChar() == '/'
				scanner.nextChar();
			}
		}
		return new Pair<>(type, comment); 
	}
	
	/**
	 * return string inside quote
	 * 
	 * @return
	 */
	protected Map.Entry<Type, String> nextQuote() {
		Type type = (scanner.currChar() == '"'? Type.QUOTE_D : Type.QUOTE_S);
		String quote;
		//SKIP " or '
		if(!scanner.nextChar()) {
			//EXPECTING END QUOTE
			quote = "";
		} else {
			if(type == Type.QUOTE_D) {
				quote = scanner.nextToken('\\', '"', true);
				//ASSERT scanner.currChar() == '"'
				scanner.nextChar();
			} else {
				quote = scanner.nextToken('\\', '\'', true);
				//ASSERT scanner.currChar() == '\''
				scanner.nextChar();
			}
		}
		return new Pair<>(type, quote); 
	}
}
