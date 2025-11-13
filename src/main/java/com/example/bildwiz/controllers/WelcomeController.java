package com.example.bildwiz.controllers;

import com.example.bildwiz.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;


import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;


import static javafx.application.Application.launch;

public class WelcomeController {


    @FXML
    private Label welcomeText;

    @FXML
    private Button startButton;

    @FXML
    protected void onStartButtonClick() throws IOException {

        Stage stage = (Stage)  startButton.getScene().getWindow();

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("fxml/dashboard.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Hello! World!");
        stage.setScene(scene);
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
