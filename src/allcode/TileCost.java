package allcode;

//'#','R','B','W','S'
public enum TileCost {
	EMPTY(-1), ROAD(2), BUILDING(3), WAREHOUSE(4), SARBASE(0);

	private int value;

	private TileCost(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}
}
