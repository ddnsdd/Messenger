package com.example.messandger2;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.*;
import java.net.Socket;

public class ChatController {
    private static final Logger logger = LogManager.getLogger(ChatController.class.getName());

    private Socket client;
    private String lobbyName;
    private String username;
    private PrintWriter out;

    @FXML
    private TextArea messages;

    @FXML
    private TextField etext;

    @FXML
    private Button send;


    /**
     * Функция инициализации конкретного клиента и лобби
     */
    public void initialize(Socket client, String lobbyName, String username) {
        this.client = client;
        this.lobbyName = lobbyName;
        this.username = username;

        if (messages != null && etext != null) {
            send.setOnAction(event -> {
                sendMessage();
            });

            try {
                out = new PrintWriter(client.getOutputStream(), true);
                out.println("ENTER_LOBBY:" + lobbyName);

                Thread receiverThread = new Thread(this::receiveMessages);
                receiverThread.setDaemon(true);
                receiverThread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Функция отправки сообщения
     */

    @FXML
    private void sendMessage() {
        if (etext != null) {
            String message = etext.getText();
            if (!message.isEmpty()) {
                out.println("SEND_MESSAGE:" + lobbyName + ":" + username + ":" + message);
                logger.info(username  + " отправил сообщение");
                // Очистим текстовое поле после отправки сообщения
                etext.clear();
            } else {
                logger.error(username + " попытался отправить пустое сообщение");
                Alert alert = new Alert(Alert.AlertType.ERROR, "Введите сообщение");
                alert.setTitle("Ошибка");
                alert.setHeaderText("Ошибка ввода");
                alert.showAndWait();
            }
        }
    }

    /**
     * Функция получения и отображения сообщения в чате от других пользователей и сервера
     */

    private void receiveMessages() {
        if (messages != null) {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String message;
                while ((message = in.readLine()) != null) {
                    String finalMessage = message;
                    Platform.runLater(() -> {
                        messages.appendText(finalMessage + "\n");
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
