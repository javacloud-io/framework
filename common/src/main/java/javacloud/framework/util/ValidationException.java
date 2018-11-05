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
	 * 
	 * @param message
	 */
	public ValidationException(String message) {
		super(message);
	}
	
	/**
	 * 
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
			Throwable cause = Exceptions.findRootCause(this);
			return cause.getClass().getSimpleName();
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
	
	//ALREADY EXIST EXCEPTION
	public static class AlreadyExists extends ValidationException {
		private static final long serialVersionUID = -1725603370191594789L;
		public AlreadyExists(String subject) {
			super(subject);
		}
	}
}
