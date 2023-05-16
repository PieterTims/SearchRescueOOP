package allcode;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BFS {
	private Graph graph;
	private HashMap<Tile, LinkedList<Tile>> adjacencyMap;
	private List<BufferedImage> walls;

	// Constructor Method
	public BFS(Graph graph, HashMap<Tile, LinkedList<Tile>> adjacencyMap, List<BufferedImage> walls) {
		this.graph = graph;
		this.adjacencyMap = adjacencyMap;
		this.walls = walls;
	}

	// Accessory method for BFS
	public List<Tile> breadthFirstSearch(Tile start, BufferedImage destination) {
		graph.addVisit(walls);
		List<Tile> result = breadthFirstSearchHelper(start, destination);
		graph.resetVisit();
		graph.resetPrev();
		return result;
	}

	// Helper method for BFS
	public List<Tile> breadthFirstSearchHelper(Tile start, BufferedImage destination) {

		// make new queue and add head/start
		Queue<Tile> queue = new LinkedList<>();
		start.setVisit(true);
		queue.add(start);
		Tile end = null;
		// loops till queue is empty; destination not reached
		while (!queue.isEmpty()) {
			Tile current = queue.poll();
			LinkedList<Tile> allNeighbors = adjacencyMap.get(current);
			// if there is any adjacent
			if (allNeighbors != null) {
				for (Tile neighbor : allNeighbors) {
					// visit tile and add to queue
					if (!neighbor.getVisit()) {
						neighbor.setVisit(true);
						queue.add(neighbor);

						// add backtrack to current tile
						neighbor.setPrev(current);

						// if destination found, stop search
						if (neighbor.equals(destination)) {
							queue.clear();
							end = neighbor;
							break;
						}
					}
				}
			}
		}
		List<Tile> result = traceRoute(start, end);
		return result;
	}

	// Method returns traced route from start tile to end tile
	public List<Tile> traceRoute(Tile start, Tile end) {
		List<Tile> route = new ArrayList<>();
		Tile tile = end;
		while (tile != null) {
			route.add(tile);
			tile = tile.getPrev();
		}
		Collections.reverse(route);

		// remove head and tail if route exist
		if (route.size() > 2) {
			route.remove(0);
			route.remove(route.size() - 1);
		}
		return route;
	}

}
