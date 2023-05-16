package allcode;

import java.awt.image.BufferedImage;

public class Tile implements Comparable<Tile> {
	private BufferedImage img;
	private String imgKeyName;
	private boolean hasLost;

	// variables for algorithm implementation
	private boolean visited;
	private Tile prev = null;
	private int minDistance = Integer.MAX_VALUE;

	// Constructor Method
	public Tile(BufferedImage img) {
		this.img = img;
		visited = hasLost = false;
		imgKeyName = "Empty.png";
	}

	// Method to set tile's image and image name
	public boolean setImage(BufferedImage img, String imgKeyName) {
		try {
			this.img = img;
			this.imgKeyName = imgKeyName;
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	// Method to change only tile's image
	public boolean setImage(BufferedImage img) {
		try {
			this.img = img;
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	// Method to get tile's image
	public BufferedImage getImage() {
		return img;
	}

	// Method to get tile's image name
	public String getName() {
		return imgKeyName;
	}

	// Method to set whether lost person is here
	public void setLost(boolean hasLost) {
		this.hasLost = hasLost;
	}

	// Method to get whether lost person is here
	public boolean getLost() {
		return hasLost;
	}

	// Method to set whether tile is visited
	public void setVisit(boolean visited) {
		this.visited = visited;
	}

	// Method to get whether tile is visited
	public boolean getVisit() {
		return visited;
	}

	// Method to set tile's reference to previous tile
	public void setPrev(Tile prev) {
		this.prev = prev;
	}

	// Method to get tile's reference to previous tile
	public Tile getPrev() {
		return prev;
	}

	// Method to set tile's min distance to nearest tile with prev = null
	public void setMinDistance(int minDistance) {
		this.minDistance = minDistance;
	}

	// Method to get tile's min distance to nearest tile with prev = null
	public int getMinDistance() {
		return minDistance;
	}

	// overload equals function (compare with img)
	public boolean equals(BufferedImage other) {
		if (img.equals(other)) {
			return true;
		} else {
			return false;
		}
	}

	// second overload equals function (compare with imgKeyName)
	public boolean equals(String other) {
		if (imgKeyName.equals(other)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	// Override implementation of Comparable
	public int compareTo(Tile other) {
		return Integer.compare(minDistance, other.getMinDistance());
	}
}
