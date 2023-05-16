package allcode;

//Class to Pair Tile with Cost for Weighted Map
public class Pair {
	private Tile tile;
	private int cost;

	// Constructor
	public Pair(Tile tile, int cost) {
		this.tile = tile;
		this.cost = cost;
	}

	// Method to get Tile
	public Tile getTile() {
		return tile;
	}

	// Method to get Cost
	public int getCost() {
		return cost;
	}
}
