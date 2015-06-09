package livefyre.streamhub;

public class LivefyreConfig {
	// comments


	public static String scheme = "http";
	public static String environment = "livefyre.com";
	public static String bootstrapDomain = "bootstrap";
	public static String quillDomain = "quill";
	public static String adminDomain = "admin";
	public static String streamDomain = "stream1";
//	public static String networkId ="labs.fyre.co";
	private static String networkID =null;


	public static void setLivefyreNetworkID(String networkID){
		LivefyreConfig.networkID=networkID;
	}

	public static String getConfiguredNetworkID(){
		if(networkID==null){
			throw new AssertionError("You should set Livefyre Network key");
		}
		return networkID;
	}
//	public static String getNetworkId() {
//		if(networkID.equals("NETWORK_ID")){
//			assert true:"You should set Livefyre Network key";
//		}
//		return networkID;
//	}

//	public static String getHostname(String networkId)
//	{
//	if (networkId.equals("livefyre.com")) {
//	return Config.environment;
//	}
//	return networkId;
//	}

	// reviews
//	public static String scheme = "http";
//	public static String environment = "livefyre.com";
//	public static String bootstrapDomain = "bootstrap";
//	public static String quillDomain = "quill";
//	public static String adminDomain = "admin";
//	public static String streamDomain = "stream1";
//	public static String networkId = "client-solutions.fyre.co";
//
//	public static String getHostname(String networkId) {
//		if (networkId.equals("livefyre.com")) {
//			return Config.environment;
//		}
//		return networkId;
//	}

}
