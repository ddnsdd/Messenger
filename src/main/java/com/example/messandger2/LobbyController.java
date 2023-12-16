package com.example.messandger2;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Optional;

public class LobbyController {
    private static final Logger lobbyLoger = LogManager.getLogger(ChatController.class.getName());

    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private String username;
    private boolean isIdentified = false;
    private String lobbyName;

    @FXML
    private ComboBox<String> lobbyComboBox;

    @FXML
    private Button joinButton;

    private Stage lobbyStage; // Добавим поле для хранения ссылки на окно выбора лобби

    /**
     * Функция инициализации графической состовляющей чата
     */
    @FXML
    void initialize() {
        if (checkServerConnection()) {
            showLoginWindow();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Отсутствует подключение к серверу");
            alert.setTitle("Ошибка подключения");
            alert.showAndWait();
            Platform.exit();
        }
        lobbyComboBox.getSelectionModel().selectFirst();

        joinButton.setOnAction(event -> {
            String selectedLobby = lobbyComboBox.getSelectionModel().getSelectedItem();
            if (selectedLobby != null) {
                if (!isIdentified) {
                    if (connectToServer()) {
                        connectToLobby(selectedLobby);
                        openChatWindow();
                    }
                } else {
                    connectToLobby(selectedLobby);
                    openChatWindow();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Выберите лобби");
                alert.setTitle("Ошибка");
                alert.setHeaderText("Ошибка выбора лобби");
                alert.showAndWait();
            }
        });

        List<String> lobbyData = fetchLobbyDataFromServer();
        if (lobbyData != null) {
            ObservableList<String> observableList = FXCollections.observableArrayList(lobbyData);
            lobbyComboBox.setItems(observableList);
        }
    }

    /**
     * Функция проверки подключения к серверу
     */
    private boolean checkServerConnection() {
        try {
            Socket serverCheck = new Socket("127.0.0.1", 9999);
            serverCheck.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    /**
     * Функция подключения к серверу
     */
    private boolean connectToServer() {
        try {
            client = new Socket("127.0.0.1", 9999);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);

            out.println("IDENTIFY:" + username);
            isIdentified = true;
            lobbyLoger.info(username + " подключился к серверу");
            return true;
        } catch (IOException e) {
            showErrorDialog("Connection Error", "Connection Failed", "Failed to connect to the server. Please try again later.");
            e.printStackTrace();
            return false;
        }
    }
    /**
     * Функция подключения к лобби
     */
    private void connectToLobby(String lobbyName) {
        try {
            out.println("JOIN_LOBBY:" + lobbyName);
            this.lobbyName = lobbyName;
            lobbyLoger.info(username + " присоединился к " + lobbyName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Функция вывода окна ошибки
     */
    private void showErrorDialog(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    /**
     * Функция получения списка лобби от сервера
     */
    private List<String> fetchLobbyDataFromServer() {
        try {
            Socket server = new Socket("127.0.0.1", 9999);
            BufferedReader serverIn = new BufferedReader(new InputStreamReader(server.getInputStream()));
            PrintWriter serverOut = new PrintWriter(server.getOutputStream(), true);

            serverOut.println("GET_LOBBY_LIST");

            String response = serverIn.readLine();

            if (response != null) {
                String[] lobbyList = response.split(",");
                return List.of(lobbyList);
            }

            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Функция отображения окна идентификации клиента
     */
    private void showLoginWindow() {
        Platform.runLater(() -> {
            TextInputDialog loginDialog = new TextInputDialog();
            loginDialog.setTitle("Login");
            loginDialog.setHeaderText("Enter your username");
            loginDialog.setContentText("Username:");

            Optional<String> result = loginDialog.showAndWait();
            if (result.isPresent() && !result.get().isEmpty()) {
                username = result.get();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Введите имя пользователя");
                alert.setTitle("Ошибка");
                alert.setHeaderText("Ошибка ввода");
                alert.showAndWait();
                showLoginWindow();
            }
        });
    }

    /**
     * Функция отображения окна чата
     */
    private void openChatWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            Parent root = loader.load();

            ChatController chatController = loader.getController();
            chatController.initialize(client, lobbyName, username);

            Scene chatScene = new Scene(root);

            Stage chatStage = new Stage();
            chatStage.setScene(chatScene);
            chatStage.setTitle("Chat");
            chatStage.show();

            // Закрываем окно выбора лобби
            if (lobbyStage != null) {
                lobbyStage.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Создание ссылки на окно выбора лобби для HelloApplication
     */
    public void setLobbyStage(Stage lobbyStage) {
        this.lobbyStage = lobbyStage;
    }
}
