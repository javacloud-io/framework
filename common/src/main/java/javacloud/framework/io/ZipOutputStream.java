package javacloud.framework.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Extended to clean up zip entry without closing out the under need stream
 * 
 * @author aimee
 *
 */
public final class ZipOutputStream extends java.util.zip.ZipOutputStream {
	public ZipOutputStream(OutputStream out) {
		super(out);
	}
	
	/**
	 * Call end when finish.
	 */
	@Override
	public void finish() throws IOException {
		super.finish();
		def.end();
	}
}
