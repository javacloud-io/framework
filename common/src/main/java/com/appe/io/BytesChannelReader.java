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
package com.appe.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
/**
 * 
 * @author aimee
 *
 */
public abstract class BytesChannelReader implements ReadableByteChannel {
	/**
	 * Transfer all of the DATA. 
	 * @param channel
	 * @return
	 * @throws IOException
	 */
	public long transferTo(WritableByteChannel channel) throws IOException {
		return transferTo(channel, -1);
	}
	
	/**
	 * Default chunk size to 4096 (4KB)
	 * @param channel
	 * @param count
	 * @return
	 */
	public long transferTo(WritableByteChannel channel, long count) throws IOException {
		return transferTo(channel, count, 4096);
	}
	
	/**
	 * Transfer up to [count] bytes to other channel if HAVE. -1 to transfer EVERYTHING.
	 * @param channel
	 * @param count
	 * @param chunk
	 * @return
	 */
	public long transferTo(WritableByteChannel channel, long count, int chunk) throws IOException {
		//NO NEED TO TRANSFER AT THIS POINT
		if(count == 0 || count < -1) {
			return 0;
		}
		
		//ALLOCATE BUFFER & TRANSFER ALL.
		ByteBuffer buf = ByteBuffer.allocate(chunk);
		long tcount = 0;
		while(true) {
			
			//Fixed count => ADJUST READ LIMIT
			if(count > 0) {
				long remaining = count - tcount;
				if(remaining == 0) {
					break;	//EOF
				} else if(remaining < chunk) {
					buf.limit((int)remaining);
				}
			}
			
			//read & break if EOF
			int read = read(buf);
			if(read < 0) {
				break;
			}
			//update transfer count
			tcount += read;
			
			//write to the channel, UNTIL HAVE NO MORE DATA.
			buf.flip();
			while (buf.hasRemaining()) {  
				channel.write(buf);  
			}
			
			//reset the buffer to original state.
			buf.clear();
		}
		return	tcount;
	}
	
	/**
	 * Transfer to all the buffer remaining until EOF.
	 * @param buf
	 * @return
	 * @throws IOException
	 */
	public long transferTo(ByteBuffer buf) throws IOException {
		long tcount = 0;
		while(buf.hasRemaining()) {
			//read & break if EOF
			int read = read(buf);
			if(read < 0) {
				break;
			}
			//update transfer count.
			tcount += read;
		}
		return tcount;
	}
	
	/**
	 * Make sure to wrap a resource READER. MAKE SURE NOT TO WASTED.
	 * @param rbc
	 * @return
	 */
	public final static BytesChannelReader wrap(final ReadableByteChannel rbc) {
		if(rbc instanceof BytesChannelReader) {
			return	(BytesChannelReader)rbc;
		}
		
		//WRAP A CHANNEL.
		return	new BytesChannelReader() {
			@Override
			public int read(ByteBuffer dst) throws IOException {
				return rbc.read(dst);
			}
			@Override
			public boolean isOpen() {
				return rbc.isOpen();
			}
			@Override
			public void close() throws IOException {
				rbc.close();
			}
		};
	}
	
	/**
	 * OK, CONVERT BYTES STREAM.
	 * 
	 * @param ins
	 * @return
	 * @throws IOException
	 */
	public final static BytesOutputStream toBytesStream(InputStream ins) throws IOException {
		return toBytesStream(Channels.newChannel(ins));
	}
	
	/**
	 * TRANSFER ALL THE BYTES CHANNEL TO OUT STREAM
	 * 
	 * @param channel
	 * @return
	 * @throws IOException
	 */
	public final static BytesOutputStream toBytesStream(ReadableByteChannel channel) throws IOException {
		BytesOutputStream dst = new BytesOutputStream(1024);
		WritableByteChannel writer = Channels.newChannel(dst);
		wrap(channel).transferTo(writer);
		writer.close();
		return dst;
	}
}
