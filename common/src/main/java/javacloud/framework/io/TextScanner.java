package javacloud.framework.io;

import java.io.IOException;
import java.io.Reader;
import java.util.function.Predicate;

/**
 * Simple interface for scanning characters
 * @author ho
 *
 */
public class TextScanner {
	//SOURCE CHAR STREAM
	public interface CharStream {
		/**
		 * return true if END of stream
		 * @return
		 */
		boolean isEOT();
		
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
		this(new CharStream() {
			int cursor = 0;
			@Override
			public boolean nextChar() {
				return (++ cursor < source.length());
			}
			@Override
			public boolean isEOT() {
				return cursor >= source.length();
			}
			@Override
			public char getChar() {
				return (cursor < source.length()? source.charAt(cursor) : 0);//EOT
			}
		});
	}
	
	/**
	 * Scan from reader source using look a head
	 * 
	 * @param source
	 */
	public TextScanner(final Reader source) {
		this(new CharStream() {
			int cchar	= -2;	//NOT YET READ
			@Override
			public boolean nextChar() {
				try {
					cchar = source.read();
				} catch(IOException ex) {
					//ASSUMING EOF
					cchar = -1;
				}
				return (cchar != -1);
			}
			@Override
			public boolean isEOT() {
				if(cchar == -2) {
					nextChar();
				}
				return (cchar == -1);
			}
			@Override
			public char getChar() {
				if(cchar == -2) {
					nextChar();
				}
				return (cchar >= 0? (char)cchar : 0);//EOT
			}
		});
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
	 * return next token of number of chars
	 * 
	 * @param nchars
	 * @return
	 */
	public String nextToken(int nchars) {
		StringBuilder buf = new StringBuilder();
		do {
			buf.append(currChar());
		} while(nextChar());
		return buf.toString();
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
			if(! nextChar()) {
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
			char ch = currChar();
			if(! matcher.test(ch)) {
				break;
			}
			buf.append(ch);
		} while(nextChar());
		return buf.toString();
	}
	
	/**
	 * return next line from EOL: lf | cr | cr lf
	 * cursor will be positioned to next LINE
	 * 
	 * @return
	 */
	public String nextLine() {
		StringBuilder buf = new StringBuilder();
		boolean lf = false;
		do {
			char ch = currChar();
			if(ch == '\n') {
				nextChar();	//SKIP \n
				break;
			} else if(lf) {
				break;
			}
			//RESET MARKER
			if(ch != '\r') {
				buf.append(ch);
				lf = false;
			} else {
				lf = true;
			}
		} while(nextChar());
		return buf.toString();
	}
	
	/** 
	 * return next token end with both starChar & slashChar, such as comment.
	 * cursor will be positioned at slashChar for matching, invokes nextCharacter() to skip.
	 * 
	 * @param starChar
	 * @param slashChar
	 * @return
	 */
	public String nextToken(char starChar, char slashChar) {
		StringBuilder buf = new StringBuilder();
		boolean star = false;
		do {
			char ch = currChar();
			if(ch == slashChar && star) {
				//KEEP CURSOR AT slashChar
				break;
			} else if(star) {
				//PREVIOUS starChar
				buf.append(starChar);
			}
			
			//RESET MARKER
			if(ch != starChar) {
				buf.append(ch);
				star = false;
			} else {
				star = true;
			}
		} while(nextChar());
		return buf.toString();
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
		StringBuilder buf = new StringBuilder();
		boolean escape = false;
		do {
			char ch = currChar();
			if(ch == quoteChar && !escape) {
				break;
			} else if(escape) {
				//PREVIOUS escapseChar
				buf.append(escapseChar);
			}
			
			//RESET MARKER
			if(ch != escapseChar) {
				buf.append(ch);
				escape = false;
			} else {
				escape = true;
			}
		} while(nextChar());
		return buf.toString();
	}
}
