// License: GPL. For details, see Readme.txt file.
package org.openstreetmap.gui.jmapviewer;

import java.util.ArrayList;
import java.util.List;

import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;
import org.openstreetmap.gui.jmapviewer.interfaces.MapObject;

public class Layer extends AbstractLayer {
    private List<MapObject> elements;

    public Layer(String name) {
        super(name);
    }

    public Layer(String name, String description) {
        super(name, description);
    }

    public Layer(String name, Style style) {
        super(name, style);
    }

    public Layer(String name, String description, Style style) {
        super(name, description, style);
    }

    public Layer(LayerGroup parent, String name) {
        super(parent, name);
    }

    public Layer(LayerGroup parent, String name, Style style) {
        super(parent, name, style);
    }

    public Layer(LayerGroup parent, String name, String description, Style style) {
        super(parent, name, description, style);
    }

    public List<MapObject> getElements() {
        return elements;
    }

    public void setElements(List<MapObject> elements) {
        this.elements = elements;
    }
    
    public MapMarkerDot getSingleDotElement(double lat,double lon) {
    	MapMarkerDot dotElement;
    	for(MapObject element:elements) {
    		dotElement = (MapMarkerDot) element;
    		if(dotElement.getLat() == lat && dotElement.getLon() == lon) {
    			return dotElement;
    		}
    	}
    	return null;
    }

    public List<MapMarkerDot> getDotElements(){
		List<MapMarkerDot> dotMarkers = new ArrayList<MapMarkerDot>();
		for (MapObject temp : elements) {
			MapMarkerDot markerTemp = (MapMarkerDot) temp;
			dotMarkers.add(markerTemp);
		}
		return dotMarkers;
    }
    public Layer add(MapObject element) {
        element.setLayer(this);
        elements = add(elements, element);
        return this;
    }
}
