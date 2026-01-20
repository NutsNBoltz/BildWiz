package com.example.bildwiz.controllers.filters;

import com.example.bildwiz.controllers.CanvasController;
import com.example.bildwiz.controllers.FilterController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Slider;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;

public class BlurController extends  CanvasController implements FilterController {

    private CanvasController canvas;


    public void setParent(CanvasController controller) {
        this.canvas = controller;
    }

    @FXML
    private Slider blurSlider;




    public void applyFilter() {

        int iterations = (int) blurSlider.getValue();

        if (!canvas.selectionMode.isSelected()) {
            Image image = canvas.selectedImage.getImage();

            BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);



            for (int i = 0; i<iterations; i++ ) {
                bImage = blurOnce(bImage);
            }

            canvas.selectedImage.setImage(SwingFXUtils.toFXImage(bImage, null));
        } else {

            BufferedImage filteredImage = FilterController.applyWithSelection(
                    canvas.selectedImage,
                    canvas.selectionRect,
                    canvas.exclusionMode.isSelected(),
                    input -> {
                        BufferedImage result = blurOnce(input);
                        for (int i = 0; i<iterations; i++ ) {
                            result = blurOnce(result);
                        }
                        return result;
                    }
            );

            canvas.selectedImage.setImage(SwingFXUtils.toFXImage(filteredImage, null));
        }



    }

    private BufferedImage blurOnce(BufferedImage image) {


        DataBufferInt buffer = (DataBufferInt) image.getRaster().getDataBuffer();
        int[] pixels = buffer.getData();

        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage output;

        int[] flatWeight = {1, 2, 1, 2, 4, 2, 1, 2, 1};

        int[] outputArray = new int[height * width];


        IntStream.range(0, height).parallel().forEach( y -> {
            for (int x = 0; x < width; x++) {

                int sumR = 0, sumG = 0, sumB = 0;
                int weightSum = 0;

                for (int ky = -1; ky <= 1; ky++) {
                    for (int kx = -1; kx <= 1; kx++) {
                        int nx = x + kx;
                        int ny = y + ky;

                        // check bounds for edges
                        if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                            int rgb = pixels[ny * width + nx];
                            int r = (rgb >> 16) & 0xff;
                            int g = (rgb >> 8) & 0xff;
                            int b = rgb & 0xff;

                            int w = flatWeight[(ky + 1) * 3 + (kx + 1)];

                            sumR += r * w;
                            sumG += g * w;
                            sumB += b * w;
                            weightSum += w;
                        }
                    }
                }

                int r = sumR / weightSum;
                int g = sumG / weightSum;
                int b = sumB / weightSum;

                int blurredRGB = (0xFF << 24) | (r << 16) | (g << 8) | b;

                outputArray[y * width + x] = blurredRGB;
            }

        });

        output = arrayToBuffered(outputArray, width, height);
        return output;
    }



    @FXML
    public void initialize() {
    }


}