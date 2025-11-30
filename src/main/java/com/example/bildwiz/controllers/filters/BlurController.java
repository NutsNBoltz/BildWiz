package com.example.bildwiz.controllers.filters;

import com.example.bildwiz.controllers.CanvasController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.util.Duration;

import java.awt.image.BufferedImage;

public class BlurController extends  CanvasController {

    private CanvasController canvas;


    public void setParent(CanvasController controller) {
        this.canvas = controller;
    }



    public BlurController() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000), event -> {

            Image image = canvas.selectedImage.getImage();

            if (image != null) {

   //             applyFilter(image);
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();


    }




    public Image bufferedToFxImage(BufferedImage bufferedImage) {
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }



    public Image applyFilter(Image image) {

        BufferedImage imgTemplate = SwingFXUtils.fromFXImage(image, null);

        BufferedImage blurImage = new BufferedImage(imgTemplate.getWidth()-2,
                imgTemplate.getHeight()-2,
                BufferedImage.TYPE_BYTE_GRAY);

        int pix = 0;

        for (int y = 0; y < blurImage.getHeight(); y++) {
            for (int x = 0; x < blurImage.getWidth(); x++) {
                pix =
                        (int) (4*(imgTemplate.getRGB(x+1, y+1) & 0xFF))
                        + 2*(imgTemplate.getRGB(x+1, y)& 0xFF)
                        + 2*(imgTemplate.getRGB(x+1, y+2)& 0xFF)
                        + 2*(imgTemplate.getRGB(x, y+1)& 0xFF)
                        + 2*(imgTemplate.getRGB(x+2, y+1)& 0xFF)
                        + (imgTemplate.getRGB(x, y)& 0xFF)
                        + (imgTemplate.getRGB(x+2, y)& 0xFF)
                        + (imgTemplate.getRGB(x, y+2)& 0xFF)
                        + (imgTemplate.getRGB(x+2, y+2)& 0xFF);

                int p = (255<<24) | (pix<<16) | (pix<<8) | pix;

                blurImage.setRGB(x, y, p);


            }
        }

        return bufferedToFxImage(blurImage);
    }

    @FXML
    public void initialize() {
    }
}