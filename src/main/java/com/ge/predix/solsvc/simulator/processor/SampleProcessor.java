/*
 * Copyright (c) 2016 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */
 
package com.ge.predix.solsvc.simulator.processor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ge.dspmicro.hoover.api.processor.IProcessor;
import com.ge.dspmicro.hoover.api.processor.ProcessorException;
import com.ge.dspmicro.hoover.api.spillway.ITransferData;
import com.ge.dspmicro.machinegateway.api.IMachineGateway;
import com.ge.dspmicro.machinegateway.api.adapter.IMachineAdapter;
import com.ge.dspmicro.machinegateway.types.ITransferable;
import com.ge.dspmicro.machinegateway.types.PDataNode;
import com.ge.dspmicro.machinegateway.types.PDataValue;
import com.ge.predix.solsvc.simulator.api.ISampleAdapterConfig;
import com.ge.predix.solsvc.simulator.types.SampleDataNode;
import com.ge.predixmachine.datamodel.datacomm.EdgeData;
import com.ge.predixmachine.datamodel.datacomm.EdgeDataList;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;
import parsii.eval.Expression;
import parsii.eval.Parser;
import parsii.tokenizer.ParseException;


/**
 * This class provides a Processor implementation which will process the data as per configuration on the spillway.
 */
@Component(immediate=true,name = SampleProcessor.SERVICE_PID, provide =
{
    IProcessor.class
})
public class SampleProcessor
        implements IProcessor
{

    /** Create logger to report errors, warning massages, and info messages (runtime Statistics) */
    protected static Logger                         _logger          = LoggerFactory.getLogger(SampleProcessor.class);
    
    /** Service PID for Sample Machine Adapter */
	public static final String SERVICE_PID = "com.ge.predix.solsvc.simulator.processor"; //$NON-NLS-1$
	
	private ISampleAdapterConfig config;
    
 	private IMachineGateway gateway;
 	
    /**
     * @param ctx context of the bundle.
     */
    @Activate
    public void activate(ComponentContext ctx)
    {
    	
    	_logger.info("Adapter : "+this.config.getAdapterName());
    	_logger.info("Spillway service activated."); //$NON-NLS-1$
    	_logger.info("Update Interval : "+this.config.getUpdateInterval());
    }
    

    /**
     * @param ctx context of the bundle.
     */
    @Deactivate
    public void deactivate(ComponentContext ctx)
    {
        
        if ( _logger.isDebugEnabled() )
        {
            _logger.debug("Spillway service deactivated."); //$NON-NLS-1$
        }
    }
 
    @Override
    public void processValues(String processType, List<ITransferable> values, ITransferData transferData)
            throws ProcessorException
    {
    	processValues(processType, new HashMap<String, String>(), values, transferData);
    }


	@SuppressWarnings("deprecation")
	@Override
	public void processValues(String processType, Map<String, String> map, List<ITransferable> values, ITransferData transferData)
			throws ProcessorException {
		_logger.info("VALUES :" +values.toString()); //$NON-NLS-1$
		for (ITransferable t: values) {
			if (t instanceof PDataValue) {
				PDataValue value = (PDataValue)t;
				//_logger.info("Node Name : "+value.getNodeName());
				SampleDataNode node = (SampleDataNode)getNode(value.getNodeName());
				String expr = node.getExpression();
    			Double result = eval(expr.replaceAll("#NODE_VALUE#", value.getValue().getValue().toString())); //$NON-NLS-1$
    			if (result == 1.0) {
    				//Insert alarm
    			}
			}
		}
    	transferData.transferData(values);
	}


	@Override
	public void processValues(String processType, Map<String, String> map, EdgeDataList values, ITransferData transferData)
			throws ProcessorException {
		for (EdgeData edgeData : values.getEdgeDataList()) {
			_logger.info("EdgeDataNodeName : "+edgeData.getNodeName());
		}
		transferData.transferData(values, map);
	}
	
	/**
	 * @param expression -
	 * @return -
	 */
	public double eval(String expression) {
		Expression expr;
		try {
			expr = Parser.parse(expression);
			return expr.evaluate();
		} catch (ParseException e) {
			throw new RuntimeException("Exception when evaluating expression",e); //$NON-NLS-1$
		}
	}

	private PDataNode getNode(String nodeName) {
		for (IMachineAdapter adapter:gateway.getMachineAdapters()) {
    		if (adapter.getInfo() != null 
    				&& adapter.getInfo().getName().equals(this.config.getAdapterName())) {
    			for (PDataNode node:adapter.getNodes()) {
    				if (node.getName().equals(nodeName)) {
    					return node;
    				}
    			}
    		}
    	}
		return null;
	}
	@Reference
	public void setGateway(IMachineGateway gateway) {
		this.gateway = gateway;
	}


	public ISampleAdapterConfig getConfig() {
		return config;
	}

	@Reference
	public void setConfig(ISampleAdapterConfig config) {
		this.config = config;
	}
   
}
