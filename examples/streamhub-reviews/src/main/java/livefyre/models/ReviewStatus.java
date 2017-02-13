package livefyre.models;

public enum ReviewStatus {
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
