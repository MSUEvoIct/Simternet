package simternet.consumer;

public class NetManager {

	private static NetManager	singleton;

	public static NetManager getSingleton() {
		if (NetManager.singleton == null)
			NetManager.singleton = new NetManager();
		return NetManager.singleton;
	}

	public void manageNetworks(Consumer c) {
		return;
	}
}
