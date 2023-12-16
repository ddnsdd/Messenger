package com.example.messandger2;
/**
 * @author SlavaDudin
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("lobby.fxml"));
        Parent root = loader.load();
        LobbyController lobbyController = loader.getController();

        lobbyController.setLobbyStage(primaryStage);

        primaryStage.setTitle("Lobby");
        primaryStage.setScene(new Scene(root, 300, 200));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}




