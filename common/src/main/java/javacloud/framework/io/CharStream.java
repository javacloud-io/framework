package javacloud.framework.io;

import java.io.IOException;
import java.io.Reader;

/**
 * SOURCE CHAR STREAM
 * 
 * @author ho
 *
 */
public interface CharStream {
	/**
	 * 
	 * @return true if END of stream
	 */
	public boolean isEOT();
	
	/**
	 * Advance cursor to next char if any.
	 * @return true/false if not
	 */
	public boolean nextChar();
	
	/**
	 * 
	 * @return char at current position
	 */
	public char getChar();
	
	/**
	 * 
	 * @param source
	 * @return
	 */
	public static CharStream build(CharSequence source) {
		return	new CharStream() {
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
		};
	}
		
	/**
	 * 
	 * @param source
	 * @return
	 */
	public static CharStream build(Reader source) {
		return new CharStream() {
			int cchar	= Integer.MIN_VALUE;	//NOT YET READ
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
				if(cchar == Integer.MIN_VALUE) {
					nextChar();
				}
				return (cchar == -1);
			}
			@Override
			public char getChar() {
				if(cchar == Integer.MIN_VALUE) {
					nextChar();
				}
				return (cchar >= 0? (char)cchar : 0);//EOT
			}
		};
	}
}