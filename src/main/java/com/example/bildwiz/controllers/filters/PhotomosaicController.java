package com.example.bildwiz.controllers.filters;

import com.example.bildwiz.controllers.CanvasController;

public class PhotomosaicController extends CanvasController {

    protected CanvasController parent;

    public void setParent(CanvasController parent) {
        this.parent = parent;
    }

}
