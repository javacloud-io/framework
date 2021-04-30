package javacloud.framework.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;

/**
 * Unchecked exceptions
 * 
 * @author ho
 *
 */
public final class InternalException extends RuntimeException {
	private static final long serialVersionUID = -3859926551631789301L;
	
	public interface Reasonable {
		String getReason();
	}
	
	// Internal unchecked construction
	private InternalException(Throwable cause) {
		super(cause);
	}
	
	private InternalException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Wrap as runtime exception
	 * 
	 * @param t
	 * @return
	 */
	public static RuntimeException of(Throwable t) {
		if (t instanceof RuntimeException) {
			return (RuntimeException)t;
		}
		return new InternalException(t);
	}
	
	/**
	 * Unchecked with a new message if applicable
	 * 
	 * @param message
	 * @param t
	 * @return
	 */
	public static RuntimeException of(String message, Throwable t) {
		return new InternalException(message, unwrap(t));
	}
	
	/**
	 * Resolve the exception if being wrapped some how
	 * 
	 * @param t
	 * @return
	 */
	static Throwable unwrap(Throwable t) {
		if (t instanceof InternalException) {
			return t.getCause();
		}
		if (t instanceof InvocationTargetException) {
			return ((InvocationTargetException)t).getTargetException();
		}
		return t;
	}
	
	/**
	 * Walk through the exception trace with cause, find one that matches the causeBy class or NONE.
	 * 
	 * @param t
	 * @param causedBy
	 * @return
	 */
	public static <T extends Throwable> T getCause(Throwable t, Class<T> causedBy) {
		do {
			Throwable cause = (t != null ? unwrap(t) : null);
			if (cause == null || causedBy.isInstance(cause)) {
				return Objects.cast(cause);
			}
			t = cause.getCause();
		} while (t != null);
		return null;
	}
	
	/**
	 * Test to see if the exception is cause this exception 
	 * 
	 * @param t
	 * @param causeBy
	 * @return
	 */
	public static boolean isCausedBy(Throwable t, Class<? extends Throwable> causedBy) {
		//BASIC CHECK
		if (t == null || causedBy == null) {
			return false;
		}
		if (causedBy.isInstance(t)) {
			return true;
		}
		//Hunt it down.
		return (getCause(t, causedBy) != null);
	}
	
	/**
	 * 
	 * @param t
	 * @return the reason code for given exception.
	 */
	public static String getReason(Throwable cause) {
		if (cause instanceof Reasonable) {
			return ((Reasonable)cause).getReason();
		}
		
		//GENERIC AS CLASS NAME
		return (cause == null? InternalException.class.getName() : cause.getClass().getName());
	}
	
	/**
	 * 
	 * @param t
	 * @param depth
	 * @return stack trace string with depth
	 */
	public static String getStackTrace(Throwable t, int depth) {
		ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
		PrintWriter s = new PrintWriter(out);
		if (depth < 0) {
			t.printStackTrace(s);
		} else {
			s.println(t);
			StackTraceElement[] stackTraces = t.getStackTrace();
			for (int i = 0; i < depth && i < stackTraces.length; i ++) {
				s.println("\tat " + stackTraces[i]);
			}
		}
		s.flush();
		return out.toString();
	}
}
