package javacloud.framework.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Quick & simple encoding utils. Trying to delegate to system utils
 * 
 * @author tobi
 *
 */
public final class Codecs {
	public static final String UTF8 	= "UTF-8";
	private static final char[] DIGITS	= {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
	private Codecs() {
	}
	
	//BASE64
	public static class Base64Encoder implements Function<byte[], String> {
		@Override
		public String apply(byte[] bytes) {
			return apply(bytes, false);
		}
		public static String apply(byte[] bytes, boolean safe) {
			return apply(bytes, safe, false);
		}
		
		/**
		 * Quick & simple ways to encodeBase64 byte. It doesn't broke to PEM.
		 * TODO: speed up the encode process
		 * 
		 * @param bytes
		 * @param safe
		 * @return
		 */
		public static String apply(byte[] bytes, boolean safe, boolean pretty) {
			String	base64 = Base64.getEncoder().encodeToString(bytes);
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
			
			//PRETTY PRINT
			if(pretty) {
				char[] chars = base64.toCharArray();
				StringBuilder buf = new StringBuilder(chars.length);
				for(int i = 1; i <= chars.length; i ++) {
					buf.append(chars[i - 1]);
					if(i % 64 == 0) {
						buf.append('\n');
					}
				}
				base64 = buf.toString();
			}
			return base64;
		}
	}
	public static class Base64Decoder implements Function<String, byte[]> {
		@Override
		public byte[] apply(String base64) {
			return apply(base64, false);
		}
		/**
		 * Return byte from BASE 64. It doesn't broke to PEM
		 * @param base64
		 * @param safe
		 * @return
		 */
		public static byte[] apply(String base64, boolean safe) {
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
			return	Base64.getDecoder().decode(base64);
		}
	}
	
	//HEX
	public static class HexEncoder implements Function<byte[], String> {
		@Override
		public String apply(byte[] bytes) {
			return apply(bytes, false);
		}
		/**
		 * Simple HEX encoding without using any library.
		 * @param bytes
		 * @param upper
		 * @return
		 */
		public static String apply(byte[] bytes, boolean upper) {
			StringBuilder sb = new StringBuilder((bytes.length << 1));
			for(int i = 0; i < bytes.length; i ++) {
				byte hi = (byte) ((bytes[i] & 0xF0) >> 4);
	            byte lo = (byte) (bytes[i] & 0x0F);
	            sb.append(upper? Character.toUpperCase(DIGITS[hi]) : DIGITS[hi]);
	            sb.append(upper? Character.toUpperCase(DIGITS[lo]) : DIGITS[lo]);
			}
			return sb.toString();
		}
		
	}
	public static class HexDecoder implements Function<String, byte[]> {
		@Override
		public byte[] apply(String digits) {
			return apply(digits.toCharArray());
		}
		public static byte[] apply(char[] cdigits) {
			byte[] buf = new byte[(cdigits.length >> 1)];
			for(int i = 0, j = 0; i < buf.length; i ++) {
				int hi = Character.digit(cdigits[j ++], 16);
				int lo = Character.digit(cdigits[j ++], 16);
				buf[i] = (byte)(((hi << 4) | lo) & 0xFF);
			}
			return buf;
		}
	}
	
	//consider replace '+' with '%20'
	public static class UrlEncoder implements Function<Map<String, Object>, String> {
		public String apply(Map<String, Object> params) {
			try {
				return apply(params, true);
			} catch (IOException ex) {
				throw GenericException.of(ex);
			}
		}
		
		/**
		 * consider replace '+' with '%20'
		 * 
		 * @param params
		 * @param multiple
		 * @return
		 * @throws IOException
		 */
		public static String apply(Map<String, Object> params, boolean multiple) throws IOException {
			StringBuilder sb = new StringBuilder();
			for(String name: params.keySet()) {
				Object value = params.get(name);
				if(value == null) {
					continue;
				}
				
				//CONVERT TO LIST
				List<Object> list;
				if(value instanceof Object[]) {
					list =  Objects.asList((Object[])value);
				} else if(value instanceof List) {
					list = Objects.cast(value);
				} else {
					list = Objects.asList(value);
				}
				for(Object val : list) {
					//&NAME=VALUE
					sb.append("&");
					sb.append(URLEncoder.encode(name, UTF8));
					sb.append("=");
					sb.append(URLEncoder.encode(Converters.STRING.apply(val), UTF8));
				}
			}
			
			//SKIP THE FIRST &
			if(sb.length() > 0) {
				return sb.substring(1);
			}
			
			//DON'T HAVE ANYTHING
			return sb.toString();
		}
		
	}
	
	public static class UrlDecoder implements Function<String, Map<String, Object>> {
		@Override
		public Map<String, Object> apply(String params) {
			try {
				return apply(params, true);
			} catch (IOException ex) {
				throw GenericException.of(ex);
			}
		}
		
		/**
		 * 
		 * TODO: need to take care of multiple value decoded.
		 * @param params
		 * @param multiple
		 * 
		 * @return
		 * @throws IOException
		 */
		public static Map<String, Object> apply(String params, boolean multiple) throws IOException {
			//NEW INSTANCE OBJECT
			Map<String, Object> props = Objects.asMap();
			
			//PARSE PROPERTIES
			String[] list = params.split("&");
			for(String p: list) {
				
				//IGNORE EMPTY ONE
				if(Objects.isEmpty(p)) {
					continue;
				}
				
				//JUST IGNORE ALL THE NAME/VALUE EMPTY
				//TODO: need to support multiple value
				int index = p.indexOf('=');
				String name, value;
				if(index > 0) {
					name = URLDecoder.decode(p.substring(0, index), UTF8);
					value= URLDecoder.decode(p.substring(index + 1),UTF8);
				} else {
					name  = URLDecoder.decode(p, UTF8);
					value = "";
				}
				
				//SET VALUE
				if(multiple) {
					if(props.containsKey(name)) {
						List<Object> values = Objects.asList(props.get(name));
						values.add(value);
						props.put(name, values.toArray(new String[values.size()]));
					} else {
						props.put(name, new String[] {value});
					}
				} else {
					props.put(name, value);
				}
			}
			return props;
		}
	}
	
	/**
	 * 
	 * @param bytes
	 * @return
	 */
	public static String toUTF8(byte[] bytes) {
		return toUTF8(bytes, 0, bytes.length);
	}
	
	/**
	 * 
	 * @param bytes
	 * @param offset
	 * @param len
	 * @return UTF8 string encoded.
	 */
	public static String toUTF8(byte[] bytes, int offset, int len) {
		try {
			return new String(bytes, offset, len, UTF8);
		} catch (UnsupportedEncodingException ex) {
			throw GenericException.of(ex);
		}
	}
	
	/**
	 * 
	 * @param utf8
	 * @return byte encoded of UTF8
	 */
	public static byte[] toBytes(String utf8) {
		try {
			return utf8.getBytes(UTF8);
		} catch (UnsupportedEncodingException ex) {
			throw GenericException.of(ex);
		}
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

