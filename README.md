# VNFManager Dummy

The Dummy VNFManager imitates the behaviour of a VNFManager. 

Instead of realling executing the tasks given by the Orchestrator it just replies as if the request was executed successfully. 

Or it returns always the same dummy data. 

For example the instantiate function of the generic VNFManager would safe scripts to the EMS and request to execute them, but the dummy VNFManager does not do that and returns the given VirtualNetworkFunctionRecord after some time. 

In this way the communication from the NFVO to the VNFManager can be tested without deploying a real network service. 

The endpoint to use in the NSDs that should be "deployed" by the dummy VNFManager is "dummy".

