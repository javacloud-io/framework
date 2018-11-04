package javacloud.framework.io;

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
	public static final String JSON = "json";
	public static final String XML  = "xml";
	
	/**
	 * return content type identifier
	 * @return
	 */
	public String type();
	
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
	public <T> T unmarshal(InputStream src, Class<?> type) throws IOException;
}
