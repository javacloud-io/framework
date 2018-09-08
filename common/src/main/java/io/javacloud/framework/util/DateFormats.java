package io.javacloud.framework.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
/**
 * TODO:
 * 
 * Should consider make it thread safe using thread local.
 * 
 * @author tobi
 *
 */
public final class DateFormats {
	//UTC ISO8601?
	public static final String	ISO8601    = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	public static final String	ISO8601_S3 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	private DateFormats() {
	}
	
	/**
	 * 
	 * @return
	 */
	public static DateFormat getUTC() {
		return getUTC(ISO8601);
	}
	
	/**
	 * 
	 * @param pattern
	 * @return
	 */
	public static DateFormat getUTC(String pattern) {
		return get(pattern, TimeZone.getTimeZone("UTC"));
	}
	
	/**
	 * 
	 * @param pattern
	 * @param tzone
	 * @return
	 */
	public static DateFormat get(String pattern, TimeZone tzone) {
		SimpleDateFormat df = new SimpleDateFormat(pattern);
		df.setTimeZone(tzone);
		return df;
	}
}
