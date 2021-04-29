package org.openstreetmap.gui.jmapviewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openstreetmap.gui.jmapviewer.events.JMVCommandEvent;
import org.openstreetmap.gui.jmapviewer.interfaces.JMapViewerEventListener;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;
import org.openstreetmap.gui.jmapviewer.interfaces.TileLoader;
import org.openstreetmap.gui.jmapviewer.interfaces.TileSource;
import org.openstreetmap.gui.jmapviewer.interfaces.MapObject;
import org.openstreetmap.gui.jmapviewer.tilesources.BingTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.BingAerialTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;


import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import simulator.MainMenu;

/**
 * Map display of Project, based off of JMapviewer https://wiki.openstreetmap.org/wiki/JMapViewer
 *
 */
public class Map extends JFrame implements JMapViewerEventListener {

	private static final long serialVersionUID = 1L;

	private final JMapViewerTree treeMap;

	private final JLabel zoomLabel;
	private final JLabel zoomValue;

	private final JLabel mperpLabelName;
	private final JLabel mperpLabelValue;
	
	private Boolean red = false;
	private Boolean yellow = false;
	private Boolean green = false;


	/**
	 * Constructs the Map
	 * @throws IOException 
	 * @throws CsvValidationException 
	 */
	public Map() throws CsvValidationException, IOException {
		super("JMapViewer Demo");
		setSize(400, 400);

		treeMap = new JMapViewerTree("Zones");

		// Listen for user operation
		map().addJMVListener(this);

		setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		JPanel panel = new JPanel(new BorderLayout());
		JPanel panelTop = new JPanel();
		JPanel panelBottom = new JPanel();
		JPanel helpPanel = new JPanel();

		mperpLabelName = new JLabel("Meters/Pixels: ");
		mperpLabelValue = new JLabel(String.format("%s", map().getMeterPerPixel()));

		zoomLabel = new JLabel("Zoom: ");
		zoomValue = new JLabel(String.format("%s", map().getZoom()));
		
		//Creates the LayerGroups and Layers
		LayerGroup yorkshire = new LayerGroup("Yorkshire");
		Layer northYorkshire = yorkshire.addLayer("N. Yorkshire Cover");
		Layer northYorkshireOuterLr = yorkshire.addLayer("Outer layer");
		Layer northYorkshireLr = yorkshire.addLayer("North Yorkshire");
		//Creates the outer most marker
		map().addMapMarker(new MapMarkerDot(northYorkshire, "North Yorkshire", 54.17, -1.248));
		
		checkMarkers(northYorkshireLr,northYorkshireOuterLr,northYorkshire);
		
		add(panel, BorderLayout.NORTH);
		add(helpPanel, BorderLayout.SOUTH);
		panel.add(panelTop, BorderLayout.NORTH);
		panel.add(panelBottom, BorderLayout.SOUTH);
		JLabel helpLabel = new JLabel(
				"Use left mouse button to move,\n " + "left double click or mouse wheel to zoom.");
		helpPanel.add(helpLabel);
		JButton button = new JButton("setDisplayToFitMapMarkers");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				map().setDisplayToFitMapMarkers();
			}
		});
		JComboBox<TileSource> tileSourceSelector = new JComboBox<>(new TileSource[] { new OsmTileSource.Mapnik(),
				new OsmTileSource.CycleMap(), new OsmTileSource.TransportMap(), new OsmTileSource.LandscapeMap(),
				new OsmTileSource.OutdoorsMap(), new BingTileSource(), new BingAerialTileSource() });
		tileSourceSelector.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				map().setTileSource((TileSource) e.getItem());
			}
		});
		JComboBox<TileLoader> tileLoaderSelector;
		tileLoaderSelector = new JComboBox<>(new TileLoader[] { new OsmTileLoader(map()) });
		tileLoaderSelector.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				map().setTileLoader((TileLoader) e.getItem());
			}
		});
		map().setTileLoader((TileLoader) tileLoaderSelector.getSelectedItem());
		panelTop.add(tileSourceSelector);
		panelTop.add(tileLoaderSelector);
		// The 3 checkboxes for only displaying the warnings
		final JCheckBox showWarning1 = new JCheckBox("Show Level 1 Warning");
		showWarning1.addActionListener(new ActionListener() {
			@Override 
			public void actionPerformed(ActionEvent e) {
				if(showWarning1.isSelected()) {
					red=true;
				}
				else {
				red = false;
				}
			}
		});
		panelBottom.add(showWarning1);
		final JCheckBox showWarning2 = new JCheckBox("Show Level 2 Warning");
		showWarning2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(showWarning2.isSelected()) {
					yellow=true;
				}
				else {
				yellow = false;
				}
			}
		});
		panelBottom.add(showWarning2);
		final JCheckBox showWarning3 = new JCheckBox("Show Level 3 Warning");
		showWarning3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(showWarning3.isSelected()) {
					green=true;
				}
				else {
				green = false;
				}
			}
		});
		panelBottom.add(showWarning3);
		
		final JCheckBox showMapMarker = new JCheckBox("Map markers visible");
		showMapMarker.setSelected(map().getMapMarkersVisible());
		showMapMarker.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				map().setMapMarkerVisible(showMapMarker.isSelected());
			}
		});
		panelBottom.add(showMapMarker);
		///
		final JCheckBox showTreeLayers = new JCheckBox("Tree Layers visible");
		showTreeLayers.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				treeMap.setTreeVisible(showTreeLayers.isSelected());
			}
		});
		panelBottom.add(showTreeLayers);
		///
		final JCheckBox showToolTip = new JCheckBox("ToolTip visible");
		showToolTip.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				map().setToolTipText(null);
			}
		});
		panelBottom.add(showToolTip);
		///
		final JCheckBox showTileGrid = new JCheckBox("Tile grid visible");
		showTileGrid.setSelected(map().isTileGridVisible());
		showTileGrid.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				map().setTileGridVisible(showTileGrid.isSelected());
			}
		});
		panelBottom.add(showTileGrid);
		final JCheckBox showZoomControls = new JCheckBox("Show zoom controls");
		showZoomControls.setSelected(map().getZoomControlsVisible());
		showZoomControls.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				map().setZoomContolsVisible(showZoomControls.isSelected());
			}
		});
		panelBottom.add(showZoomControls);
		final JCheckBox scrollWrapEnabled = new JCheckBox("Scrollwrap enabled");
		scrollWrapEnabled.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				map().setScrollWrapEnabled(scrollWrapEnabled.isSelected());
			}
		});
		//updates map
        map().zoomSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
            	checkMarkers(northYorkshireLr,northYorkshireOuterLr,northYorkshire);
            }
        });
        //Returns the user back to main menu
		final JButton back = new JButton("Main Menu");
		back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new MainMenu().setVisible(true);
				dispose();
			}
		});
		
		panelBottom.add(scrollWrapEnabled);
		panelBottom.add(back);
		panelBottom.add(button);

		panelTop.add(zoomLabel);
		panelTop.add(zoomValue);
		panelTop.add(mperpLabelName);
		panelTop.add(mperpLabelValue);

		add(treeMap, BorderLayout.CENTER);

		
		try {
			reader(northYorkshireLr);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		generateOuterLayers(northYorkshireOuterLr);
		
		map().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					map().getAttribution().handleAttribution(e.getPoint(), true);
				}
			}
		});

		map().addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				Point p = e.getPoint();
				boolean cursorHand = map().getAttribution().handleAttributionCursor(p);
				if (cursorHand) {
					map().setCursor(new Cursor(Cursor.HAND_CURSOR));
				} else {
					map().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
				if (showToolTip.isSelected())
					map().setToolTipText(map().getPosition(p).toString());
			}
		});
	}
	
	//Generates the 'middle' Layer markers
	private void generateOuterLayers(Layer layer) {
		map().addMapMarker(new MapMarkerDot(layer, "Whitby", 54.48, -0.61));
		map().addMapMarker(new MapMarkerDot(layer, "Ripon", 54.13, -1.52));
		map().addMapMarker(new MapMarkerDot(layer,"Boroughbridge",54.09,-1.39));
		map().addMapMarker(new MapMarkerDot(layer,"Skipton",53.96,-2.01));
		map().addMapMarker(new MapMarkerDot(layer,"Settle",54.06,-2.28));
		map().addMapMarker(new MapMarkerDot(layer,"Scarborough",54.28,-0.399));
		map().addMapMarker(new MapMarkerDot(layer,"Knaresborough",54.011,-1.471));
		map().addMapMarker(new MapMarkerDot(layer,"Selby",53.78,-1.067));
		map().addMapMarker(new MapMarkerDot(layer,"River Aire",53.71,-1.089));
		map().addMapMarker(new MapMarkerDot(layer,"Malton",54.136,-0.798));
		map().addMapMarker(new MapMarkerDot(layer,"Pickering",54.234,-0.777));
		map().addMapMarker(new MapMarkerDot(layer,"Thornton Dale",54.236,-0.719));
		map().addMapMarker(new MapMarkerDot(layer,"Catterick",54.377,-1.629));
		map().addMapMarker(new MapMarkerDot(layer,"Northallerton",54.338,-1.429));
		map().addMapMarker(new MapMarkerDot(layer,"Brompton",54.362,-1.420));
		map().addMapMarker(new MapMarkerDot(layer,"Hellifield",54.004,-2.219));
		map().addMapMarker(new MapMarkerDot(layer,"Crosshills",53.901,-1.989));
		map().addMapMarker(new MapMarkerDot(layer,"River Rye",54.205,-0.946));
		map().addMapMarker(new MapMarkerDot(layer,"River Seven",54.226,-0.874));
		map().addMapMarker(new MapMarkerDot(layer,"High Bentham",54.113,-2.510));
		map().addMapMarker(new MapMarkerDot(layer,"River Leven",54.472,-1.185));


	}
	//Reader of CSV files
	private void reader(Layer layer) throws CsvValidationException, IOException {

		String[] arrays;
		ArrayList<ArrayList<String>> curFloods = new ArrayList<ArrayList<String>>();
		CSVReader floodsReader = new CSVReader(
				new FileReader("assets/floods.csv"));
		floodsReader.readNext();

		while ((arrays = floodsReader.readNext()) != null) // returns a Boolean value
		{
			ArrayList<String> flood = new ArrayList<String>();
			flood.add(arrays[6]);
			flood.add(arrays[2]);
			curFloods.add(flood);
		}

		String[] read;
		CSVReader reader = new CSVReader(
				new FileReader("assets/floodAreas.csv"));
		reader.readNext();
		while ((read = reader.readNext()) != null) // returns a Boolean value
		{
			double lat = Double.parseDouble(read[1]);
			double lon = Double.parseDouble(read[2]);

			for (ArrayList<String> activeFloods : curFloods) {
				if (activeFloods.contains(read[7])) {
					if (activeFloods.get(1).equals("1")) {
						map().addMapMarker(new MapMarkerDot(layer, read[3], lat, lon, MapMarkerDot.get1Style()));
						break;
					} else if (activeFloods.get(1).equals("2")) {
						map().addMapMarker(new MapMarkerDot(layer, read[3], lat, lon, MapMarkerDot.get2Style()));
						break;

					} else if (activeFloods.get(1).equals("3")) {
						map().addMapMarker(new MapMarkerDot(layer, read[3], lat, lon, MapMarkerDot.get3Style()));
						break;
					}
				} 
					else {
					map().addMapMarker(new MapMarkerDot(layer, read[3], lat, lon));
				}
			}

		}
	}

	private JMapViewer map() {
		return treeMap.getViewer();
	}

	private static Coordinate c(double lat, double lon) {
		return new Coordinate(lat, lon);
	}


	//This function's purpose is to check if any warnings need to be specifically visible 
	private void checkMarkers(Layer innerLayer,Layer outerLayer,Layer coverLayer) {
		List<MapMarker> allMarkers = map().getMapMarkerList();
		List<MapMarkerDot> allDotMarkers = new ArrayList<MapMarkerDot>();
		for (MapMarker temp : allMarkers) {
			MapMarkerDot markerTemp = (MapMarkerDot) temp;
			allDotMarkers.add(markerTemp);
		}
		
		if (red == true || yellow == true || green == true) {
			innerLayer.setVisible(false);
			outerLayer.setVisible(false);
			coverLayer.setVisible(false);
			
			for (MapMarkerDot temp : allDotMarkers) {
				if (red == true && temp.getDotColour().equals(Color.RED)) {
						temp.setVisible(true);
				}			
				if (yellow == true && temp.getDotColour().equals(Color.YELLOW)) {
						temp.setVisible(true);
				}			
				if (green == true && temp.getDotColour().equals(Color.GREEN)) {
						temp.setVisible(true);
				}
			} 
		} else {
			
			for (MapMarkerDot temp : allDotMarkers) {
				if (temp.getDotColour().equals(Color.RED) || temp.getDotColour().equals(Color.YELLOW) || temp.getDotColour().equals(Color.GREEN) ) {
					temp.setVisible(false);
				}
			}
			hideMarkers(innerLayer, outerLayer, coverLayer,allDotMarkers);
		}
	}
	//Function thats purpose is to control which markers are visible normally
	private void hideMarkers(Layer innerLayer,Layer outerLayer,Layer coverLayer,List<MapMarkerDot> allDotMarkers) {
		if(map().getZoom()>=14) {
			innerLayer.setVisible(true);
			outerLayer.setVisible(false);
			coverLayer.setVisible(false);
			
			for (MapMarkerDot temp : allDotMarkers) {
				if (temp.getDotColour().equals(Color.RED) || temp.getDotColour().equals(Color.YELLOW) || temp.getDotColour().equals(Color.GREEN) ) {
					temp.setVisible(true);
				}
			}
		}
		else {
			innerLayer.setVisible(false);
			
			for (MapMarkerDot temp : allDotMarkers) {
				if (temp.getDotColour().equals(Color.RED) || temp.getDotColour().equals(Color.YELLOW) || temp.getDotColour().equals(Color.GREEN) ) {
					temp.setVisible(false);
				}
			}
		}
		
		if(map().getZoom()>=8 && map().getZoom()<=13) {
			outerLayer.setVisible(true);
			innerLayer.setVisible(false);
			coverLayer.setVisible(false);
			
			for (MapMarkerDot temp : allDotMarkers) {
				if (temp.getDotColour().equals(Color.RED) || temp.getDotColour().equals(Color.YELLOW) || temp.getDotColour().equals(Color.GREEN) ) {
					temp.setVisible(false);
				}
			}

		}
		else if(map().getZoom()>=1 || map().getZoom()>=14) {
			outerLayer.setVisible(false);
		}
		
		if(map().getZoom()<8) {
			coverLayer.setVisible(true);
			outerLayer.setVisible(false);
			innerLayer.setVisible(false);
			
			for (MapMarkerDot temp : allDotMarkers) {
				if (temp.getDotColour().equals(Color.RED) || temp.getDotColour().equals(Color.YELLOW) || temp.getDotColour().equals(Color.GREEN) ) {
					temp.setVisible(false);
				}
			}
			
		}
		else {
			coverLayer.setVisible(false);
		}
	}

	private void updateZoomParameters() {
		if (mperpLabelValue != null)
			mperpLabelValue.setText(String.format("%s", map().getMeterPerPixel()));
		if (zoomValue != null)
			zoomValue.setText(String.format("%s", map().getZoom()));
	}

	@Override
	public void processCommand(JMVCommandEvent command) {
		if (command.getCommand().equals(JMVCommandEvent.COMMAND.ZOOM)
				|| command.getCommand().equals(JMVCommandEvent.COMMAND.MOVE)) {
			updateZoomParameters();
		}
	}
}
