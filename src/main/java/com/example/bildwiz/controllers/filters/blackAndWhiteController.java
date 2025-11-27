package com.example.bildwiz.controllers.filters;

import com.example.bildwiz.controllers.CanvasController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class blackAndWhiteController extends CanvasController {

    protected CanvasController parent;

    public void setParent(CanvasController parent) {
        this.parent = parent;
    }

}
