package com.appe.framework.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Provide the object mapping capability
 * 
 * @author ho
 *
 */
public interface Externalizer {
	public static final String JSON = "application/json";
	
	/**
	 * return content type identifier
	 * @return
	 */
	public String contentType();
	
	/**
	 * 
	 * @param v
	 * @param dst
	 * @throws IOException
	 */
	public void marshal(Object v, OutputStream dst) throws IOException;
	
	/**
	 * 
	 * @param src
	 * @param type
	 * @return
	 * @throws IOException
	 */
	public <T> T unmarshal(InputStream src, Class<T> type) throws IOException;
}
