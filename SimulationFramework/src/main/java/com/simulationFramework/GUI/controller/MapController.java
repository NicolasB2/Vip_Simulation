package com.simulationFramework.GUI.controller;

import java.io.IOException;
import java.net.URL;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import lombok.Data;

@Data
public class MapController {
	
	private GUIController mainController;
	
	private WebEngine webEngine;
	
    @FXML
    private WebView wvMap;
    
    @FXML
    private void initialize() throws IOException {
    	 webEngine = wvMap.getEngine();
    	 URL url = getClass().getResource(GUIController.VIEW_ADDRESS+"Map.html");
         webEngine.load(url.toExternalForm());
    }
    
    @FXML
    void butCloseAction(ActionEvent event) {
    	mainController.getMiMap().fire();
    }
    
}
