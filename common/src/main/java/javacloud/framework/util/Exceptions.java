package javacloud.framework.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Pattern;

/**
 * Unchecked exceptions
 * 
 * @author ho
 *
 */
public final class Exceptions extends RuntimeException {
	private static final long serialVersionUID = -3859926551631789301L;
	
	// DOMAIN & EMAIL
	public static final String REGEX_DOMAIN = "^[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*$";
	public static final String REGEX_EMAIL 	= "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	// User input ID, alpha numeric...
	public static final String REGEX_ID 	= "^[_\\p{L}0-9-@\\.:]+$";
	
	// Internal unchecked construction
	private Exceptions(Throwable cause) {
		super(cause);
	}
	private Exceptions(String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Find the root cause of the exception at any KIND.
	 * 
	 * @param t
	 * @return
	 */
	public static Throwable getRootCause(Throwable t) {
		Throwable cause = (t != null ? t.getCause() : null);
		while(cause != null) {
			t = getCause(cause);
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
	public static <T extends Throwable> T getCause(Throwable t, Class<T> causedBy) {
		Throwable cause = (t != null ? t.getCause() : null);
		while(cause != null) {
			cause = getCause(cause);
			//OK, found it...
			if(causedBy.isInstance(cause)) {
				return Objects.cast(cause);
			}
			//OK, recursive
			t = cause.getCause();
		}
		return null;
	}
	
	/**
	 * Unwrap the exception if being wrapped somehow
	 * 
	 * @param t
	 * @return
	 */
	public static Throwable getCause(Throwable t) {
		if(t instanceof Exceptions) {
			return t.getCause();
		}
		if(t instanceof InvocationTargetException) {
			return ((InvocationTargetException)t).getTargetException();
		}
		return t;
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
		if(t == null || causedBy == null) {
			return false;
		}
		if(causedBy.isInstance(t)) {
			return true;
		}
		//Hunt it down.
		return (getCause(t, causedBy) != null);
	}
	
	/**
	 * return the reason code for given exception.
	 * 
	 * @param t
	 * @return
	 */
	public static String getReason(Throwable cause) {
		if(cause instanceof ValidationException) {
			return ((ValidationException)cause).getReason();
		}
		//GENERIC AS CLASS NAME
		return (cause == null? null : cause.getClass().getName());
	}
	
	/**
	 * return stack trace string with depth
	 * @param t
	 * @param depth
	 * @return
	 */
	public static String getStackTrace(Throwable t, int depth) {
		ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
		PrintWriter s = new PrintWriter(out);
		if(depth < 0) {
			t.printStackTrace(s);
		} else {
			s.println(t);
			StackTraceElement[] stackTraces = t.getStackTrace();
			for(int i = 0; i < depth && i < stackTraces.length; i ++) {
				s.println("\tat " + stackTraces[i]);
			}
		}
		s.flush();
		return out.toString();
	}
	
	/**
	 * Wrap as runtime exception
	 * 
	 * @param t
	 * @return
	 */
	public static RuntimeException asUnchecked(Throwable t) {
		if(t instanceof RuntimeException) {
			return (RuntimeException)t;
		}
		return new Exceptions(t);
	}
	
	/**
	 * Unchecked with a new message if applicable
	 * 
	 * @param message
	 * @param t
	 * @return
	 */
	public static RuntimeException asUnchecked(String message, Throwable t) {
		return new Exceptions(message, getCause(t));
	}
	
	/**
	 * Make sure string value meet minimum length.
	 * 
	 * @param value
	 * @param minLength
	 * @param reason
	 */
	public static void assertMinLength(String value, int minLength, String reason) {
		if (value == null || value.length() < minLength) {
			throw new ValidationException(reason);
		}
	}

	/**
	 * Make sure string value meet maximum length
	 * 
	 * @param value
	 * @param maxLength
	 * @param reason
	 */
	public static void assertMaxLength(String value, int maxLength, String reason) {
		if (value != null && value.length() > maxLength) {
			throw new ValidationException(reason);
		}
	}

	/**
	 * Make sure the value matching the pattern, throw exception if not match.
	 * 
	 * @param value
	 * @param pattern
	 * @param reason
	 * @throws ValidationException
	 */
	public static void assertPattern(String value, String pattern, String reason) throws ValidationException {
		if (value == null || !Pattern.matches(pattern, value)) {
			throw new ValidationException(reason);
		}
	}

	/**
	 * Make sure the ID is valid
	 * 
	 * @param value
	 * @param reason
	 * @throws ValidationException
	 */
	public static void assertId(String value, String reason) throws ValidationException {
		assertPattern(value, REGEX_ID, reason);
	}

	/**
	 * Make sure domain is valid and match the expression
	 * 
	 * @param value
	 * @param reason
	 * @throws ValidationException
	 */
	public static void assertDomain(String value, String reason) throws ValidationException {
		assertPattern(value, REGEX_DOMAIN, reason);
	}
	
	/**
	 * 
	 * @param value
	 * @param reason
	 * @throws ValidationException
	 */
	public static void assertEmail(String value, String reason) throws ValidationException {
		assertPattern(value, REGEX_EMAIL, reason);
	}
	
	/**
	 * 
	 * @param subject
	 * @throws ValidationException
	 */
	public static void assertNotFound(String subject) throws ValidationException {
		throw new ValidationException.NotFound(subject);
	}
	
	/**
	 * 
	 * @param subject
	 * @throws ValidationException
	 */
	public static void assertAlreadyExists(String subject) throws ValidationException {
		throw new ValidationException.AlreadyExists(subject);
	}
}
