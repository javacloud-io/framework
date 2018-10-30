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
		default public double getBackoffRate() {
			return 1.0;
		}
	}
	
	//SUCCESS
	interface Success extends StateTransition {
		/**
		 * Delays second prior to move to next state
		 * @return
		 */
		default public int getDelaySeconds() {
			return 0;
		}
		
		/**
		 * 
		 * @return
		 */
		public String getNext();
	}
	
	//FAILURE
	interface Failure extends StateTransition {
	}
}
