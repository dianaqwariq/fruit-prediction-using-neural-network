package com.example.fruit_rd;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
public class HelloController {
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private ImageView image;

    @FXML
    private Button start;
    
    @FXML
    void ShowTheResult(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("playing.fxml"));
        stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


    

    @FXML
    private void OnStart(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("playing.fxml"));
        stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }



    }


