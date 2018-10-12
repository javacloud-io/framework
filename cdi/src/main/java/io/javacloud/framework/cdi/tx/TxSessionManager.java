package io.javacloud.framework.cdi.tx;
/**
 * Abstract UNIT OF WORK management
 * 
 * @author ho
 *
 */
public interface TxSessionManager {
	/**
	 * 
	 */
	public void beginSession();
	
	/**
	 * 
	 */
	public void endSession();
}
