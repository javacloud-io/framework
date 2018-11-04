package javacloud.framework.txn.spi;
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
