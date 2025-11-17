package com.example.bildwiz.controllers;


import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;


public class DashboardController {

    @FXML
    private FlowPane cardsContainer;



    Card card1 = new Card("Monochrome",
            "A filter, which transforms your colorful image to Black & White",
            null,
            "/fxml/blackAndWhite.fxml");

    Card card2 = new Card(
            "Pop Art Filter",
            "A filter which transforms your picture pop art filter",
            null,
            "/fxml/popArt.fxml");

    Card card3 = new Card(
            "ASCII",
            "A Filter which transforms your image into ASCII Art",
            null,
            "/fxml/ascii.fxml"
    );

    Card card4 = new Card(
            "Photomosaic",
            "A Filter which transforms your image into tiles of other smaller images",
            null,
            "/fxml/photomosaic.fxml"
    );

    Card card5 = new Card(
            "Voronoi",
            "A filter which applies the Voronoi effect to the image",
            null,
            "/fxml/voronoi.fxml"
    );

    Card card6 = new Card(
            "Dithering",
            "A Filter which transforms your image into ASCII Art",
            null,
            "/fxml/dithering.fxml"
    );

    Card card7 = new Card(
            "ASCII",
            "A Filter which transforms your image into ASCII Art",
            null,
            "/fxml/ascii.fxml"
    );

    public void initialize(){
        cardsContainer.getChildren().add(card1);
        cardsContainer.getChildren().add(card2);
        cardsContainer.getChildren().add(card3);
        cardsContainer.getChildren().add(card4);
        cardsContainer.getChildren().add(card5);
        cardsContainer.getChildren().add(card6);
        cardsContainer.getChildren().add(card7);

    }




    public static class Card extends StackPane {

        @FXML private ImageView imageView;
        @FXML private Label filterName;
        @FXML private Label filterDescription;

        public Card(String name, String description, Image image, String targetFXML) {

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/bildwiz/fxml/filterCard.fxml"));

            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);

            try {
                fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            init(name, description, image, targetFXML);
        }



        private void init(String name, String description, Image image, String targetFXML) {

            filterName.setText(name);
            imageView.setImage(image);
            filterDescription.setText(description);
            filterDescription.setOpacity(0); //This makes it transparent originally.

            this.setOnMouseEntered(e ->{
                this.setScaleX(1.05);
                this.setScaleY(1.05);
                FadeTransition fadeOut = new FadeTransition(Duration.millis(200), filterDescription);
                fadeOut.setToValue(1);
                fadeOut.play();
            });


            this.setOnMouseExited(e -> {
                this.setScaleX(1);
                this.setScaleY(1);
                FadeTransition fadeOut = new FadeTransition(Duration.millis(200), filterDescription);
                fadeOut.setToValue(0);
                fadeOut.play();
            });

            this.setOnMouseClicked(e -> {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(targetFXML));
                    Stage stage = (Stage) this.getScene().getWindow();

                    Parent filterUI = fxmlLoader.load();

                    stage.setScene(new Scene(filterUI));

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });


        }


    }
}
