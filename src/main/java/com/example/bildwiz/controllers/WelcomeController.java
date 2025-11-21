package com.example.bildwiz.controllers;

import com.example.bildwiz.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;


import static javafx.application.Application.launch;

public class WelcomeController {


    @FXML
    private Label welcomeText;

    @FXML
    private Button startButton;


    @FXML
    private ImageView welcomePageImage;




    @FXML
    protected void onStartButtonClick() throws IOException {

        Stage stage = (Stage)  startButton.getScene().getWindow();


        //to be shifted to CanvasController, mechanism for selecting an image from computer.
//        FileChooser fileChooser = new FileChooser();
//        fileChooser.setTitle("Choose Image");
//        File file = fileChooser.showOpenDialog(stage);
//
//        if (file != null) {
//            Image img =  new Image(file.toURI().toString());
//            welcomePageImage.setImage(img);
//        }


        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("fxml/dashboard.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Hello! World!");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();

    }


    @FXML
    protected void onTutorialButtonClick(){

        new Thread(()->{
            try {

                Desktop.getDesktop().browse(new URI("https://www.youtube.com/"));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }
}
