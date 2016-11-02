package com.livefyre.streamhub_android_sdk.util;

public class LFSConstants {

	public static final String LFSPostBodyKey = "body";
	public static final String LFSPostTitleKey = "title";
	public static final String LFSPostRatingKey = "rating";
	public static final String LFSPostUserTokenKey = "lftoken";
	public static final String LFSPostParentIdKey = "parent_id";
	public static final String LFSPostMIMETypeKey = "mimetype";
	public static final String LFSPostShareTypesKey = "share_types";
	public static final String LFSPostAttachmentsKey = "attachments";
	public static final String LFSPostMediaKey = "media";
	public static final String LFSPostCollectionIdKey = "collection_id";
	public static final String LFSPostTypeReview = "review";
	public static final String LFSPostTypeComment = "comment";
	public static final String LFSPostTypeRating = "rating";
	public static final String LFSPostAttachment = "attachment";
	public static final String LFSPostTypeReply = "";
	public static final String LFSPostType = "type";

	public enum LFSPostType {

		REVIEW, RATING, REPLY,COMMENT;

		public String getValue() {
			switch (this) {
			case REVIEW:
				return "review";

			case RATING:
				return "rating";

			case REPLY:
				return "";
			case COMMENT:
				return "comment";
			default:
				return "";
			}
		}

	}

}