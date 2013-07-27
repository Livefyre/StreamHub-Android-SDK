package com.livefyre.streamhub_android_sdk;

public class Constants {
	public static String scheme = "http://";
	public static String bootstrapDomain = "bootstrap";
	public static String quillDomain = "quill";
	public static String adminDomain = "admin";
	public static String streamDomain = "stream1";

	/**  Flagging actions */
	public enum FlagType {
		/** Offensive */
		OFFENSIVE,
		/** Spam */
		SPAM,
		/** Disagree */
		DISAGREE,
		/** Off topic */
		OFF_TOPIC
	}
}
