package javacloud.framework.flow.spi;

import java.util.Date;

/**
 * 
 * @author ho
 *
 */
public interface FlowExecution {
	//EXECUTION STATUS
	public enum Status {
		SCHEDULED,
		INPROGRESS,
		
		SUCCEEDED,
		FAILED,
		CANCELLED;
		
		//COMPLETED
		public boolean isCompleted() {
			return (this == SUCCEEDED || this == FAILED || this == CANCELLED);
		}
	}
	
	/**
	 * Execution INSTANCE ID
	 * 
	 * @return
	 */
	public String getId();
	
	/**
	 * Name of flow execution
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * 
	 * @return
	 */
	public Date   getStartDate();
	
	/**
	 * 
	 * @return
	 */
	public Status getStatus();
	
	/**
	 * 
	 * @param type
	 * @return
	 */
	public <T> T getOutput(Class<T> type);
	
	/**
	 * Date which is completed
	 * 
	 * @return
	 */
	public Date	  getCompletionDate();
}
