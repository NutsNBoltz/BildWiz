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


public class DashboardController {

    @FXML
    private FlowPane cardsContainer;



    Card card1 = new Card("Monochrome",
            "A filter, which transforms your colorful image to Black & White",
            "tbd.png",
            "grayscale.fxml");

    Card card12 = new Card("Blur",
            "A filter, which will blur the image",
            "blur.png",
            "blur.fxml");

    Card card2 = new Card(
            "Pop Art Filter",
            "A filter which transforms your picture pop art filter",
            "tbd.png",
            "popArt.fxml");

    Card card3 = new Card(
            "ASCII",
            "A Filter which transforms your image into ASCII Art",
            "tbd.png",
            "ascii.fxml"
    );

    Card card4 = new Card(
            "Photomosaic",
            "A Filter which transforms your image into tiles of other smaller images",
            "tbd.png",
            "photomosaic.fxml"
    );

    Card card5 = new Card(
            "Voronoi",
            "A filter which applies the Voronoi effect to the image",
            "tbd.png",
            "voronoi.fxml"
    );

    Card card6 = new Card(
            "Dithering",
            "A Filter which transforms your image into ASCII Art",
            "tbd.png",
            "dithering.fxml"
    );

    Card card7 = new Card(
            "ASCII",
            "A Filter which transforms your image into ASCII Art",
            "tbd.png",
            "ascii.fxml"
    );

    Card card8 = new Card(
            "Testing",
            "Testing Canvas, to make sure it is functioning as intended.",
            "tbd.png",
            "canvas.fxml"
    );

    public void initialize(){
        cardsContainer.getChildren().add(card1);
        cardsContainer.getChildren().add(card12);
        cardsContainer.getChildren().add(card2);
        cardsContainer.getChildren().add(card3);
        cardsContainer.getChildren().add(card4);
        cardsContainer.getChildren().add(card5);
        cardsContainer.getChildren().add(card6);
        cardsContainer.getChildren().add(card7);
        cardsContainer.getChildren().add(card8);

    }




    public static class Card extends StackPane {

        @FXML private ImageView imageView;
        @FXML private Label filterName;
        @FXML private Label filterDescription;

        public Card(String name, String description, String imagePath, String targetFXML) {

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/bildwiz/fxml/filterCard.fxml"));

            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);

            try {
                fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            init(name, description, imagePath, targetFXML);
        }



        private void init(String name, String description, String imagePath, String targetFXML) {

            filterName.setText(name);

            filterDescription.setText(description);
            filterDescription.setOpacity(0); //This makes the description transparent originally.


            Image image = new Image(getClass().getResourceAsStream("/com/example/bildwiz/images/" + imagePath));
            imageView.setImage(image);
imageView.setSmooth(false);

            this.setOnMouseEntered(e ->{
                filterName.setVisible(true);
                FadeTransition fadeOut = new FadeTransition(Duration.millis(500), filterDescription);
                fadeOut.setToValue(1);
                fadeOut.play();
            });


            this.setOnMouseExited(e -> {
                filterName.setVisible(false);
                FadeTransition fadeOut = new FadeTransition(Duration.millis(500), filterDescription);
                fadeOut.setToValue(0);
                fadeOut.play();
            });

            this.setOnMouseClicked(e -> {
                try {

                    FXMLLoader canvasLoader = new FXMLLoader(getClass().getResource("/com/example/bildwiz/fxml/canvas.fxml"));
                    Parent canvasParent = canvasLoader.load();

                    CanvasController canvas = canvasLoader.getController();


                    canvas.openFilter(targetFXML);

                    Stage stage = (Stage) this.getScene().getWindow();
                    stage.setScene(new Scene(canvasParent));

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });


        }


    }
}
