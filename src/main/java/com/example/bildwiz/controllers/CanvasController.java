package com.example.bildwiz.controllers;

import com.example.bildwiz.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class CanvasController {

    @FXML AnchorPane root;
    @FXML TextField imageURL;
    @FXML ImageView selectedImage;
    @FXML Button selectImageButton;
    @FXML Button changeImageButton;
    @FXML Button saveImageButton;


    @FXML public void initialize() {
        imageURL.textProperty().addListener((obs, oldVal, newVal) -> {
            LoadImageFromURL(newVal, selectedImage);
        });


        selectedImage.setOnScroll(event -> {
            double zoom = 1.05;

            if (event.getDeltaY() < 0) {zoom = 1/zoom;}

            selectedImage.setScaleX(selectedImage.getScaleX() * zoom);
            selectedImage.setScaleY(selectedImage.getScaleY() * zoom);
        });

        final double[] offset = new double[2];

        selectedImage.setOnMousePressed(event -> {
            offset[0] = event.getSceneX() - selectedImage.getLayoutX();
            offset[1] = event.getSceneY() - selectedImage.getLayoutY();
        });

        selectedImage.setOnMouseDragged(event -> {
            selectedImage.setLayoutX(event.getSceneX() - offset[0]);
            selectedImage.setLayoutY(event.getSceneY() - offset[1]);
        });

        Group group = new Group(selectedImage);
        root.getChildren().add(group);




    }


    private void LoadImageFromURL(String urlString, ImageView imageView ) {

        if (urlString.isEmpty()) {
            return;
        }

        try  {
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
                imageURL.setVisible(false);
                changeImageButton.setVisible(true);
                saveImageButton.setVisible(true);
                selectImageButton.setVisible(false);
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
            Image img =  new Image(file.toURI().toString());
            selectedImage.setImage(img);

            changeImageButton.setVisible(true);
            saveImageButton.setVisible(true);
            imageURL.setVisible(false);
        }
    }

    @FXML
    public void OnChangeImageButtonClick() {
        OnSelectImageButtonClick();
    }


    @FXML
    public void OnSaveImageButtonClick() {

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



    @FXML VBox rightPanel;

    public void openFilter(String fxmlPath) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node ui = loader.load();

            rightPanel.getChildren().clear();
            rightPanel.getChildren().add(ui);


            Object controller = loader.getController();

            try {
                controller.getClass().getMethod("setParent", CanvasController.class).invoke(controller, this);

            } catch (NoSuchMethodException ignored) {
                //ignore
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
