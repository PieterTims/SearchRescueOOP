package allcode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class GUI extends JFrame implements ActionListener, ChangeListener, ItemListener {
	private static final long serialVersionUID = 1L;

	// panels
	private MapPanel mapArea;
	private JPanel controlArea;

	// wrap panels
	private static JPanel sizeControl;
	private static JPanel missingControl;
	private static JPanel searchingControl;
	private static JPanel buttonControl;

	// scroll panel inside controlArea
	private JScrollPane scrollArea;
public static JTextArea textArea;

	// values
	private final int WIDTH = 708;
	private final int HEIGHT = 408;
	private String size = "small";
	private int numLost = 2;
	private int numSearch = 1;

	private final Dimension[] sizeDimensions = { new Dimension(708, 408), new Dimension(1008, 608),
			new Dimension(1208, 708) };

	// buttons
	private static JRadioButton small, medium, big;
	private static JSlider missing, searching;
	private static JButton clear, random, start;
	private static JCheckBox isVisible;

	// labels
	private static JLabel sizeLabel, missingLabel, searchingLabel;
	private static Container container;

	// Constructor Method
	public GUI() {

		// window
		this.setTitle("Blind Pathfinding Sim");
		this.setSize(WIDTH, HEIGHT);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBounds(0, 0, WIDTH, HEIGHT);
		this.setResizable(true);

		container = this.getContentPane();

		// GUI right panels
		controlArea = new JPanel();
		controlArea.setMaximumSize(new Dimension(200, 200));
		controlArea.setLayout(new BoxLayout(controlArea, BoxLayout.Y_AXIS));

		textArea = new JTextArea(1, 50);
		textArea.setBackground(Color.BLACK);
		textArea.setForeground(Color.WHITE);
		textArea.setVisible(true);

		scrollArea = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollArea.setPreferredSize(new Dimension(200, 420));
		scrollArea.setBorder(BorderFactory.createLineBorder(Color.black));
		scrollArea.setVisible(true);

		// size buttons
		small = new JRadioButton("Small");
		small.setName("Small");
		small.addActionListener(this);
		medium = new JRadioButton("Medium");
		medium.setName("Medium");
		medium.addActionListener(this);
		medium.setSelected(true);
		big = new JRadioButton("Big");
		big.setName("Big");
		big.addActionListener(this);
		// add all JRadioButtons into one ButtonGroup
		ButtonGroup bg = new ButtonGroup();
		bg.add(small);
		bg.add(medium);
		bg.add(big);
		// wrap
		sizeControl = new JPanel();
		sizeControl.setMaximumSize(new Dimension(200, 30));
		sizeControl.add(small);
		sizeControl.add(medium);
		sizeControl.add(big);

		// missing persons slider
		missing = new JSlider(1, 10, 2);
		missing.setMaximumSize(new Dimension(340, 360));
		missing.setMinorTickSpacing(1);
		missing.setMajorTickSpacing(3);
		missing.setPaintTicks(true);
		missing.setPaintLabels(true);
		missing.setName("Missing");
		missing.addChangeListener(this);
		// wrap
		missingControl = new JPanel();
		missingControl.setMaximumSize(new Dimension(200, 50));
		missingControl.add(missing);

		// searching teams slider
		searching = new JSlider(1, 4, 1);
		searching.setMaximumSize(new Dimension(340, 360));
		searching.setMajorTickSpacing(1);
		searching.setSnapToTicks(true);
		searching.setPaintTicks(true);
		searching.setPaintLabels(true);
		searching.setName("Searching");
		searching.addChangeListener(this);
		// wrap
		searchingControl = new JPanel();
		searchingControl.setMaximumSize(new Dimension(200, 50));
		searchingControl.add(searching);

		// checkbox
		isVisible = new JCheckBox("Lost Person(s) Visible?");
		isVisible.addItemListener(this);
		isVisible.setAlignmentX(Component.CENTER_ALIGNMENT);

		// button and slider labels
		sizeLabel = new JLabel("Size: Medium");
		sizeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		missingLabel = new JLabel("# of Missing: 2");
		missingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		searchingLabel = new JLabel("# of Search Base: 1");
		searchingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		// map control buttons
		clear = new JButton("Clear");
		clear.setName("Clear");
		clear.addActionListener(this);
		random = new JButton("Shuffle");
		random.setName("Shuffle");
		random.addActionListener(this);
		start = new JButton("Start");
		start.setName("Start");
		start.addActionListener(this);
		start.setEnabled(false);

		// wrap
		buttonControl = new JPanel();
		buttonControl.setMaximumSize(new Dimension(240, 50));
		buttonControl.add(clear);
		buttonControl.add(random);
		buttonControl.add(start);

		// add all buttons, sliders, and labels into controlArea JPanel
		controlArea.add(sizeLabel);
		controlArea.add(sizeControl);
		controlArea.add(missingLabel);
		controlArea.add(missingControl);
		controlArea.add(searchingLabel);
		controlArea.add(searchingControl);
		controlArea.add(isVisible);
		controlArea.add(buttonControl);
		controlArea.add(scrollArea);

		// Map panel
		mapArea = new MapPanel(); // default map
		mapArea.setSize(getPreferredSize());
		mapArea.setBorder(BorderFactory.createLineBorder(Color.black));

		// add final JPanels into container
		container.add(mapArea, BorderLayout.CENTER);
		container.add(controlArea, BorderLayout.EAST);
	}

	// Changes size of JFrame if it is smaller than needed to paint MapPanel
	private void changeSize() {
		int newSize = (size.equals("small")) ? 0 : (size.equals("medium")) ? 1 : (size.equals("big")) ? 2 : 0;
		if (newSize == 2) {
			this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		} else {
			this.setSize(sizeDimensions[newSize]);
		}
	}

	// JButton and JRadioButton Listener
	@Override
	public void actionPerformed(ActionEvent e) {
		Object comp = e.getSource();
		// JButton Listener
		if (comp instanceof JButton) {
			JButton button = (JButton) comp;

			// shuffle button pressed
			if (button.getName().equals("Shuffle")) {
				start.setEnabled(true);
				size = (small.isSelected()) ? "small"
						: (medium.isSelected()) ? "medium" : (big.isSelected()) ? "big" : "small";

				mapArea.paintNew(true, size, numLost, numSearch);
				changeSize();
				textArea.setText(null);
				appendMessage("Shuffled map, size: " + size + "\n\n");
			}

			// clear button pressed
			if (button.getName().equals("Clear")) {
				start.setEnabled(false);
				size = "small";
				mapArea.paintNew(false, "small");
			}

			// start button pressed (enabled only if map is not empty)
			if (button.getName().equals("Start")) {
				sizeControl.setVisible(false);
				missingControl.setVisible(false);
				searchingControl.setVisible(false);
				isVisible.setVisible(false);

				clear.setEnabled(false);
				random.setEnabled(false);
				mapArea.findPath();
			}
		}

		// JRadioButton text changer
		if (comp instanceof JRadioButton) {
			JRadioButton size = (JRadioButton) comp;
			sizeLabel.setText("Size: " + size.getName());
		}
	}

//STATIC METHODS
	// appends message
	public static void appendMessage(String message) {
		textArea.append(message);
	}

	// Helper method to unhide JPanels after all path is traced
	public static void endSearch() {
		sizeControl.setVisible(true);
		missingControl.setVisible(true);
		searchingControl.setVisible(true);

		isVisible.setVisible(true);

		clear.setEnabled(true);
		random.setEnabled(true);
		start.setEnabled(false);
	}

	// Method to handle image load error
	public static void loadError() {
		sizeControl.setVisible(false);
		missingControl.setVisible(false);
		searchingControl.setVisible(false);
		buttonControl.setVisible(false);

		isVisible.setVisible(false);

		sizeLabel.setVisible(false);
		missingLabel.setVisible(false);
		searchingLabel.setVisible(false);

		textArea.setForeground(Color.RED);
		textArea.append("ERROR IN LOADING IMAGES\nERROR SOURCE:\n");
	}

//LISTENER METHODS
	// Sliders Listener
	@Override
	public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider) e.getSource();

		// slider for # of missing persons
		if (source.getName().equals("Missing")) {
			missingLabel.setText("# of Missing: " + source.getValue());
			numLost = source.getValue();
		}

		// slider for # of sar bases
		if (source.getName().equals("Searching")) {
			searchingLabel.setText("# of Search Base: " + source.getValue());
			numSearch = source.getValue();
		}
	}

	// CheckBox Listener
	@Override
	public void itemStateChanged(ItemEvent e) {
		JCheckBox source = (JCheckBox) e.getSource();

		if (source == isVisible) {
			int visibility = (e.getStateChange() == 1 ? 2 : 1);
			mapArea.changeVisibility(visibility);
		}

	}

}
