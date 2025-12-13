package com.example.bildwiz.controllers;

import com.example.bildwiz.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

public class CanvasController {

    @FXML
    TextField imageURL;
    @FXML
    private Button selectImageButton;
    @FXML
    private Button saveImageButton;

    @FXML Button applyFilterButton;

    @FXML
    public StackPane imageContainer;
    @FXML
    public ImageView selectedImage;

    @FXML
    public ToggleButton selectionMode;
    @FXML
    public Rectangle selectionRect = new Rectangle();
    private double anchorX;
    private double anchorY;



    @FXML
    private Button undoButton;

    protected FilterController currentFilterController;

    public boolean isImageLoaded = false;

    public ArrayList<Image> imageVersions = new ArrayList<>();


    @FXML
    public void initialize() {

        selectionRect.setStroke(Color.BLUE);
        selectionRect.setFill(Color.color(0, 0, 1, 0.2));
        selectionRect.setVisible(false);

        Group imageGroup = new Group(selectedImage, selectionRect);

        // imageContainer comes from FXML (StackPane)
        imageContainer.getChildren().add(imageGroup);

        imageContainer.layoutBoundsProperty().addListener((obs, old, bounds) -> {
            imageContainer.setClip(
                    new Rectangle(bounds.getWidth(), bounds.getHeight())
            );
        });

        imageURL.textProperty().addListener((obs, oldVal, newVal) -> {
            LoadImageFromURL(newVal, selectedImage);
        });

        imageContainer.setOnScroll(event -> {
            double zoom = event.getDeltaY() > 0 ? 1.05 : 1 / 1.05;

            imageGroup.setScaleX(imageGroup.getScaleX() * zoom);
            imageGroup.setScaleY(imageGroup.getScaleY() * zoom);
        });

        final double[] dragDelta = new double[2];
        final double[] anchor = new double[2];

        imageContainer.setOnMousePressed(e -> {

            Point2D p = imageGroup.sceneToLocal(e.getSceneX(), e.getSceneY());

            if (selectionMode.isSelected()) {
                anchor[0] = p.getX();
                anchor[1] = p.getY();

                selectionRect.setX(anchor[0]);
                selectionRect.setY(anchor[1]);
                selectionRect.setWidth(0);
                selectionRect.setHeight(0);
                selectionRect.setVisible(true);
            } else {
                dragDelta[0] = e.getSceneX();
                dragDelta[1] = e.getSceneY();
            }
        });

        imageContainer.setOnMouseDragged(e -> {

            Point2D p = imageGroup.sceneToLocal(e.getSceneX(), e.getSceneY());

            if (selectionMode.isSelected()) {

                double x = Math.min(anchor[0], p.getX());
                double y = Math.min(anchor[1], p.getY());
                double w = Math.abs(p.getX() - anchor[0]);
                double h = Math.abs(p.getY() - anchor[1]);

                selectionRect.setX(x);
                selectionRect.setY(y);
                selectionRect.setWidth(w);
                selectionRect.setHeight(h);

            } else {
                double dx = e.getSceneX() - dragDelta[0];
                double dy = e.getSceneY() - dragDelta[1];

                imageGroup.setTranslateX(imageGroup.getTranslateX() + dx);
                imageGroup.setTranslateY(imageGroup.getTranslateY() + dy);

                dragDelta[0] = e.getSceneX();
                dragDelta[1] = e.getSceneY();
            }
        });
    }



    private void LoadImageFromURL(String urlString, ImageView imageView) {

        if (urlString.isEmpty()) {
            return;
        }

        try {
            URL url = new URL(urlString);
            URI uri = new URI(
                    url.getProtocol(),
                    url.getUserInfo(),
                    url.getHost(),
                    url.getPort(),
                    url.getPath(),
                    url.getQuery(),
                    url.getRef()
            );


            HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();


            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);


            String fileType = connection.getContentType();


            if (fileType != null && fileType.startsWith("image")) {

                Image image = new Image(uri.toString(), true);
                imageView.setImage(image);

                onImageLoaded();

            } else {
                System.out.println("Not a valid image URL");
            }

        } catch (Exception e) {
            System.out.println("Invalid URL or cannot load image");
        }

    }


    @FXML
    public void OnSelectImageButtonClick() {

        Stage stage = (Stage) selectImageButton.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Image");
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            Image img = new Image(file.toURI().toString());
            selectedImage.setImage(img);

            onImageLoaded();

        }
    }


    @FXML
    public void OnSaveImageButtonClick() {

    }

    @FXML
    public void OnApplyFilterButtonClick() {
        if (currentFilterController != null) {


            currentFilterController.applyFilter();

            imageVersions.add(selectedImage.getImage());
        }
    }

    @FXML
    public void OnBackButtonClick() throws IOException {
        Stage stage = (Stage) selectImageButton.getScene().getWindow();

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("fxml/dashboard.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Hello! World!");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();

    }


    @FXML
    VBox rightPanel;

    public void openFilter(String fxmlPath) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/bildwiz/fxml/" + fxmlPath));
            Node ui = loader.load();

            rightPanel.getChildren().clear();
            rightPanel.getChildren().add(ui);


            Object controller = loader.getController();

            try {
                controller.getClass().getMethod("setParent", CanvasController.class).invoke(controller, this);

                if (controller instanceof FilterController) {
                    currentFilterController = (FilterController) controller;
                }

            } catch (NoSuchMethodException ignored) {
                System.out.println("nosuchmethod");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    void OnUndoButtonClick() {
        if (imageVersions.size() > 1) {
            selectedImage.setImage(imageVersions.get(imageVersions.size() - 2));

            imageVersions.remove(imageVersions.size() - 1);
        }
    }

    @FXML
    AnchorPane initialControls;
    @FXML
    HBox toolbar;

    @FXML
    void onImageLoaded() {
        imageContainer.getChildren().remove(initialControls);

        toolbar.getChildren().addAll(selectImageButton, imageURL);

        HBox.setMargin(selectedImage, new Insets(0, 0, 0, 310.00));

        toolbar.setSpacing(10);

        undoButton.setDisable(false);

        imageVersions.add(selectedImage.getImage());

    }
}
