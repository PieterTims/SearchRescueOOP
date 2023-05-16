package allcode;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Graph {
	private HashMap<Tile, LinkedList<Tile>> unweightedAdjacencyMap;
	private HashMap<Tile, LinkedList<Pair>> weightedAdjacencyMap;

	// Constructor Method
	public Graph() {
		unweightedAdjacencyMap = new HashMap<Tile, LinkedList<Tile>>();
	}

	// Accessory method to add edge
	public void addEdge(Tile source, Tile destination) {

		// if source tile not in map
		if (!unweightedAdjacencyMap.containsKey(source)) {
			unweightedAdjacencyMap.put(source, null);
		}

		// if destination tile not in map
		if (!unweightedAdjacencyMap.containsKey(destination)) {
			unweightedAdjacencyMap.put(destination, null);
		}

		// call helper method twice
		addEdgeHelper(source, destination);
		addEdgeHelper(destination, source);
	}

	// Helper method to make sure no duplicate tile in linked list
	private void addEdgeHelper(Tile a, Tile b) {
		LinkedList<Tile> temp = unweightedAdjacencyMap.get(a);
		if (temp != null) {
			temp.remove(b);
		} else {
			temp = new LinkedList<Tile>();
		}
		temp.add(b);
		unweightedAdjacencyMap.put(a, temp);
	}

	// Helper method to create map with weighted edges
	private void makeWeightedMap(List<String> imgKeyNames) {
		Iterator<Tile> it = unweightedAdjacencyMap.keySet().iterator();
		HashMap<Tile, LinkedList<Pair>> tempMap = new HashMap<Tile, LinkedList<Pair>>();
		while (it.hasNext()) {
			Tile source = it.next();
			LinkedList<Tile> destinations = unweightedAdjacencyMap.get(source);
			LinkedList<Pair> weightedDestinations = new LinkedList<Pair>();
			for (int i = 0; i < destinations.size(); i++) {
				int index = imgKeyNames.indexOf(destinations.get(i).getName());
				if (index >= 0) {
					String tileName = imgKeyNames.get(index).replace(".png", "").toUpperCase();
					int cost = TileCost.valueOf(tileName).getValue();
					Pair pair = new Pair(destinations.get(i), cost);
					weightedDestinations.add(pair);
				}
			}
			tempMap.put(source, weightedDestinations);
		}
		weightedAdjacencyMap = tempMap;
	}

	// Method to erase edges connected to certain BufferedImage
	// only used to delete Tile with imgKeyName == "Empty.png"
	public void deleteEdgesWith(BufferedImage comparison) {
		Iterator<Tile> it = unweightedAdjacencyMap.keySet().iterator();
		while (it.hasNext()) {
			Tile tile = it.next();
			if (tile.equals(comparison)) {
				it.remove();
				continue;
			}
			for (int i = 0; i < unweightedAdjacencyMap.get(tile).size(); i++) {
				if (unweightedAdjacencyMap.get(tile).get(i).equals(comparison)) {
					unweightedAdjacencyMap.get(tile).remove(i);
				}
			}
		}
	}

	// Method to return true if adjacent tile BufferedImage == image
	public boolean hasImage(Tile tile, String imgKeyName) {
		if (unweightedAdjacencyMap.containsKey(tile) && unweightedAdjacencyMap.get(tile) != null) {
			for (int i = 0; i < unweightedAdjacencyMap.get(tile).size(); i++) {
				if (unweightedAdjacencyMap.get(tile).get(i).equals(imgKeyName)) {
					return true;
				}
			}
		}
		return false;
	}

	// Method to resets all tile.visited
	public void resetVisit() {
		for (Tile tile : unweightedAdjacencyMap.keySet()) {
			tile.setVisit(false);
		}
	}

	// Method to resets all tile.prev
	public void resetPrev() {
		for (Tile tile : unweightedAdjacencyMap.keySet()) {
			tile.setPrev(null);
		}
	}

	// Method to add blockade to specific tiles
	public void addVisit(BufferedImage wall) {
		for (Tile tile : unweightedAdjacencyMap.keySet()) {
			if (tile.equals(wall)) {
				tile.setVisit(true);
//				System.out.println("Blocked tile: " + tile.getX() + " " + tile.getY());
			}
		}
	}

	// Method to add multiple types of blockade
	public void addVisit(Collection<BufferedImage> walls) {
		for (BufferedImage wall : walls) {
			addVisit(wall);
		}
	}

	// Method to return unweighted map
	public HashMap<Tile, LinkedList<Tile>> getUnweightedMap() {
		return unweightedAdjacencyMap;
	}

	// Method to return weighted map
	public HashMap<Tile, LinkedList<Pair>> getWeightedMap(List<String> imgKeyNames) {
		// make weighted map if not yet made
		if (weightedAdjacencyMap == null) {
			makeWeightedMap(imgKeyNames);
		}
		return weightedAdjacencyMap;
	}
}
