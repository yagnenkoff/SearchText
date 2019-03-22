package com.yagnenkoff.prj1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Form.fxml"));
        primaryStage.setTitle("SearchText");
        primaryStage.setScene(new Scene(root, 600,460));
        primaryStage.show();
    }
}
