package livefyre;

public class AppSingleton {
	private static AppSingleton appSingleton = new AppSingleton();

	private LivefyreApplication application;

	private AppSingleton() {
	}

	public static AppSingleton getInstance() {
		if(appSingleton == null)
			appSingleton = new AppSingleton();

		return appSingleton;
	}

	public LivefyreApplication getApplication() {
		return application;
	}

	public void setApplication(LivefyreApplication application) {
		this.application = application;
	}

}