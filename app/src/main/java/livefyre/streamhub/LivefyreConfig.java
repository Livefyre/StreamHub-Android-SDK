package livefyre.streamhub;

public class LivefyreConfig {

    public static String scheme = "http";
    public static String environment = "livefyre.com";
    public static String bootstrapDomain = "bootstrap";
    public static String quillDomain = "quill";
    public static String adminDomain = "admin";
    public static String streamDomain = "stream1";
    private static String networkID = null;

    public static void setLivefyreNetworkID(String networkID) {
        LivefyreConfig.networkID = networkID;
    }

    public static String getConfiguredNetworkID() {
        if (networkID == null) {
            throw new AssertionError("You should set Livefyre Network key");
        }
        return networkID;
    }
}
