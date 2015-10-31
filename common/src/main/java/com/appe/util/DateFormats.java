/** 
 * Copyright 2015 APPE, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.appe.util;

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
	public static final String	UTC_ISO8601    = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	public static final String	UTC_ISO8601_MS = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	private DateFormats() {
	}
	
	/**
	 * 
	 * @return
	 */
	public static DateFormat getUTC() {
		return getUTC(UTC_ISO8601);
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
