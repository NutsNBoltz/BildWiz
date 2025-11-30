package com.example.bildwiz.controllers.filters;

import com.example.bildwiz.controllers.CanvasController;
import javafx.fxml.FXML;

public class GrayscaleController extends CanvasController {

    private CanvasController canvas;

    public void setParent(CanvasController controller) {
        this.canvas = controller;
    }




    @FXML
    public void initialize() {}

}
