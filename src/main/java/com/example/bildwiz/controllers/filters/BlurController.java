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
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.security.PrivateKey;

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

            BufferedImage original = SwingFXUtils.fromFXImage(canvas.selectedImage.getImage(), null);
            int imgW = original.getWidth();
            int imgH = original.getHeight();

// ImageView display size (after zoom/scale)
            double viewW = canvas.selectedImage.getBoundsInLocal().getWidth();
            double viewH = canvas.selectedImage.getBoundsInLocal().getHeight();

// scale factor from view → image
            double scaleX = imgW / viewW;
            double scaleY = imgH / viewH;

// convert selection rectangle to image coordinates
            double selX = canvas.selectionRect.getX() * scaleX;
            double selY = canvas.selectionRect.getY() * scaleY;
            double selW = canvas.selectionRect.getWidth() * scaleX;
            double selH = canvas.selectionRect.getHeight() * scaleY;

// clamp to image bounds
            int selXInt = Math.max(0, (int)Math.floor(selX));
            int selYInt = Math.max(0, (int)Math.floor(selY));
            int selWInt = Math.max(1, Math.min(imgW - selXInt, (int)Math.ceil(selW)));
            int selHInt = Math.max(1, Math.min(imgH - selYInt, (int)Math.ceil(selH)));

            java.awt.Rectangle selectionPixels = new java.awt.Rectangle(selXInt, selYInt, selWInt, selHInt);

            boolean isExcluded = canvas.exclusionMode.isSelected();

            BufferedImage filteredInput = new BufferedImage(imgW, imgH, original.getType());
            Graphics2D g = filteredInput.createGraphics();
            g.drawImage(original, 0, 0, null);
            g.dispose();

            BufferedImage filteredResult = blurOnce(filteredInput);
            for (int i = 1; i < iterations-1; i++) {
                filteredResult = blurOnce(filteredResult);
            }

            for (int y = 0; y < imgH; y++) {
                for (int x = 0; x < imgW; x++) {
                    boolean isInside = selectionPixels.contains(x, y);
                    if ((!isExcluded && isInside) || (isExcluded && !isInside)) {
                        original.setRGB(x, y, filteredResult.getRGB(x, y));
                    }
                }
            }

            canvas.selectedImage.setImage(SwingFXUtils.toFXImage(original, null));

        }



    }

    private BufferedImage blurOnce(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        int[][] weight = {
                {1, 2, 1},
                {2, 4, 2},
                {1, 2, 1}
        };

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                int sumR = 0, sumG = 0, sumB = 0;
                int weightSum = 0;

                for (int ky = -1; ky <= 1; ky++) {
                    for (int kx = -1; kx <= 1; kx++) {
                        int nx = x + kx;
                        int ny = y + ky;

                        // check bounds for edges
                        if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                            int rgb = image.getRGB(nx, ny);
                            int r = (rgb >> 16) & 0xff;
                            int g = (rgb >> 8) & 0xff;
                            int b = rgb & 0xff;

                            int w = weight[ky + 1][kx + 1];

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
                output.setRGB(x, y, blurredRGB);
            }
        }

        return output;

    }



    @FXML
    public void initialize() {
    }


}