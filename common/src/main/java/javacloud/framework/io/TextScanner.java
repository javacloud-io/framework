package javacloud.framework.io;

import java.util.function.Predicate;

/**
 * Simple interface for scanning characters
 * @author ho
 *
 */
public class TextScanner {
	//CHAR STREAM
	public interface Source {
		/**
		 * return true if END of stream
		 * @return
		 */
		boolean isEnd();
		
		/**
		 * Advance cursor to next char if any. return true/false if not
		 * @return
		 */
		boolean nextChar();
		
		/**
		 * return char at current position
		 * @return
		 */
		char getChar();
	}
	private final Source source;
	private int lineNo		= 1;
	private int columnNo	= 0;
	
	/**
	 * 
	 * @param source
	 */
	public TextScanner(Source source) {
		this.source = source;
	}
	
	/**
	 * 
	 * @param source
	 */
	public TextScanner(final CharSequence source) {
		this(new Source() {
			int cursor = 0;
			@Override
			public boolean nextChar() {
				return (++ cursor < source.length());
			}
			@Override
			public boolean isEnd() {
				return cursor >= source.length();
			}
			@Override
			public char getChar() {
				return (cursor < source.length()? source.charAt(cursor) : 0x00);//EOT
			}
		});
	}
	
	/**
	 * return line number
	 * @return
	 */
	public int getLineNo() {
		return lineNo;
	}
	
	/**
	 * return colume number
	 * @return
	 */
	public int getColumnNo() {
		return columnNo;
	}
	
	/**
	 * return true if has MORE
	 * @return
	 */
	public boolean hasMoreTokens() {
		return !source.isEnd();
	}
	
	/**
	 * return look ahead character
	 * @return
	 */
	public char getCharacter() {
		return source.getChar();
	}
	
	/**
	 * Move to next char and return false if has NONE.
	 * 
	 * @return
	 */
	public boolean nextCharacter() {
		if(!source.isEnd()) {
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
	 * Skip token matches the matcher, example skipWhitespace()
	 * skipToken((ch) -> Character.isWhitespace(ch))
	 * 
	 * @param matcher
	 * @return
	 */
	public boolean skipToken(Predicate<Character> matcher) {
		while(matcher.test(source.getChar())) {
			if(! nextCharacter()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * return a token with matches boundary or EMPTY
	 * 
	 * @param matcher
	 * @return
	 */
	public String nextToken(Predicate<Character> matcher) {
		StringBuilder buf = new StringBuilder();
		do {
			char ch = getCharacter();
			if(!matcher.test(ch)) {
				break;
			}
			buf.append(ch);
		} while(nextCharacter());
		return buf.toString();
	}
	
	/**
	 * return next line from EOL: lf | cr | cr lf
	 * cursor will be positioned to next LINE
	 * 
	 * @return
	 */
	public String nextLine() {
		return nextToken(new Predicate<Character>() {
			boolean lf = false;
			@Override
			public boolean test(Character ch) {
				//EOL: LF CR
				if(ch == '\n') {
					nextCharacter();
					return false;
				} else if(ch == '\r') {
					lf = true;
				} else if(lf) {
					//ELF: LF
					return false;
				}
				return true;
			}
		});
	}
	
	/** 
	 * return next token match both start & slash character.
	 * cursor will be positioned at slashChar for matching, invokes nextCharacter() to skip.
	 * 
	 * @param starChar
	 * @param slashChar
	 * @return
	 */
	public String nextToken(char starChar, char slashChar) {
		String token = nextToken(new Predicate<Character>() {
			boolean star = false;
			@Override
			public boolean test(Character ch) {
				if(ch == slashChar && star) {
					return false;
				}
				//LEFT OVER STAR
				star = (ch == starChar);
				return true;
			}
		});
		
		//TRIM TRAIL starChar if MATCHED
		if(getCharacter() == slashChar) {
			return token.substring(0, token.length() - 1);
		}
		return token;
	}
	
	/**
	 * return next string with quote trailing
	 * cursor will be positioned at quoteChar for matching, invokes nextCharacter() to skip.
	 * 
	 * @param quoteChar
	 * @param escapseChar
	 * @return
	 */
	public String nextQuote(char quoteChar, char escapseChar) {
		return nextToken(new Predicate<Character>() {
			boolean escape = false;
			@Override
			public boolean test(Character ch) {
				if(ch == quoteChar && !escape) {
					return false;
				}
				//LEFT OVER STAR
				escape = (ch == escapseChar);
				return true;
			}
		});
	}
}
