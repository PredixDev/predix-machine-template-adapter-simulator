/*
 * Copyright (c) 2014 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.simulator.types;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import com.ge.dspmicro.machinegateway.types.PDataNode;
import com.ge.predix.solsvc.simulator.config.JsonDataNode;

/**
 * 
 * 
 * @author Predix Machine Sample
 */
public class SampleDataNode extends PDataNode
{

		
    private JsonDataNode node;
    private String expression;
	/**
	 * @param machineAdapterId -
	 * @param node -
	 */
	public SampleDataNode(UUID machineAdapterId, JsonDataNode node) {
		super(machineAdapterId, node.getNodeName());
		this.setNode(node);
		this.setExpression(node.getExpression());
	}

    /**
     * Node address to uniquely identify the node.
     */
    @Override
    public URI getAddress()
    {
        try
        {
            URI address = new URI("sample.subscription.adapter", null, "localhost", -1, "/" + getName(), null, null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            return address;
        }
        catch (URISyntaxException e)
        {
            return null;
        }
    }

	/**
	 * @return -
	 */
	public JsonDataNode getNode() {
		return this.node;
	}

	/**
	 * @param node -
	 */
	public void setNode(JsonDataNode node) {
		this.node = node;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}
}
