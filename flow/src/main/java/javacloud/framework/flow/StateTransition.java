package javacloud.framework.flow;

/**
 * 
 * @author ho
 *
 */
public interface StateTransition {
	/**
	 * 
	 * @return
	 */
	public boolean isEnd();
	
	//RETRY -> RESUME
	interface Retry extends StateTransition {
		@Override
		default public boolean isEnd() {
			return false;
		}
		
		/**
		 * Delays second prior to resume
		 * @return
		 */
		public int getDelaySeconds();
	}
	
	//SUCCEED -> NEXT
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
	
	//FAILED -> TERMINATE
	interface Failure extends StateTransition {
		@Override
		default public boolean isEnd() {
			return true;
		}
	}
}
