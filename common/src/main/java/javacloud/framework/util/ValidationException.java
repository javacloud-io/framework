package javacloud.framework.util;
/**
 * Basic validation for all
 * 
 * @author ho
 *
 */
public class ValidationException extends IllegalArgumentException {
	private static final long serialVersionUID = 4869699128152046268L;
	
	/**
	 * 
	 * @param message
	 * @param cause
	 */
	public ValidationException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * A validation message
	 * @param message
	 */
	public ValidationException(String message) {
		super(message);
	}
	
	/**
	 * Pass through only for subclass
	 */
	protected ValidationException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * Using message as validation error
	 */
	public String getReason() {
		String reason = getMessage();
		if(Objects.isEmpty(reason)) {
			Throwable cause = getCause();
			return (cause == null? "ValidationException" : cause.getClass().getSimpleName());
		}
		return reason;
	}
	
	//NOT FOUND EXCEPTION
	public static class NotFound extends ValidationException {
		private static final long serialVersionUID = -1655317657875278373L;
		public NotFound(String subject) {
			super(subject);
		}
	}
	
	//CONFLICT STATE
	public static class Conflict extends ValidationException {
		private static final long serialVersionUID = -2389670074826460467L;
		public Conflict(String subject) {
			super(subject);
		}
	}
	
	//ALREADY EXIST EXCEPTION
	public static class AlreadyExists extends Conflict {
		private static final long serialVersionUID = -1725603370191594789L;
		public AlreadyExists(String subject) {
			super(subject);
		}
	}
}
