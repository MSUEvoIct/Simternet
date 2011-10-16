package simternet.agents.consumer;

import simternet.engine.TraceConfig;

public class NetManager {

	private static NetManager	singleton;

	public static NetManager getSingleton() {
		if (NetManager.singleton == null) {
			NetManager.singleton = new NetManager();
		}
		return NetManager.singleton;
	}

	public void manageNetworks(Consumer c) {
		if (TraceConfig.kitchenSink) {
			TraceConfig.out.println("Managing net consumption decision of " + c);
		}
		return;
	}
}
