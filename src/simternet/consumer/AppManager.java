package simternet.consumer;

public class AppManager {

	private static AppManager	singleton;

	public static AppManager getSingleton() {
		if (AppManager.singleton == null)
			AppManager.singleton = new AppManager();
		return AppManager.singleton;
	}

	public void manageApplications(Consumer c) {
		return;
	}
}
