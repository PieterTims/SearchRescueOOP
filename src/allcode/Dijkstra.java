package allcode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

public class Dijkstra {
	private Graph graph;
	private HashMap<Tile, LinkedList<Pair>> adjacencyMap;

	// Constructor Method
	public Dijkstra(Graph graph, HashMap<Tile, LinkedList<Pair>> adjacencyMap) {
		this.graph = graph;
		this.adjacencyMap = adjacencyMap;
	}

	// Actual Method to do Dijkstra Search Algorithm
	public void DijkstraSearch(Tile start) {
		graph.resetVisit();
		// make new priority queue and add head/start
		PriorityQueue<Tile> pQueue = new PriorityQueue<>();
		start.setMinDistance(0);
		start.setPrev(null);
		pQueue.add(start);
		// build a Dijkstra graph
		while (!pQueue.isEmpty()) {
			Tile current = pQueue.poll();
			current.setVisit(true);
			LinkedList<Pair> allEdges = adjacencyMap.get(current);
			// if there is any adjacent pairs
			if (allEdges != null) {
				for (Pair edge : allEdges) {
					if (!edge.getTile().getVisit()) {
						int weight = edge.getCost();
						int minDistance = current.getMinDistance() + weight;

						if (minDistance < edge.getTile().getMinDistance()) {
							edge.getTile().setPrev(current);
							edge.getTile().setMinDistance(minDistance);
							pQueue.add(edge.getTile());
						}
					}
				}
			}
		}
	}

	// Method returns traced route from source tile to target tile
	// if reverse = true, from target tile to source tile
	public List<Tile> traceRoute(Tile target, boolean reverse) {
		List<Tile> route = new ArrayList<>();

		for (Tile tile = target; tile != null; tile = tile.getPrev()) {
			route.add(tile);
		}

		if (route.size() > 2 && !reverse) {
			Collections.reverse(route);
			route.remove(0);
			route.remove(route.size() - 1);
		}

		return route;
	}
}
