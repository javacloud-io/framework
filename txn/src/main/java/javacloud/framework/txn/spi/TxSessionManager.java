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
	void beginSession();
	
	/**
	 * 
	 */
	void endSession();
}
