package javacloud.framework.flow.internal;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javacloud.framework.flow.spec.FlowDefinition;
import javacloud.framework.flow.spi.FlowClient;
import javacloud.framework.flow.spi.FlowExecution;
import javacloud.framework.flow.spi.FlowRegister;
import javacloud.framework.util.Pair;
/**
 * 
 * @author ho
 *
 */
public class LocalFlowClient extends LocalFlowExecutor implements FlowClient {
	private final Map<String, Pair<FlowRegister, FlowDefinition>> flowDefinitions = new HashMap<>();
	
	@Override
	public FlowRegister registerFlow(String flowName, FlowDefinition flowDefinition) {
		Pair<FlowRegister, FlowDefinition> pair = flowDefinitions.get(flowName);
		FlowRegister register = (pair != null? pair.getKey() : null);
		if(register == null) {
			register = new FlowRegister() {
				@Override
				public int getRevision() {
					return 0;
				}
				
				@Override
				public Date getCreationDate() {
					return null;
				}
				@Override
				public Date getUpdateDate() {
					return null;
				}
			};
			pair = new Pair<>(register, flowDefinition);
			flowDefinitions.put(flowName, pair);
		} else {
			pair.setValue(flowDefinition);
		}
		return register;
	}
	
	/**
	 * 
	 * @param flowName
	 * @param reason
	 * @return
	 */
	@Override
	public FlowRegister deleteFlow(String flowName, String reason) {
		Pair<FlowRegister, FlowDefinition> pair = flowDefinitions.get(flowName);
		if(pair != null) {
			flowDefinitions.remove(flowName);
			return pair.getKey();
		}
		return null;
	}
	
	/**
	 * 
	 * @param flowName
	 * @param input
	 * @param surrogateKey
	 * @return
	 */
	@Override
	public <T> FlowExecution startExecution(String flowName, T input, String surrogateKey) {
		return null;
	}
	
	/**
	 * 
	 * @param executionId
	 * @return
	 */
	@Override
	public FlowExecution getExecution(String executionId) {
		return null;
	}
	
	/**
	 * 
	 * @param executionId
	 * @param reason
	 * @return
	 */
	@Override
	public FlowExecution cancelExecution(String executionId, String reason) {
		return null;
	}
}
