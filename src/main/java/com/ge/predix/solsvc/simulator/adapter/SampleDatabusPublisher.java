/*
 * Copyright (c) 2015 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.simulator.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.ConfigurationPolicy;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;
import aQute.bnd.annotation.metatype.Configurable;
import aQute.bnd.annotation.metatype.Meta;

import com.ge.dspmicro.databus.api.common.DatabusException;
import com.ge.dspmicro.databus.api.data.IDataComm;
import com.ge.dspmicro.databus.api.data.IDataListener;
import com.ge.dspmicro.databus.api.data.IEdgeDataComm;
import com.ge.dspmicro.databus.api.data.IEdgeDataListener;
import com.ge.dspmicro.machinegateway.types.PDataValue;
import com.ge.dspmicro.machinegateway.types.PEnvelope;
import com.ge.dspmicro.machinegateway.types.PQuality.QualityEnum;
import com.ge.predixmachine.datamodel.datacomm.Data;
import com.ge.predixmachine.datamodel.datacomm.DataQuality;
import com.ge.predixmachine.datamodel.datacomm.EdgeData;
import com.ge.predixmachine.datamodel.datacomm.EdgeDataList;
import com.google.protobuf.ByteString;

/**
 * 
 * @author Predix Machine Sample
 */

// @Component is use to register this component as Service in the container.
@SuppressWarnings("nls")
@Component(name = SampleDatabusPublisher.SERVICE_PID, provide = {}, designate = SampleDatabusPublisher.Configuration.class, configurationPolicy = ConfigurationPolicy.optional)
public class SampleDatabusPublisher
        implements IDataListener, IEdgeDataListener
{
    // Meta mapping for configuration properties
    @SuppressWarnings("javadoc")
    @Meta.OCD(name = "%component.name", localization = "OSGI-INF/l10n/bundle")
    interface Configuration
    {
        /** [Optional] The topic tag to use for publishing and subscribing to messages. */
        @Meta.AD(name = "%topicTag.name", description = "%topicTag.description", id = PROPKEY_TOPIC_TAG, required = true, deflt = "sampleTopic")
        String topicTag();
    }

    /** OSGi service PID for this sample. */
    protected static final String SERVICE_PID               = "com.ge.dspmicro.sample.databus";

    /** Property key for the topic tag in com.ge.dspmicro.sample.databus.config file. */
    protected static final String PROPKEY_TOPIC_TAG         = SERVICE_PID + ".topic.tag";                        //$NON-NLS-1$

    /** Create logger to report errors, warning massages, and info messages (runtime Statistics) */
    private static Logger         _logger                   = LoggerFactory.getLogger(SampleDatabusPublisher.class);

    /** EdgeData communication handler. */
    private IEdgeDataComm         edgeDataComm;

    /** PDataValue communication handler. */
    private IDataComm             dataComm;

    private boolean               isSubscribedToEdgeData = false;
    private boolean               isSubscribedToPData       = false;

    /**
     * The activate method is called when bundle is started.
     * 
     * @param ctx Component Context.
     */
    @Activate
    public void activate(ComponentContext ctx)
    {
        // Use the logger service for debugging
        if ( _logger.isDebugEnabled() )
        {
            _logger.debug("Starting sample " + ctx.getBundleContext().getBundle().getSymbolicName());
        }

        // Load configurations from com.ge.dspmicro.sample.databus.config file
        Configuration config = Configurable.createConfigurable(Configuration.class, ctx.getProperties());
        String topicTag = config.topicTag();

        try
        {
            pubSubWithIEdgeDataComm(topicTag);
            pubSubWithIDataComm(topicTag);
        }
        catch (DatabusException e)
        {
            _logger.error("Error occurred in sample", e);
        }
    }

    private void pubSubWithIEdgeDataComm(String topicTag)
    {
        // Subscribe to topic
        this.edgeDataComm.subscribeData(new String[]
        {
                topicTag
        }, this);
        _logger.debug("Subscribed to topic " + topicTag);
        this.isSubscribedToEdgeData = true;

        // Create some data
        final Data stringData = Data.newBuilder().setStringData("Some String Data").build();
        final Data binaryData = Data.newBuilder().setBinaryData(ByteString.copyFrom("Some Binary Data".getBytes()))
                .build();
        final Data intData = Data.newBuilder().setIntData(100).build();
        final Data doubleData = Data.newBuilder().setDoubleData(0.000000123).build();
        final Data booleanData = Data.newBuilder().setBooleanData(true).build();

        EdgeData.Builder dataBuilder = EdgeData.newBuilder();
        EdgeData goodData1 = dataBuilder.setNodeName("edgeDataNode").setNodeId(UUID.randomUUID().toString())
                .addBody(stringData).addBody(binaryData).setQuality(DataQuality.GOOD).build();
        EdgeData goodData2 = dataBuilder.setNodeName("edgeDataNode").setNodeId(UUID.randomUUID().toString())
                .addBody(intData).addBody(doubleData).addBody(booleanData).setQuality(DataQuality.GOOD).build();
        EdgeDataList goodDataList = EdgeDataList.newBuilder().addEdgeData(goodData1)
                .addEdgeData(goodData2).build();

        // Publish data
        this.edgeDataComm.publishData(topicTag, goodDataList);
        _logger.debug("Published data to topic " + topicTag + ": " + goodDataList);

        // Allow time for message to arrive
        try
        {
            Thread.sleep(1000); // sleep for 1 second
        }
        catch (InterruptedException e)
        {
            // ignore
        }

        // Unsubscribe to topic
        this.edgeDataComm.unsubscribeData(this);
        this.isSubscribedToEdgeData = false;

        // Publish again and callback should not be invoked
        EdgeData badData1 = dataBuilder.setNodeName("edgeDataNode").setNodeId(UUID.randomUUID().toString())
                .addBody(stringData).addBody(binaryData).setQuality(DataQuality.BAD).build();
        EdgeData badData2 = dataBuilder.setNodeName("edgeDataNode").setNodeId(UUID.randomUUID().toString())
                .addBody(intData).addBody(doubleData).addBody(booleanData).setQuality(DataQuality.BAD).build();
        EdgeDataList badDataList = EdgeDataList.newBuilder().addEdgeData(badData1)
                .addEdgeData(badData2).build();
        this.edgeDataComm.publishData(topicTag, badDataList);
        _logger.debug("Published data to topic " + topicTag + ": " + badDataList);

        // Allow time for message to arrive
        try
        {
            Thread.sleep(1000); // sleep for 1 second
        }
        catch (InterruptedException e)
        {
            // ignore
        }
    }

    private void pubSubWithIDataComm(String topicTag)
    {
        // Subscribe to topic
        this.dataComm.subscribeData(new String[]
        {
                topicTag
        }, this);
        _logger.debug("Subscribed to topic " + topicTag);
        this.isSubscribedToPData = true;

        // Publish some data
        List<PDataValue> goodData = new ArrayList<PDataValue>();
        goodData.add(new PDataValue("pdataNode", UUID.randomUUID(), new PEnvelope("Good Data 1"), QualityEnum.GOOD));
        goodData.add(new PDataValue("pdataNode", UUID.randomUUID(), new PEnvelope("Good Data 2"), QualityEnum.GOOD));
        this.dataComm.publishData(topicTag, goodData);
        _logger.debug("Published data to topic " + topicTag + ": " + goodData.toString());

        // Allow time for message to arrive
        try
        {
            Thread.sleep(1000); // sleep for 1 second
        }
        catch (InterruptedException e)
        {
            // ignore
        }

        // Unsubscribe to topic
        this.dataComm.unsubscribeData(this);
        this.isSubscribedToPData = false;

        // Publish again and callback should not be invoked
        List<PDataValue> badData = new ArrayList<PDataValue>();
        badData.add(new PDataValue("pdataNode", UUID.randomUUID(), new PEnvelope("Bad Data 1"), QualityEnum.BAD));
        badData.add(new PDataValue("pdataNode", UUID.randomUUID(), new PEnvelope("Bad Data 2"), QualityEnum.BAD));
        this.dataComm.publishData(topicTag, badData);
        _logger.debug("Published data to topic " + topicTag + ": " + badData.toString());

        // Allow time for message to arrive
        try
        {
            Thread.sleep(1000); // sleep for 1 second
        }
        catch (InterruptedException e)
        {
            // ignore
        }
    }

    /**
     * This method is called when the bundle is stopped.
     * 
     * @param ctx Component Context
     */
    @Deactivate
    public void deactivate(ComponentContext ctx)
    {
        // Put your clean up code here when container is shutting down
        if ( this.dataComm != null )
        {
            this.dataComm = null;
        }

        if ( _logger.isDebugEnabled() )
        {
            _logger.debug("Stopped sample " + ctx.getBundleContext().getBundle().getSymbolicName()); //$NON-NLS-1$
        }
    }

    /*
     * (non-Javadoc)
     * @see com.ge.dspmicro.databus.api.data.IEdgeDataListener#dataReceived(java.lang.String, com.ge.predixmachine.datamodel.EdgeDataList)
     */
    @Override
    public void dataReceived(String topicTag, EdgeDataList data)
    {
        if ( this.isSubscribedToEdgeData )
        {
            _logger.info("EdgeData received on \"" + topicTag + "\" topic: " + data);
        }
        else
        {
            _logger.error("Unexpected EdgeData received on \"" + topicTag + "\" topic: " + data);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.ge.dspmicro.databus.api.data.IDataListener#dataReceived(java.lang.String, com.ge.dspmicro.machinegateway.types.PDataValue)
     */
    @Override
    public void dataReceived(String topicTag, List<PDataValue> data)
    {
        if ( this.isSubscribedToPData )
        {
            _logger.info("PDataValue received on \"" + topicTag + "\" topic: " + data);
        }
        else
        {
            _logger.error("Unexpected PDataValue received on \"" + topicTag + "\" topic: " + data);
        }
    }

    /**
     * Dependency injection of IDataComm.
     *
     * @param dataComm Communication handler for PDataValue messages.
     */
    @Reference
    public void setDataComm(IDataComm dataComm)
    {
        _logger.debug("setDataComm() called.");
        this.dataComm = dataComm;
    }

    /**
     * Clears dependency injection of IDataComm.
     *
     * @param dataComm Communication handler for PDataValue messages.
     */
    public void unsetDataComm(@SuppressWarnings("hiding") IDataComm dataComm)
    {
        _logger.debug("unsetDataComm() called.");
        this.dataComm = null;
    }

    /**
     * Dependency injection of IEdgeDataComm.
     *
     * @param edgeDataComm Communication handler for EdgeData messages.
     */
    @Reference
    public void setEdgeDataComm(IEdgeDataComm edgeDataComm)
    {
        _logger.debug("setEdgeDataComm() called.");
        this.edgeDataComm = edgeDataComm;
    }

    /**
     * Clears dependency injection of IEdgeDataComm.
     *
     * @param edgeDataComm Communication handler for EdgeData messages.
     */
    public void unsetEdgeDataComm(@SuppressWarnings("hiding") IEdgeDataComm edgeDataComm)
    {
        _logger.debug("unsetEdgeDataComm() called.");
        this.edgeDataComm = null;
    }
}
