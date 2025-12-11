package com.example.bildwiz.controllers.filters;

import com.example.bildwiz.controllers.CanvasController;
import com.example.bildwiz.controllers.FilterController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Slider;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.awt.image.BufferedImage;
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

            Rectangle2D selectionArea = new Rectangle2D(
                    selectionRect.getX(),
                    selectionRect.getY(),
                    selectionRect.getWidth(),
                    selectionRect.getHeight()
            );


            PixelReader reader = selectedImage.getImage().getPixelReader();

            BufferedImage cropped = new BufferedImage(
                    (int) selectionArea.getWidth(),
                    (int) selectionArea.getHeight(),
                    BufferedImage.TYPE_INT_ARGB
            );

            for (int y = 0; y < selectionArea.getHeight(); y++) {
                for (int x = 0; x < selectionArea.getWidth(); x++) {

                    int fxX = (int) (selectionArea.getMinX() + x);
                    int fxY = (int) (selectionArea.getMinY() + y);

                    Color fxColor = reader.getColor(fxX, fxY);

                    int argb =
                            ((int)(fxColor.getOpacity() * 255) << 24) |
                                    ((int)(fxColor.getRed()     * 255) << 16) |
                                    ((int)(fxColor.getGreen()   * 255) << 8 ) |
                                    ((int)(fxColor.getBlue()    * 255));

                    cropped.setRGB(x, y, argb);
                }
            }


            for (int i = 0; i < iterations; i++) {
                cropped = blurOnce(cropped);
            }

            WritableImage croppedFx = new WritableImage(
                    cropped.getWidth(),
                    cropped.getHeight()
            );

            PixelWriter cw = croppedFx.getPixelWriter();

            for (int y = 0; y < cropped.getHeight(); y++) {
                for (int x = 0; x < cropped.getWidth(); x++) {

                    int argb = cropped.getRGB(x, y);

                    Color fxColor = Color.rgb(
                            (argb >> 16) & 0xFF,
                            (argb >> 8 ) & 0xFF,
                            (argb      ) & 0xFF,
                            ((argb >> 24) & 0xFF) / 255.0
                    );

                    cw.setColor(x, y, fxColor);
                }
            }

            WritableImage wImage = new WritableImage(
                    selectedImage.getImage().getPixelReader(),
                    (int) selectedImage.getImage().getWidth(),
                    (int) selectedImage.getImage().getHeight()
            );

            PixelWriter writer = wImage.getPixelWriter();

            writer.setPixels(
                    (int) selectionArea.getMinX(),
                    (int) selectionArea.getMinY(),
                    (int) croppedFx.getWidth(),
                    (int) croppedFx.getHeight(),
                    croppedFx.getPixelReader(),
                    0,
                    0
            );

            selectedImage.setImage(wImage);


        }



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