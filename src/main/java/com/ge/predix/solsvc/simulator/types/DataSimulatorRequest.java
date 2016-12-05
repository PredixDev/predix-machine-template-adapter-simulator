package com.ge.predix.solsvc.simulator.types;

/**
 * 
 * @author 212546387 -
 */
public class DataSimulatorRequest {
	/**
	 * 
	 */
	String simulatorRequest;
	/**
	 * 
	 */
	String simulatorRequestType = Constants.SIMULATOR_REQUEST_TYPE_JSON;
	/**
	 * @return -
	 */
	public String getSimulatorRequest() {
		return this.simulatorRequest;
	}
	/**
	 * @param simulatorRequest -
	 */
	public void setSimulatorRequest(String simulatorRequest) {
		this.simulatorRequest = simulatorRequest;
	}
	/**
	 * @return -
	 */
	public String getSimulatorRequestType() {
		return this.simulatorRequestType;
	}
	/**
	 * @param simulatorRequestType -
	 */
	public void setSimulatorRequestType(String simulatorRequestType) {
		this.simulatorRequestType = simulatorRequestType;
	}
	
}
