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
public final class GenericException extends RuntimeException {
	private static final long serialVersionUID = -3859926551631789301L;
	
	// DOMAIN & EMAIL
	public static final String REGEX_DOMAIN = "^[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*$";
	public static final String REGEX_EMAIL 	= "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	// User input ID, alpha numeric...
	public static final String REGEX_ID 	= "^[_\\p{L}0-9-@\\.:]+$";
	
	// Internal unchecked construction
	private GenericException(Throwable cause) {
		super(cause);
	}
	
	private GenericException(String message, Throwable cause) {
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
		return new GenericException(t);
	}
	
	/**
	 * Unchecked with a new message if applicable
	 * 
	 * @param message
	 * @param t
	 * @return
	 */
	public static RuntimeException of(String message, Throwable t) {
		return new GenericException(message, unwrap(t));
	}
	
	/**
	 * Resolve the exception if being wrapped some how
	 * 
	 * @param t
	 * @return
	 */
	static Throwable unwrap(Throwable t) {
		if (t instanceof GenericException) {
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
		if (cause instanceof ValidationException) {
			return ((ValidationException)cause).getReason();
		}
		//GENERIC AS CLASS NAME
		return (cause == null? null : cause.getClass().getName());
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
