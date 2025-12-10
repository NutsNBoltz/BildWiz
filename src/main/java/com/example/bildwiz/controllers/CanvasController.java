package com.example.bildwiz.controllers;

import com.example.bildwiz.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
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

    protected FilterController currentFilterController;

    public boolean isImageLoaded = false;

    public ArrayList<Image> imageVersions = new ArrayList<>();


    @FXML
    public void initialize() {


        imageContainer.layoutBoundsProperty().addListener((obs, old, bounds) -> {
            Rectangle clip = new Rectangle(bounds.getWidth(), bounds.getHeight());
            imageContainer.setClip(clip);
        });

        imageURL.textProperty().addListener((obs, oldVal, newVal) -> {
            LoadImageFromURL(newVal, selectedImage);
        });


        selectedImage.setOnScroll(event -> {
            double zoom = 1.05;

            if (event.getDeltaY() < 0) {
                zoom = 1 / zoom;
            }

            selectedImage.setScaleX(selectedImage.getScaleX() * zoom);
            selectedImage.setScaleY(selectedImage.getScaleY() * zoom);
        });


        Rectangle selectionRect = new Rectangle();
        selectionRect.setStroke(Color.BLUE);
        selectionRect.setFill(Color.color(0, 0, 1, 0.2));
        selectionRect.setVisible(false);
        imageContainer.getChildren().add(selectionRect);


        final double[] dragDelta = new double[2];
        selectedImage.setOnMousePressed(e -> {

            if (selectionMode.isSelected()) {
                selectionRect.setTranslateX(e.getX());
                selectionRect.setTranslateY(e.getY());
                selectionRect.setWidth(0);
                selectionRect.setHeight(0);
                selectionRect.setVisible(true);
            } else {
                dragDelta[0] = e.getSceneX();
                dragDelta[1] = e.getSceneY();
            }


        });


        selectedImage.setOnMouseDragged(e -> {

            if (selectionMode.isSelected()) {
                double width = e.getX() - selectionRect.getX();
                double height = e.getY() - selectionRect.getY();

                selectionRect.setWidth(Math.abs(width));
                selectionRect.setHeight(Math.abs(height));
                selectionRect.setX(width < 0 ? e.getX() : selectionRect.getX());
                selectionRect.setY(height < 0 ? e.getY() : selectionRect.getY());
            } else {
                double dx = e.getSceneX() - dragDelta[0];
                double dy = e.getSceneY() - dragDelta[1];

                selectedImage.setTranslateX(selectedImage.getTranslateX() + dx);
                selectedImage.setTranslateY(selectedImage.getTranslateY() + dy);

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
    AnchorPane initialControls;
    @FXML
    HBox toolbar;

    @FXML
    void onImageLoaded() {
        imageContainer.getChildren().remove(initialControls);

        toolbar.getChildren().addAll(selectImageButton, imageURL);

        HBox.setMargin(selectedImage, new Insets(0, 0, 0, 310.00));


        toolbar.setSpacing(10);

    }
}
