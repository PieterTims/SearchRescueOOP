package allcode;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

public class MapPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;

	// initialized variables on constructor call
	private Random rand = new Random();
	private Map<String, List<BufferedImage>> imgs;
	private BufferedImage[] empty = new BufferedImage[2];
	private BufferedImage sarBase;

	// search algorithm implementations
	private List<Tile> buildingLocations;
	private List<Tile> lostLocations;
	private List<Tile> sarLocations;
	private Graph graph = new Graph();

	// map drawing implementations
	private Tile[][] allTiles;
	private String size = "small";
	private int numLost = 0;
	private int numSearch = 0;
	private boolean makeNew = false;
	private int lostVisible = 1; // 1 = invisible; 2 = visible

	// variables for showing path
	private Timer timer;
	private int numLoop, numFound;
	private boolean lostFound, afterOne;
	private final int DELAY = 350;
	private List<Tile> allPaths;

	// Constructor Method
	public MapPanel() {
		// load all images and resize to 50x50p
		// BufferedImage sorting purposes --
		// 0 = normal
		// 1 = has lost, invisible
		// 2 = has lost, visible
		// 3 = path tracing
		// Miscellaneous --
		// Empty0.png = initial empty (clear and program start)
		// Empty1.png = empty icon after map made
		// SARBase.png = SAR Base icon

		imgs = new HashMap<String, List<BufferedImage>>();
		imgs.put("Building.png", new ArrayList<BufferedImage>());
		imgs.put("Road.png", new ArrayList<BufferedImage>());
		imgs.put("Warehouse.png", new ArrayList<BufferedImage>());

		try {
			empty[0] = loadImage("Empty0.png", 50, 50);
			empty[1] = loadImage("Empty1.png", 50, 50);
			sarBase = loadImage("SARBase.png", 50, 50);

			imgs.get("Building.png").add(loadImage("Building0.png", 50, 50));
			imgs.get("Building.png").add(loadImage("Building1.png", 50, 50));
			imgs.get("Building.png").add(loadImage("Building2.png", 50, 50));
			imgs.get("Building.png").add(loadImage("Building3.png", 50, 50));

			imgs.get("Road.png").add(loadImage("Road0.png", 50, 50));
			imgs.get("Road.png").add(loadImage("Road1.png", 50, 50));
			imgs.get("Road.png").add(loadImage("Road2.png", 50, 50));
			imgs.get("Road.png").add(loadImage("Road3.png", 50, 50));

			imgs.get("Warehouse.png").add(loadImage("Warehouse0.png", 50, 50));
			imgs.get("Warehouse.png").add(loadImage("Warehouse1.png", 50, 50));
			imgs.get("Warehouse.png").add(loadImage("Warehouse2.png", 50, 50));
			imgs.get("Warehouse.png").add(loadImage("Warehouse3.png", 50, 50));
		} catch (Exception e) {
			GUI.loadError();
			GUI.appendMessage(e.getMessage());
			e.printStackTrace();
		} finally {
			paintNew(false, size);
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		placeMap(g);
		revalidate();
		repaint();
	}

	private void placeMap(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		// create new random map
		if (makeNew) {
			createMap();
			makeNew = false;
		}
		for (int i = 0; i < allTiles.length; i++) {
			for (int j = 0; j < allTiles[i].length; j++) {
				g2d.drawImage(allTiles[i][j].getImage(), 10 + (i * 50), 10 + (j * 50), this);
			}
		}

	}

	// Method to create a new random map
	private void createMap() {

		// build main road
		int roadRand = rand.nextInt(2);
		// Horizontal main road
		int midX = allTiles.length / 2, midY = allTiles[0].length / 2;
		if (roadRand == 0) {
			for (int i = 0; i < allTiles.length; i++) {
				allTiles[i][midY].setImage(imgs.get("Road.png").get(0), "Road.png");
			}
		}
		// Vertical main road
		if (roadRand == 1) {
			for (int i = 0; i < allTiles[0].length; i++) {
				allTiles[midX][i].setImage(imgs.get("Road.png").get(0), "Road.png");
			}
		}

		// add building tiles
		for (int i = 0; i < allTiles.length; i++) {
			for (int j = 0; j < allTiles[i].length; j++) {
				int buildRand = rand.nextInt(100);
				// if tile is empty
				if (allTiles[i][j].equals("Empty.png")) {
					boolean nextToRoad = graph.hasImage(allTiles[i][j], "Road.png");
					// Building
					if (buildRand < 30 && nextToRoad) {
						allTiles[i][j].setImage(imgs.get("Building.png").get(0), "Building.png");
						buildingLocations.add(allTiles[i][j]);
					}

					// Warehouse
					if (buildRand >= 30 && buildRand < 45 && !nextToRoad) {
						allTiles[i][j].setImage(imgs.get("Warehouse.png").get(0), "Warehouse.png");
						buildingLocations.add(allTiles[i][j]);
					}

					// More Buildings
					if (buildRand >= 45 && buildRand < 60 && !nextToRoad) {
						allTiles[i][j].setImage(imgs.get("Building.png").get(0), "Building.png");
						buildingLocations.add(allTiles[i][j]);
					}
				}
			}
		}

		// make list without 'Road.png' for adding roads using BFS
		Set<String> keys = imgs.keySet();
		List<BufferedImage> tempImgs = new ArrayList<BufferedImage>();
		for (String str : keys) {
			if (str.equals("Road.png")) {
				continue;
			}
			tempImgs.add(imgs.get(str).get(0));
		}

		// add roads to connect warehouse to main road using BFS
		// uses iterator to remove empty tiles from list of building locations
		BFS bfs = new BFS(graph, graph.getUnweightedMap(), tempImgs);
		Iterator<Tile> it = buildingLocations.iterator();
		while (it.hasNext()) {
			Tile tile = it.next();
			List<Tile> newRoads = new ArrayList<Tile>();
			newRoads.addAll(bfs.breadthFirstSearch(tile, imgs.get("Road.png").get(0)));
			// if blocked off from any road tile, remove
			if (newRoads.size() == 0) {
				tile.setImage(empty[0], "Empty.png");
				it.remove();
			}
			// connect building tile with road tile
			for (Tile newTile : newRoads) {
				if (!newTile.equals("Road.png") && newTile.equals("Empty.png")) {
					newTile.setImage(imgs.get("Road.png").get(0), "Road.png");
				}
			}
		}

		// place SAR base(s)
		for (int i = 0; i < numSearch; i++) {
			while (true) {
				int placeRandX = rand.nextInt(allTiles.length);
				int placeRandY = rand.nextInt(allTiles[0].length);
				Tile temp = allTiles[placeRandX][placeRandY];

				// checks if tile is empty and next to road
				if (temp.equals("Empty.png") && graph.hasImage(temp, "Road.png")) {
					temp.setImage(sarBase, "SARBase.png");
					sarLocations.add(temp);
					break;
				}
			}
		}

		// place lost person(s)
		for (int i = 0; i < numLost; i++) {
			while (true) {
				int placeRandX = rand.nextInt(allTiles.length);
				int placeRandY = rand.nextInt(allTiles[0].length);
				Tile temp = allTiles[placeRandX][placeRandY];

				// checks if tile is not empty and not next to sar base and tile is not sar base
				if (!temp.equals("Empty.png") && !graph.hasImage(temp, "SARBase.png") && !temp.equals("SARBase.png")
						&& !temp.getLost()) {
					temp.setLost(true);
					lostLocations.add(temp);
					break;
				}
			}
			changeVisibility(lostVisible);
		}
		graph.deleteEdgesWith(empty[0]);
		for (int i = 0; i < allTiles.length; i++) {
			for (int j = 0; j < allTiles[i].length; j++) {
				if (allTiles[i][j].equals("Empty.png")) {
					allTiles[i][j].setImage(empty[1]);
				}
			}
		}

	}

	// Method to change whether lost person(s) is visually visible
	public void changeVisibility(int img) {
		this.lostVisible = img;
		changeVisibility(lostLocations, img);
	}

	// Overload method to call each tile to set image
	private void changeVisibility(List<Tile> tiles, int img) {
		for (Tile tile : tiles) {
			changeVisibility(tile, img);
		}
	}

	// Actual method to set image accordingly (see BufferedImage sorting purposes)
	private void changeVisibility(Tile tile, int img) {
		if (tile.equals("Road.png"))
			tile.setImage(imgs.get("Road.png").get(img));
		if (tile.equals("Warehouse.png"))
			tile.setImage(imgs.get("Warehouse.png").get(img));
		if (tile.equals("Building.png"))
			tile.setImage(imgs.get("Building.png").get(img));
	}

	// fill graph with necessary edges
	private void findNeighbors() {
		for (int i = 0; i < allTiles.length; i++) {
			for (int j = 0; j < allTiles[i].length; j++) {
				if (j - 1 >= 0) {// up
					graph.addEdge(allTiles[i][j], allTiles[i][j - 1]);
				}
				if (i + 1 < allTiles.length) {// right
					graph.addEdge(allTiles[i][j], allTiles[i + 1][j]);
				}
				if (j + 1 < allTiles[0].length) {// down
					graph.addEdge(allTiles[i][j], allTiles[i][j + 1]);
				}
				if (i - 1 >= 0) {// left
					graph.addEdge(allTiles[i][j], allTiles[i - 1][j]);
				}
			}
		}
	}

	// Method to make empty map (clear and program start)
	public void paintNew(boolean makeNew, String size) {
		paintNew(makeNew, size, 0, 0);
	}

	// Actual method to create map
	public void paintNew(boolean makeNew, String size, int numLost, int numSearch) {
		// get new values to make new map
		this.numLost = numLost;
		this.numSearch = numSearch;
		this.graph = new Graph();
		this.makeNew = makeNew;
		this.size = size;
		// reset everything
		this.buildingLocations = new ArrayList<Tile>();
		this.lostLocations = new ArrayList<Tile>();
		this.sarLocations = new ArrayList<Tile>();
		// choose size
		if (size.equals("small")) {
			allTiles = new Tile[9][7];
		} else if (size.equals("medium")) {
			allTiles = new Tile[15][11];
		} else if (size.equals("big")) {
			allTiles = new Tile[19][13];
		} else {
			allTiles = new Tile[9][7];
		}
		// initialize new tiles
		for (int i = 0; i < allTiles.length; i++) {
			for (int j = 0; j < allTiles[i].length; j++) {
				allTiles[i][j] = new Tile(empty[0]);
			}
		}
		findNeighbors();
		repaint();
	}

	// Method to trace path and print text
	//
	// path is traced one by one from the
	// "nearest" lost person to the "nearest" sar base
	public void findPath() {
		Dijkstra dijkstra = new Dijkstra(graph, graph.getWeightedMap(new ArrayList<String>(imgs.keySet())));
		// make minimum distance graph for each sar base(s)
		for (Tile tile : sarLocations) {
			dijkstra.DijkstraSearch(tile);
		}

		// sort by minimum distance
		PriorityQueue<Tile> lostPQueue = new PriorityQueue<>(lostLocations);

		// add all path from sar base to lost
		// and lost to sar base
		allPaths = new ArrayList<Tile>();
		for (Tile tile : lostPQueue) {
			allPaths.addAll(dijkstra.traceRoute(tile, false));
			allPaths.addAll(dijkstra.traceRoute(tile, true));
		}

		// start path tracing
		numLoop = numFound = 0;
		lostFound = afterOne = false;
		timer = new Timer(DELAY, this);
		timer.start();
	}

	// Actual method to trace path and print text
	private void goToAllLost() {
		if (numLoop < allPaths.size()) {
			// if lost person is found
			if (allPaths.get(numLoop).getLost()) {
				lostFound = afterOne = true;
				allPaths.get(numLoop).setLost(false);
				numFound++;
				GUI.appendMessage("\n");
				// if lost person reaches sar base
			} else if (allPaths.get(numLoop).equals("SARBase.png")) {
				lostFound = false;
				changeVisibility(allPaths.get(numLoop - 1), 0);
			}

			// if currently walking to lost person
			if (!lostFound && !allPaths.get(numLoop).equals("SARBase.png")) {
				changeVisibility(allPaths.get(numLoop), 3);
				String str = allPaths.get(numLoop).getName();
				str.substring(0, str.lastIndexOf("."));
				GUI.appendMessage("Walking through " + str + "\n");
			} else {
				// if lost person is located
				if (afterOne) {
					changeVisibility(allPaths.get(numLoop), 2);
					changeVisibility(allPaths.get(numLoop - 1), 3);
					String str;
					switch (numFound) {
					case 0:
					case 1:
						str = "st";
						break;
					case 2:
						str = "nd";
						break;
					case 3:
						str = "rd";
						break;
					default:
						str = "th";
					}
					GUI.appendMessage("Located " + numFound + str + "\n");
					afterOne = false;
				}
				// if lost person currently going back to sar base
				else {
					changeVisibility(allPaths.get(numLoop), 2);
					changeVisibility(allPaths.get(numLoop - 1), 0);
					GUI.appendMessage("Going back to SAR Base\n");
				}
			}
		}
		// end timer if all lost person(s) found
		else {
			GUI.appendMessage("\nAll missing person(s) found\n");
			GUI.endSearch();
			timer.stop();
		}
	}

	// Method to load and resize image
	public BufferedImage loadImage(String imageFile, int targetWidth, int targetHeight) throws IOException {
		BufferedImage originalImage = null;
		originalImage = ImageIO.read(getClass().getClassLoader().getResourceAsStream(imageFile));

		BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics2D = resizedImage.createGraphics();
		graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
		graphics2D.dispose();

		return resizedImage;
	}

	// Timer Listener
	@Override
	public void actionPerformed(ActionEvent e) {
		goToAllLost();
		numLoop++;
	}
}
