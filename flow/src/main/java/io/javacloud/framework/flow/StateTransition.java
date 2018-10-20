package io.javacloud.framework.flow;

/**
 * 
 * @author ho
 *
 */
public interface StateTransition {
	public boolean isEnd();
	//RETRY
	interface Retry extends StateTransition {
		/**
		 * 
		 * @return
		 */
		public int getMaxAttempts();
		
		/**
		 * 
		 * @return
		 */
		public int getIntervalSeconds();
		
		/**
		 * 
		 * @return
		 */
		public double getBackoffRate();
		
		/**
		 * 
		 * @return
		 */
		public int getTimeoutSeconds();
	}
	//SUCCESS
	interface Success extends StateTransition {
		public String getNext();
	}
	//FAILURE
	interface Failure extends StateTransition {
	}
}
