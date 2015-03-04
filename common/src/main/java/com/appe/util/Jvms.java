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

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Provide basic stuff, rich enough to talk to specific OS.
 * 
 * @author aimee
 *
 */
public final class Jvms {
	private	static final boolean windows;
	private static final boolean mac;
	private static final boolean arch64;
	
	//cache most often usage 
	static {
		String osName = System.getProperty("os.name").toLowerCase();
		windows = osName.indexOf("win") >= 0;
		mac		= osName.indexOf("mac") >= 0;
		arch64  = "64".equals(System.getProperty("sun.arch.data.model"));
	}
	
	//protected
	private Jvms() {
	}
	/**
	 * Return java home folder.
	 * @return
	 */
	public static String getJavaHome() {
		return System.getProperty("java.home");
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getJavaVersion() {
		return System.getProperty("java.version");
	}
	
	/**
	 * Return user home folder
	 * @return
	 */
	public static String getUserHome() {
		return System.getProperty("user.dir");
	}
	
	/**
	 * return full vendor os/version
	 * 
	 * @return
	 */
	public static String getOsDetails() {
		return	System.getProperty("os.arch") + "; " + System.getProperty("os.name") + "/" + System.getProperty("os.version");
	}
	
	/**
	 * 
	 * @return true if running on window
	 */
	public static boolean isWindows() {
		return windows;
	}
	
	/**
	 * 
	 * @return true if running on OSX
	 */
	public static boolean isMac() {
		return mac;
	}
	
	/**
	 * 
	 * @return true if 64 bits architecture
	 */
	public static boolean isArch64() {
		return arch64;
	}
	
	/**
	 * return current local host address.
	 * 
	 * @return
	 * @throws UnknownHostException
	 */
	public static InetAddress getLocalAddress() throws UnknownHostException {
			return	InetAddress.getLocalHost();
	}
	
	/**
	 * Validate to see if given address match the rangeIP definition.
	 * 
	 * 192.168.1.0/24 -> 192.168.1.0 - 192.168.1.255 (first 24 bits have to match)
	 * 
	 * @param address
	 * @param range
	 * @return
	 * @throws UnknownHostException
	 */
	public static boolean ipRange(String address, String range) throws UnknownHostException {
		byte[] baddress = InetAddress.getByName(address).getAddress();
		int bits, sep = range.lastIndexOf('/');
		if(sep <= 0) {
			bits = baddress.length * 8;	//IP4-6
		} else {
			bits = Integer.valueOf(range.substring(sep + 1).trim());
			range = range.substring(0, sep).trim();
		}
		
		//NO BIT NEED TO BE MATCH
		if(bits <= 0) {
			return true;
		}
		
		//VERITY NUMBER OF BIT NEED TO BE MATCH
		byte[] paddress = InetAddress.getByName(range).getAddress();
		int nb = bits / 8;
		
		//TOO MANY BYTES NEED TO MATCH
		if(nb > baddress.length || nb > paddress.length) {
			return false;
		}
		
		//MATCH ALL THE BYTES
		for(int i = 0; i < nb; i ++) {
			if(baddress[i] != paddress[i]) {
				return false;
			}
		}
		
		//MATCH REMAINING BITS
		int mb = bits % 8;
		byte bb = baddress[nb], bp = paddress[nb];
		for(int i = 0; i < mb; i ++) {
			if((bb & 0x80) != (bp & 0x80)) {
				return false;
			}
			
			//SHIFT LEFT
			bb <<= 1;
			bp <<= 1;
		}
		
		//PERFECT
		return true;
	}
}
