package javacloud.framework.txn.spi;
/**
 * Abstract UNIT OF WORK management:
 * 
 * beginSession
 * 	beginTransaction
 * 		doXX
 * 		commit/rollback
 * 	endTransaction
 * endSession
 * 
 * @author ho
 *
 */
public interface SessionManager {
	/**
	 * prepare the session
	 */
	void beginSession();
	
	/**
	 * clean up session
	 */
	void endSession();
}
