package com.livefyre.streamhub_android_sdk.util;

public enum LFSActions {

	EDIT, APPROVE, UNAPPROVE, HIDE, DELETE, BOZO, IGNORE_FLAGS, ADD_NOTE, LIKE, UNLIKE, FLAG, MENTION, SHARE, VOTE;

	public int value() {
		switch (this) {
		case EDIT:
			return 0;
		case APPROVE:
			return 1;
		case UNAPPROVE:
			return 2;
		case HIDE:
			return 3;
		case DELETE:
			return 4;
		case BOZO:
			return 5;
		case IGNORE_FLAGS:
			return 6;
		case ADD_NOTE:
			return 7;
		case LIKE:
			return 8;
		case UNLIKE:
			return 9;
		case FLAG:
			return 10;
		case MENTION:
			return 11;
		case SHARE:
			return 12;
		case VOTE:
			return 13;
		default:
			return 14;
		}
	}

}
