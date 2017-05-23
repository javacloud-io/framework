package com.appe.framework.job.ext;


import java.util.Set;

import com.appe.framework.job.ExecutionState;
import com.appe.framework.util.Dictionary;
import com.appe.framework.util.Objects;
/**
 * To be able to address both parameters & attributes
 * 
 * @author ho
 *
 */
public final class JobParameters extends Dictionary implements ExecutionState.Attributes {
	public JobParameters() {
	}
	/**
	 * 
	 * @param parameters
	 */
	public JobParameters(Dictionary parameters) {
		super(parameters);
	}
	
	/**
	 * Initialize the attributes SET
	 * @param attributes
	 */
	public JobParameters(ExecutionState.Parameters parameters) {
		set(parameters);
	}
	
	/**
	 * TODO: return READONLY keys
	 */
	public Set<String> keys() {
		return keySet();
	}
	
	/**
	 * Override set of new VALUEs
	 * 
	 * @param parameters
	 * @return
	 */
	public JobParameters set(ExecutionState.Parameters parameters) {
		if(parameters != null) {
			for(String key: parameters.keys()) {
				set(key, parameters.get(key));
			}
		}
		return this;
	}
	
	/**
	 * 
	 * @param parameters
	 * @return
	 */
	public static JobParameters wrap(ExecutionState.Parameters parameters) {
		if(parameters instanceof JobParameters) {
			return (JobParameters)parameters;
		}
		return new JobParameters(parameters);
	}
	
	/**
	 * Build attributes & parameters from list of name/value
	 * 
	 * @param values
	 * @return
	 */
	public static JobParameters build(Object...values) {
		return new JobParameters(Objects.asDict(values));
	}
}
