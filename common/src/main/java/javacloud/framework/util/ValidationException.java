package javacloud.framework.util;

import java.util.regex.Pattern;

/**
 * Basic validation for all
 * 
 * @author ho
 *
 */
public class ValidationException extends IllegalArgumentException implements InternalException.Reasonable {
	private static final long serialVersionUID = 4869699128152046268L;
	
	// DOMAIN & EMAIL
	public static final String REGEX_DOMAIN = "^[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*$";
	public static final String REGEX_EMAIL 	= "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	// User input ID, alpha numeric...
	public static final String REGEX_ID 	= "^[_\\p{L}0-9-@\\.:]+$";
		
	private final String reason;
	
	/**
	 * 
	 * @param error
	 * @param message
	 */
	public ValidationException(String reason, String message) {
		super(message);
		this.reason = reason;
	}
	
	/**
	 * A validation message
	 * @param message
	 */
	public ValidationException(String reason) {
		this.reason = reason;
	}
	
	protected ValidationException(String reason, Throwable cause) {
		super(cause);
		this.reason = reason;
	}
	
	@Override
	public String getMessage() {
		String message = super.getMessage();
		return (Objects.isEmpty(message)? reason: message);
	}

	/**
	 * Using message as validation error
	 */
	@Override
	public String getReason() {
		if (Objects.isEmpty(reason)) {
			Throwable cause = getCause();
			return (cause == null? ValidationException.class : cause.getClass()).getSimpleName();
		}
		return reason;
	}
	
	//NOT FOUND EXCEPTION
	public static class NotFound extends ValidationException {
		private static final long serialVersionUID = -1655317657875278373L;
		
		public NotFound(String reason, String message) {
			super(reason, message);
		}
		public NotFound(String subject) {
			this(null, subject);
		}
	}
	
	//CONFLICT STATE
	public static class Conflict extends ValidationException {
		private static final long serialVersionUID = -2389670074826460467L;
		
		public Conflict(String reason, String message) {
			super(reason, message);
		}
		
		public Conflict(String subject) {
			this(null, subject);
		}
	}
	
	//ALREADY EXIST EXCEPTION
	public static class AlreadyExists extends Conflict {
		private static final long serialVersionUID = -1725603370191594789L;
		
		public AlreadyExists(String reason, String message) {
			super(reason, message);
		}
		
		public AlreadyExists(String subject) {
			this(null, subject);
		}
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
