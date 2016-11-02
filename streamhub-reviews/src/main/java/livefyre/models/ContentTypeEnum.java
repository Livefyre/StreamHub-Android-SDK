package livefyre.models;

public enum ContentTypeEnum {
	PARENT, CHILD, DELETED;

	public int getValue() {
		switch (this) {
		case PARENT:
			return 0;
		case CHILD:
			return 1;
		case DELETED:
			return -1;
		default:
			return 1;
		}
	}
}
