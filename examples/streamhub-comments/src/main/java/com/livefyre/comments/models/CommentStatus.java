package com.livefyre.comments.models;

public enum CommentStatus {
    DELETED, NOT_DELETED;

    public int getValue() {
        switch (this) {
            case DELETED:
                return 0;

            case NOT_DELETED:
                return 1;

            default:
                return 1;
        }
    }

}