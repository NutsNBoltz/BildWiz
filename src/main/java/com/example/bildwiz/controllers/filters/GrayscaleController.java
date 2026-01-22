package com.example.bildwiz.controllers.filters;

import com.example.bildwiz.controllers.CanvasController;
import com.example.bildwiz.controllers.FilterController;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.stream.IntStream;

public class GrayscaleController extends CanvasController implements FilterController {

    private CanvasController canvas;

    public void setParent(CanvasController controller) {
        this.canvas = controller;
    }




    @FXML
    public void initialize() {}

    @Override
    public void applyFilter() {

        Image image = canvas.selectedImage.getImage();
        BufferedImage bufferedImg = SwingFXUtils.fromFXImage(image, null);

        if (canvas.selectionMode.isSelected()) {



            BufferedImage filteredImage = FilterController.applyWithSelection(
                    canvas.selectedImage,
                    canvas.selectionRect,
                    canvas.exclusionMode.isSelected(),
                    input -> {
                        BufferedImage result = applyGrayscale(input);

                        return result;
                    });

            canvas.selectedImage.setImage(SwingFXUtils.toFXImage(filteredImage, null));

        } else {
            canvas.selectedImage.setImage(SwingFXUtils.toFXImage(applyGrayscale(bufferedImg), null));
        }

    }

    private BufferedImage applyGrayscale (BufferedImage inputImage) {

        DataBufferInt buffer = (DataBufferInt) inputImage.getRaster().getDataBuffer();
         int[] pixels = buffer.getData();

         int height = inputImage.getHeight();
         int width = inputImage.getWidth();

        int[] outputArray = new int[height * width];


        IntStream.range(0, height).parallel().forEach(y -> {
            for (int x = 0; x < width; x++) {

                int a = (pixels[y * width + x] >> 24) & 0xff;
                int r = (pixels[y * width + x] >> 16) & 0xff;
                int g = (pixels[y * width + x] >> 8) & 0xff;
                int b = (pixels[y * width + x]) & 0xff;

                int gray = (int)(0.299 * r + 0.587 * g + 0.114 * b);

                outputArray[y * width + x] = (a << 24) | (gray << 16) | (gray << 8) | gray;;

            }
        });




            return arrayToBuffered(outputArray,width, height);
    }

}
