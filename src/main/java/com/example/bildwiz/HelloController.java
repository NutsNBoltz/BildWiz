package com.example.bildwiz;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.stage.Stage;


import java.awt.Desktop;
import java.net.URI;
import java.net.URL;


import java.io.IOException;

import static javafx.application.Application.launch;

public class HelloController {

    boolean isOn = false;

    @FXML
    private Label welcomeText;

    @FXML
    private Button helloButton;

    @FXML
    protected void onHelloButtonClick(){

        if(!isOn){
            welcomeText.setText("Welcome to JavaFX Application!");
            helloButton.setText("Bye");
            isOn = true;
        } else {
            welcomeText.setText("Unwelcome to JavaFX Application!");
            helloButton.setText("Hello");
            isOn = false;
        }


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
