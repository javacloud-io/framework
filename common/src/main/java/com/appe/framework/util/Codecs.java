package com.appe.framework.util;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

import javax.xml.bind.DatatypeConverter;

import com.appe.framework.AppeException;

/**
 * Quick & simple encoding utils. Trying to delegate to system utils
 * @author tobi
 *
 */
public final class Codecs {
	public static final String UTF8 	= "UTF-8";
	private static final char[] DIGITS	= {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
	private Codecs() {
	}
	
	/**
	 * 
	 * @param bytes
	 * @return
	 */
	public static String encodeUTF8(byte[] bytes) {
		return encodeUTF8(bytes, 0, bytes.length);
	}
	
	/**
	 * return UTF8 string encoded.
	 * 
	 * @param bytes
	 * @param offset
	 * @param len
	 * @return
	 */
	public static String encodeUTF8(byte[] bytes, int offset, int len) {
		try {
			return new String(bytes, offset, len, UTF8);
		} catch (UnsupportedEncodingException ex) {
			throw AppeException.wrap(ex);
		}
	}
	
	/**
	 * return byte encoded of UTF8
	 * @param utf8
	 * @return
	 */
	public static byte[] decodeUTF8(String utf8) {
		try {
			return utf8.getBytes(UTF8);
		} catch (UnsupportedEncodingException ex) {
			throw AppeException.wrap(ex);
		}
	}
	
	/**
	 * Quick & simple ways to encodeBase64 byte. It doesn't broke to PEM.
	 * TODO: speed up the encode process
	 * 
	 * @param bytes
	 * @param safe
	 * @return
	 */
	public static String encodeBase64(byte[] bytes, boolean safe) {
		String	base64 = DatatypeConverter.printBase64Binary(bytes);
		if(safe) {
			char[] chars = base64.toCharArray();
			int count = 0;
			for(; count < chars.length; count ++) {
				if(chars[count] == '+') {
					chars[count] = '-';
				} else if(chars[count] == '/') {
					chars[count] = '_';
				} else if(chars[count] == '=') {
					break;
				}
			}
			//NEW BASE64
			base64 = new String(chars, 0, count);
		}
		return base64;
	}
	
	/**
	 * Return byte from BASE 64. It doesn't broke to PEM
	 * @param base64
	 * @param safe
	 * @return
	 */
	public static byte[] decodeBase64(String base64, boolean safe) {
		//NEED TO MODIFY THE BUFF
		if(safe) {
			char[] chars = new char[(((base64.length() - 1) >> 2) + 1) << 2]; //round up to 4
			for(int i = base64.length() - 1; i >=0; i --) {
				char c = base64.charAt(i);
				if(c == '-') {
					c = '+';
				}else if(c == '_') {
					c = '/';
				}
				chars[i] = c;
			}
			
			//PADDING
			for(int i = base64.length(); i < chars.length; i ++) {
				chars[i] = '=';
			}
			base64 = new String(chars);
		}
		return	DatatypeConverter.parseBase64Binary(base64);
	}
	
	/**
	 * Simple HEX encoding without using any library.
	 * @param bytes
	 * @return
	 */
	public static String encodeHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder((bytes.length << 1));
		for(int i = 0; i < bytes.length; i ++) {
			byte hi = (byte) ((bytes[i] & 0xF0) >> 4);
            byte lo = (byte) (bytes[i] & 0x0F);
            sb.append(DIGITS[hi]);
            sb.append(DIGITS[lo]);
		}
		return sb.toString();
	}
	
	/**
	 * Simple HEX decoding without using library.
	 * @param hexes
	 * @return
	 */
	public static byte[] decodeHex(String digits) {
		char[] cdigits = digits.toCharArray();
		byte[] buf = new byte[(cdigits.length >> 1)];
		for(int i = 0, j = 0; i < buf.length; i ++) {
			int hi = Character.digit(cdigits[j ++], 16);
			int lo = Character.digit(cdigits[j ++], 16);
			buf[i] = (byte)(((hi << 4) | lo) & 0xFF);
		}
		return buf;
	}
	
	/**
	 * Make sure to broken down to 64 characters per line except the last one.
	 * @param encoded
	 * @return
	 */
	public static String encodePEM(byte[] encoded) {
		String base64 = encodeBase64(encoded, false);
		
		char[] cbase64 = base64.toCharArray();
		StringBuilder buf = new StringBuilder();
		for(int i = 1; i <= cbase64.length; i ++) {
			buf.append(cbase64[i - 1]);
			if(i % 64 == 0) {
				buf.append('\n');
			}
		}
		
		//ASSIGN NEW STRING
		return buf.toString();
	}
	
	/**
	 * Using base 36 to convert the ID, much safer and nicer!!
	 * 
	 * FIXME: Using BigInteger is kind of 2 time slower than hand crafted one (base64 or base 16)
	 * @return RANDOM ID 16 bytes
	 */
	public static String randomID() {
		BigInteger id = PRNG.nextBInteger(16);
		return id.toString(36);
	}
}
