package com.appe.framework.io;

import java.util.Date;
/**
 * Enhance reader with more associated with the information such as sha1, length(), timestamp
 * 
 * @author ho
 */
public abstract class BlobChannelReader extends BytesChannelReader {
	/**
	 * sha1 of the content if any, will stream using ETAG
	 * @return
	 */
	public abstract String sha1();
	
	/**
	 * size of data in length if specified
	 * @return
	 */
	public abstract long length();
	
	/**
	 * Last time the data is modified
	 * @return
	 */
	public abstract Date timestamp();
}