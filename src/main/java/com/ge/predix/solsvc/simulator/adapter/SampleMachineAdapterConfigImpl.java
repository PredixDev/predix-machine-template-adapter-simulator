package com.ge.predix.solsvc.simulator.adapter;

import org.osgi.service.component.ComponentContext;

import com.ge.predix.solsvc.simulator.api.ISampleAdapterConfig;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.ConfigurationPolicy;
import aQute.bnd.annotation.metatype.Configurable;
import aQute.bnd.annotation.metatype.Meta;
import aQute.configurable.Config;

@Component(immediate=true,name=SampleMachineAdapterConfigImpl.SERVICE_PID,
	designateFactory=Config.class,
	configurationPolicy=ConfigurationPolicy.require,
	provide=ISampleAdapterConfig.class
)
public class SampleMachineAdapterConfigImpl implements ISampleAdapterConfig {
	
	private int updateInterval;
	private String nodeConfigFile;
	private String adapterName;
	private String adapterDescription;
	private String[] dataSubscriptions;
	
	/** Service PID for Sample Machine Adapter */
	public static final String SERVICE_PID = "com.ge.predix.solsvc.simulator.config"; //$NON-NLS-1$

	
	public void activate(ComponentContext ctx) {
		Config config = Configurable.createConfigurable(Config.class, ctx.getProperties());
		setAdapterDescription(config.adapterDescription());
		setAdapterName(config.adapterName());
		setDataSubscriptions(config.dataSubscriptions());
		setNodeConfigFile(config.nodeConfigFile());
		setUpdateInterval(Integer.parseInt(config.updateInterval()));
	}
	
	@Override
	public int getUpdateInterval() {
		return this.updateInterval;
	}
	public void setUpdateInterval(int updateInterval) {
		this.updateInterval = updateInterval;
	}
	
	@Override
	public String getNodeConfigFile() {
		return nodeConfigFile;
	}
	public void setNodeConfigFile(String nodeConfigFile) {
		this.nodeConfigFile = nodeConfigFile;
	}
	@Override
	public String getAdapterName() {
		return this.adapterName;
	}
	public void setAdapterName(String adapterName) {
		this.adapterName = adapterName;
	}
	
	@Override
	public String getAdapterDescription() {
		return this.adapterDescription;
	}
	public void setAdapterDescription(String adapterDescription) {
		this.adapterDescription = adapterDescription;
	}
	
	@Override
	public String[] getDataSubscriptions() {
		return this.dataSubscriptions;
	}
	public void setDataSubscriptions(String[] dataSubscriptions) {
		this.dataSubscriptions = dataSubscriptions;
	}

	// Meta mapping for configuration properties
	@Meta.OCD(name="%component.name", factory=true, localization="OSGI-INF/l10n/bundle")
	interface Config {
		@Meta.AD(name = "%updateInterval.name", description = "%updateInterval.description", id = PROPKEY_UPDATE_INTERVAL, required = false, deflt = "")
		String updateInterval();

		@Meta.AD(name = "%nodeConfigFile.name", description = "%nodeConfigFile.description", id = PROPKEY_NODES_CONFIG_FILE, required = false, deflt = "")
		String nodeConfigFile();

		@Meta.AD(name = "%adapterName.name", description = "%adapterName.description", id = PROPKEY_ADAPTER_NAME, required = false, deflt = "")
		String adapterName();

		@Meta.AD(name = "%adapterDescription.name", description = "%adapterDescription.description", id = PROPKEY_ADAPTER_DESCRIPTION, required = false, deflt = "")
		String adapterDescription();

		@Meta.AD(id = PROPKEY_DATA_SUBSCRIPTONS, name = "%dataSubscriptions.name", description = "%dataSubscriptions.description", required = true, deflt = "")
		String[] dataSubscriptions();
	}
}
