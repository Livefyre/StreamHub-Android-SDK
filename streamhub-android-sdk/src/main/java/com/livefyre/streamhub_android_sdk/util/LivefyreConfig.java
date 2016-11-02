package com.livefyre.streamhub_android_sdk.util;

public class LivefyreConfig {
    // comments
    public static String scheme = "https";
    public static String environment = "livefyre.com";
    public static String bootstrapDomain = "bootstrap";
    public static String quillDomain = "quill";
    public static String adminDomain = "admin";
    public static String streamDomain = "stream1";
    public static String identityDomain = "identity";
    public static String origin = "https://livefyre-cdn-dev.s3.amazonaws.com";
    public static String referer = "https://livefyre-cdn-dev.s3.amazonaws.com/demos/lfep2-comments.html";
    public static String networkId = "labs.fyre.co";
//    private static String networkId = null;

    public static void setLivefyreNetworkID(String networkID) {
        LivefyreConfig.networkId = networkID;
    }

    public static String getConfiguredNetworkID() {
        if (networkId == null) {
            throw new AssertionError("You should set Livefyre Network key");
        }
        return networkId;
    }
}
