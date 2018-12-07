package javacloud.framework.flow.spi;

import java.util.Date;

import javacloud.framework.util.Objects;

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
	 * return default output without conversion
	 * 
	 * @return
	 */
	default public <R> R getOutput() {
		return Objects.cast(getOutput(Object.class));
	}
	
	/**
	 * return output of TYPE conversion
	 * 
	 * @param type
	 * @return
	 */
	public <R> R getOutput(Class<R> type);
	
	/**
	 * Date which is completed, null if haven't yet
	 * 
	 * @return
	 */
	public Date	  getCompletionDate();
}
