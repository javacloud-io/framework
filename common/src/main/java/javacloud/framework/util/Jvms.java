package javacloud.framework.util;

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
	 * 
	 * @return java home folder.
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
	 * 
	 * @return user home folder
	 */
	public static String getUserHome() {
		return System.getProperty("user.dir");
	}
	
	/**
	 * 
	 * @return full vendor os/version
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
	 * 
	 * @return current local host address.
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
	public static boolean isAddressInRange(String address, String range) throws UnknownHostException {
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
		
		//MATCH REMAINING BITS IF ANY RIGHT TO LEFT
		int mb = bits % 8;
		if(mb > 0) {
			byte bb = baddress[nb], bp = paddress[nb];
			for(int i = 0; i < mb; i ++) {
				if((bb & 0x01) != (bp & 0x01)) {
					return false;
				}
				
				//SHIFT RIGHT
				bb >>= 1;
				bp >>= 1;
			}
		}
		//PERFECT
		return true;
	}
}
