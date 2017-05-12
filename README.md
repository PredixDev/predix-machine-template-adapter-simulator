<a href="http://predixdev.github.io/predix-machine-template-adapter-simulator/javadocs/index.html" target="_blank" >
	<img height="50px" width="100px" src="images/javadoc.png" alt="view javadoc"></a>
&nbsp;
<a href="http://predixdev.github.io/predix-machine-template-adapter-simulator" target="_blank">
	<img height="50px" width="100px" src="images/pages.jpg" alt="view github pages">
</a>

# Machine Data Simulator

This repository contains a sample bundle that shows how to create sample data for the Reciprocating compressor. 
The simulator creates random data points between the threshold values as defined in com.ge.predix.workshop.nodeconfig.json. 

The threshold values and node names can be changed in com.ge.predix.workshop.nodeconfig.json file to simulate data for any other asset.

There are two rest endpoints defined in SampleMachineAdapter, 
	getconfig - lists current configuration in JSON format
	saveconfig - accpets a JSON string similar to content in com.ge.predix.workshop.nodeconfig.json to change the config paramters

[![Analytics](https://ga-beacon.appspot.com/UA-82773213-1/predix-machine-template-adapter-simulator/readme?pixel)](https://github.com/PredixDev)
