package com.example.mes;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class HelloController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane background;

    @FXML
    private TextField etext;

    @FXML
    private TextArea messages;

    @FXML
    private Button send;

    @FXML
    private ListView<?> users;

    @FXML
    void initialize() {
        assert background != null : "fx:id=\"background\" was not injected: check your FXML file 'hello-view.fxml'.";
        assert etext != null : "fx:id=\"etext\" was not injected: check your FXML file 'hello-view.fxml'.";
        assert messages != null : "fx:id=\"messages\" was not injected: check your FXML file 'hello-view.fxml'.";
        assert send != null : "fx:id=\"send\" was not injected: check your FXML file 'hello-view.fxml'.";
        assert users != null : "fx:id=\"users\" was not injected: check your FXML file 'hello-view.fxml'.";

    }

}
