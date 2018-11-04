package javacloud.framework.util;

import java.io.PrintWriter;
import java.util.zip.CRC32;

import javacloud.framework.io.BytesOutputStream;

/**
 * Unchecked exception to deal with most of the RUNTIME problem. Just to avoid alot of try cache.
 * 
 * @author aimee
 *
 */
public class UncheckedException extends RuntimeException {
	private static final long serialVersionUID = -7421569948910282662L;
	protected UncheckedException() {
	}
	/**
	 * 
	 * @param cause
	 */
	protected UncheckedException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * 
	 * @param message
	 */
	public UncheckedException(String message) {
		super(message);
	}
	
	/**
	 * 
	 * @param message
	 * @param cause
	 */
	public UncheckedException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * 
	 * @throws Throwable
	 */
	public void rethrow() throws Throwable {
		Throwable th = super.getCause();
		if(th != null) {
			throw th;
		}
	}
	
	/**
	 * return a reason code so we can identify the problem, make sure reason is consistent by using CRC of root cause.
	 * 
	 * @return
	 */
	public String getReason() {
		Throwable cause = findRootCause(this);
		return crc32Code(cause);
	}
	
	/**
	 * Try to find cause T and throw it
	 * 
	 * @param causedBy
	 * @throws T
	 */
	public <T extends Throwable> void rethrow(Class<T> causedBy) throws T {
		T t = findCause(this, causedBy);
		if(t != null) {
			throw t;
		}
	}
	
	/**
	 * Find the root cause of the exception at any KIND.
	 * 
	 * @param t
	 * @return
	 */
	public static final Throwable findRootCause(Throwable t) {
		Throwable cause = (t != null ? t.getCause() : null);
		while(cause != null) {
			t = cause;
			cause = t.getCause();
		}
		return t;
	}
	
	/**
	 * Find the root cause which is kind of causedBy.
	 * return NULL if NOT FOUND ANY.
	 * 
	 * @param t
	 * @param causedBy
	 * @return
	 */
	public static final <T extends Throwable> T findCause(Throwable t, Class<T> causedBy) {
		Throwable cause = (t != null ? t.getCause() : null);
		while(cause != null) {
			//OK, found it...
			if(causedBy.isInstance(cause.getClass())) {
				return Objects.cast(cause);
			}
			//OK, recursive
			t = cause.getCause();
		}
		return null;
	}
	
	/**
	 * Test to see if the exception is cause this exception 
	 * 
	 * @param t
	 * @param causeBy
	 * @return
	 */
	public static final boolean isCausedBy(Throwable t, Class<? extends Throwable> causedBy) {
		//BASIC CHECK
		if(t == null || causedBy == null) {
			return false;
		}
		if(causedBy.isInstance(t)) {
			return true;
		}
		//Hunt it down.
		return (findCause(t, causedBy) != null);
	}
	
	/**
	 * return the reason code for given exception.
	 * 
	 * @param t
	 * @return
	 */
	public static final String findReason(Throwable cause) {
		if(cause instanceof UncheckedException) {
			return ((UncheckedException)cause).getReason();
		}
		//GENERIC AS CLASS NAME
		return cause.getClass().getName();
	}
	
	/**
	 * Consistent hash using crc32 is OK.
	 * @param cause
	 * @return
	 */
	static final String crc32Code(Throwable cause) {
		CRC32 crc = new CRC32();
		//CLASS NAME
		String message = cause.getClass().getName();
		crc.update(message.getBytes());
		
		//+ MESSAGE
		message = cause.getMessage();
		if(message != null && !message.isEmpty()) {
			crc.update(message.getBytes());
		}
		return Long.toHexString(crc.getValue()).toUpperCase();
	}
	
	/**
	 * Make sure to always return a single AppeException instead of stack of them.
	 * 
	 * @param t
	 */
	public static UncheckedException wrap(Throwable t) {
		if(t instanceof UncheckedException) {
			return	(UncheckedException)t;
		}
		return	new UncheckedException(t);
	}
	
	/**
	 * 
	 * @param t
	 * @return
	 */
	public static String toStackTrace(Throwable t) {
		BytesOutputStream out = new BytesOutputStream();
		PrintWriter s = new PrintWriter(out);
		t.printStackTrace(s);
		s.flush();
		return out.toString();
	}
}
