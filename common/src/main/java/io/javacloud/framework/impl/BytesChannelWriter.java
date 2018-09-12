package io.javacloud.framework.impl;

import io.javacloud.framework.util.UncheckedException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
/**
 * Simple bytes channel which guarantee transfer whole byte buffer.
 * @author aimee
 *
 */
public abstract class BytesChannelWriter implements WritableByteChannel {
	/**
	 * 100% percent sure will transfer all data or EXCEPRION
	 * @param buf
	 * @return
	 * @throws IOException
	 */
	public long transferFrom(ByteBuffer buf) throws IOException {
		long tcount = 0;
		while(buf.hasRemaining()) {
			//read & break if EOF
			int write = write(buf);
			if(write > 0) {
				tcount += write;
			}
		}
		return tcount;
	}
	
	/**
	 * Call to abort the current write, anything written so far should be rollback/clean up...
	 * By default, just call close()!!!
	 * @return
	 */
	public void abort() {
		try {
			close();
		}catch(IOException ex) {
			throw UncheckedException.wrap(ex);
		}
	}
	/**
	 * 
	 * @param wbc
	 * @return
	 */
	public static BytesChannelWriter wrap(final WritableByteChannel wbc) {
		if(wbc instanceof BytesChannelWriter) {
			return (BytesChannelWriter)wbc;
		}
		
		//WRAP A CHANNEL.
		return new BytesChannelWriter() {
			@Override
			public int write(ByteBuffer src) throws IOException {
				return wbc.write(src);
			}

			@Override
			public boolean isOpen() {
				return wbc.isOpen();
			}

			@Override
			public void close() throws IOException {
				wbc.close();
			}
		};
	}
}
