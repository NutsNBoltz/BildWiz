package com.example.bildwiz.controllers.filters;

import com.example.bildwiz.controllers.CanvasController;
import com.example.bildwiz.controllers.FilterController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.util.Duration;

import java.awt.image.BufferedImage;
import java.security.PrivateKey;

public class BlurController extends  CanvasController implements FilterController {

    private CanvasController canvas;


    public void setParent(CanvasController controller) {
        this.canvas = controller;
    }

    @FXML
    private Slider blurSlider;




    public void applyFilter() {


        Image image = canvas.selectedImage.getImage();

        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);


        int iterations = (int) blurSlider.getValue();

        for (int i = 0; i<iterations; i++ ) {
            bImage = blurOnce(bImage);
        }


        canvas.selectedImage.setImage(SwingFXUtils.toFXImage(bImage, null));
    }

    private BufferedImage blurOnce(BufferedImage image) {

        BufferedImage output = new BufferedImage(
                image.getWidth() - 2,
                image.getHeight() - 2,
                BufferedImage.TYPE_INT_ARGB
        );

        int[][] weight = {
                {1, 2, 1},
                {2, 4, 2},
                {1, 2, 1}
        };

        int maxWeight = 16;

        for (int y = 0; y < output.getHeight(); y++) {
            for (int x = 0; x < output.getWidth(); x++) {

                int sumR = 0, sumG = 0, sumB = 0;

                for (int ky = 0; ky < 3; ky++) {
                    for (int kx = 0; kx < 3; kx++) {

                        int rgb = image.getRGB(x + kx, y + ky);

                        int r = (rgb >> 16) & 0xff;
                        int g = (rgb >> 8) & 0xff;
                        int b =  rgb        & 0xff;

                        int weightage = weight[ky][kx];

                        sumR += r * weightage;
                        sumG += g * weightage;
                        sumB += b * weightage;
                    }
                }

                int r = sumR / maxWeight;
                int g = sumG / maxWeight;
                int b = sumB / maxWeight;

                int blurredRGB = (0xFF << 24) | (r << 16) | (g << 8) | b;

                output.setRGB(x, y, blurredRGB);
            }
        }

        return output;
    }



    @FXML
    public void initialize() {
    }


}