package com.example.bildwiz.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class CanvasController {


    @FXML TextField imageURL;
    @FXML ImageView selectedImage;
    @FXML Button selectImageButton;
    @FXML Button changeImageButton;
    @FXML Button saveImageButton;


    @FXML public void initialize() {
        imageURL.textProperty().addListener((obs, oldVal, newVal) -> {
            LoadImageFromURL(newVal, selectedImage);
        });
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
        }
    }

    @FXML
    public void OnChangeImageButtonClick() {
        OnSelectImageButtonClick();
    }


    @FXML
    public void OnSaveImageButtonClick() {}

}
