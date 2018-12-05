package javacloud.framework.io;

import java.io.Reader;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Simple interface for scanning characters, which supports:
 * 
 * 1. Emit token with EOL using 2 alternate characters: '\r' or '\n'
 * 2. Emit token inside comment using combination of 2 characters: '*' and '/'
 * 3. Emit token inside quote using end and escape characters: '"' and '\'
 * 
 * @author ho
 *
 */
public class TextScanner {
	private final CharStream source;
	private int lineNo		= 1;
	private int columnNo	= 0;
	/**
	 * 
	 * @param source
	 */
	protected TextScanner(CharStream source) {
		this.source = source;
	}
	
	/**
	 * Scan from string source using absolute position
	 * 
	 * @param source
	 */
	public TextScanner(final CharSequence source) {
		this(CharStream.build(source));
	}
	
	/**
	 * Scan from reader source using look a head
	 * 
	 * @param source
	 */
	public TextScanner(final Reader source) {
		this(CharStream.build(source));
	}
	
	/**
	 * return line number
	 * 
	 * @return
	 */
	public int getLineNo() {
		return lineNo;
	}
	
	/**
	 * return column number
	 * 
	 * @return
	 */
	public int getColumnNo() {
		return columnNo;
	}
	
	/**
	 * return true if has MORE
	 * 
	 * @return
	 */
	public boolean hasMoreChars() {
		return !source.isEOT();
	}
	
	/**
	 * return look ahead character
	 * 
	 * @return
	 */
	public char currChar() {
		return source.getChar();
	}
	
	/**
	 * Move to next char and return false if has NONE.
	 * 
	 * @return
	 */
	public boolean nextChar() {
		if(!source.isEOT()) {
			if(source.getChar() == '\n') {
				lineNo ++;
				columnNo = 0;
			} else {
				columnNo ++;
			}
		}
		return source.nextChar();
	}
	
	/**
	 * return a token with matches boundary or EMPTY to consumer
	 * 
	 * @param matcher
	 * @param consumer
	 * @return
	 */
	public boolean nextChars(Predicate<Character> matcher, Consumer<Character> consumer) {
		do {
			char ch = currChar();
			if(! matcher.test(ch)) {
				return hasMoreChars();
			}
			consumer.accept(ch);
		} while(nextChar());
		return false;
	}
	
	/**
	 * return next line from EOL: lf | cr | cr lf
	 * cursor will be positioned to next LINE
	 * 
	 * @param lfChar
	 * @param crChar
	 * @param consumer
	 * @return
	 */
	public boolean nextLine(char lfChar, char crChar, Consumer<Character> consumer) {
		//move cursor pass  \r\n to next line
		if(nextChars((ch) -> (ch != lfChar && ch != crChar), consumer)) {
			char ch = currChar();
			if(nextChar() && ch == lfChar && currChar() == crChar) {
				return nextChar();
			}
			return hasMoreChars();
		}
		return false;
	}
	
	/**
	 * Return token until quoteChar with escapeChar, similar to string quote in java
	 * 
	 * @param escapseChar
	 * @param quoteChar
	 * @param unescape
	 * @return
	 */
	public String nextToken(char escapseChar, char quoteChar, boolean unescape) {
		Token token = (unescape? Token.quote(escapseChar) : new Token());
		nextChars(Matcher.quote(escapseChar, quoteChar), token);
		return token.toString();
	}
	
	/**
	 * 
	 * @param starChar
	 * @param slashChar
	 * @return
	 */
	public String nextToken(char starChar, char slashChar) {
		Token token = Token.slash(starChar);
		nextChars(Matcher.slash(starChar, slashChar), token);
		return token.toString();
	}
	
	/**
	 * return token matches boundary
	 * 
	 * @param matcher
	 * @return
	 */
	public String nextToken(Predicate<Character> matcher) {
		Token token = new Token();
		nextChars(matcher, token);
		return token.toString();
	}
	
	/**
	 * Next line with delimiter: '\r' | '\n' | '\r\n'
	 * 
	 * @return
	 */
	public String nextLine() {
		Token token = new Token();
		nextLine('\r', '\n', token);
		return token.toString();
	}
	
	/**
	 * Skip token matches the matcher, example skipWhitespace()
	 * skipToken((ch) -> Character.isWhitespace(ch))
	 * 
	 * @param matcher
	 * @return
	 */
	public boolean skipChars(Predicate<Character> matcher) {
		return nextChars(matcher, (ch) -> {});
	}
	
	/**
	 * Skip standard whitespace characters
	 * 
	 * @return
	 */
	public boolean skipWhitespace() {
		return skipChars((ch) -> Character.isWhitespace(ch));
	}
	
	/**
	 * Skip to next line
	 * 
	 * @return
	 */
	public boolean skipLine() {
		return nextLine('\r', '\n', (ch) -> {});
	}
	
	//TOKENS
	public static class Token implements Consumer<Character> {
		private final StringBuilder buf = new StringBuilder();
		@Override
		public void accept(Character ch) {
			buf.append(ch.charValue());
		}
		
		@Override
		public String toString() {
			return buf.toString();
		}
		
		//UNESCAPE TOKEN
		public static Token quote(char escapseChar) {
			return new Token() {
				boolean escaped = false;
				@Override
				public void accept(Character ch) {
					if(escaped || ch != escapseChar) {
						super.accept(ch.charValue());
					}
					escaped = (!escaped && ch == escapseChar);
				}
			};
		}
		
		//SLASH TOKEN
		public static Token slash(char starChar) {
			return new Token() {
				boolean stared = false;
				@Override
				public void accept(Character ch) {
					if(stared) {
						super.accept(starChar);
					}
					if(!(stared = (ch == starChar))) {
						super.accept(ch);
					}
				}
			};
		}
	}
		
	//CHAR MATCHERS
	public static abstract class Matcher implements Predicate<Character> {
		/**
		 * Matches quote char while supporting escape char, ex: '\""' -> '""'
		 * 
		 * @param escapseChar
		 * @param quoteChar
		 * @return
		 */
		public static Matcher quote(char escapseChar, char quoteChar) {
			return new Matcher() {
				boolean escaped = false;
				@Override
				public boolean test(Character ch) {
					if(!escaped && ch == quoteChar) {
						return false;
					}
					escaped = (!escaped && ch == escapseChar);
					return true;
				}
			};
		}
		
		/**
		 * Matches double character only, ex: '*''/'
		 * 
		 * @param starChar
		 * @param slashChar
		 * @return
		 */
		public static Matcher slash(char starChar, char slashChar) {
			return new Matcher() {
				boolean stared = false;
				@Override
				public boolean test(Character ch) {
					if(stared && ch == slashChar) {
						return false;
					}
					stared = (ch == starChar);
					return true;
				}
			};
		}
	}
}
