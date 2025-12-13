package com.example.bildwiz.controllers;

import com.example.bildwiz.controllers.filters.BufferedImageProcessor;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public interface FilterController{
    void applyFilter();


    public static BufferedImage applyWithSelection(
            ImageView imageView,
            Rectangle selectionRect,
            boolean isExcluded,
            BufferedImageProcessor processor
    ) {

        BufferedImage original =
                SwingFXUtils.fromFXImage(imageView.getImage(), null);

        int imgW = original.getWidth();
        int imgH = original.getHeight();

        double viewW = imageView.getBoundsInLocal().getWidth();
        double viewH = imageView.getBoundsInLocal().getHeight();

        double scaleX = imgW / viewW;
        double scaleY = imgH / viewH;

        int selX = (int) Math.floor(selectionRect.getX() * scaleX);
        int selY = (int) Math.floor(selectionRect.getY() * scaleY);
        int selW = (int) Math.ceil(selectionRect.getWidth() * scaleX);
        int selH = (int) Math.ceil(selectionRect.getHeight() * scaleY);

        selX = Math.max(0, selX);
        selY = Math.max(0, selY);
        selW = Math.max(1, Math.min(imgW - selX, selW));
        selH = Math.max(1, Math.min(imgH - selY, selH));

        Rectangle selectionPixels =
                new Rectangle(selX, selY, selW, selH);

        BufferedImage input = deepCopy(original);

        // 🔥 THE IMPORTANT PART
        BufferedImage processed = processor.process(input);

        for (int y = 0; y < imgH; y++) {
            for (int x = 0; x < imgW; x++) {
                boolean inside = selectionPixels.contains(x, y);

                if ((!isExcluded && inside) || (isExcluded && !inside)) {
                    original.setRGB(x, y, processed.getRGB(x, y));
                }
            }
        }

        imageView.setImage(SwingFXUtils.toFXImage(original, null));
        return original;
    }


    private static BufferedImage deepCopy(BufferedImage src) {
        BufferedImage copy = new BufferedImage(
                src.getWidth(),
                src.getHeight(),
                src.getType());
        Graphics2D g = copy.createGraphics();
        g.drawImage(src, 0, 0, null);
        g.dispose();
        return copy;
    }


}
