package com.example.messandger2;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static final int PORT = 9999;
    private static final Map<String, List<ClientHandler>> lobbyClients = new ConcurrentHashMap<>();
    private static final List<String> lobbyList = new ArrayList<>();

    public static void main(String[] args) {
        lobbyList.add("Lobby 1");
        lobbyList.add("Lobby 2");
        lobbyList.add("Lobby 3");

        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server is running on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler extends Thread {
        private Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;
        private String username;
        private String lobbyName;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }
        /**
         * Функция обработки сообщений и комманд от клиента
         */
        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                String message;
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("IDENTIFY:")) {
                        username = message.substring(9);
                    } else if (message.equals("GET_LOBBY_LIST")) {
                        sendLobbyList();
                    } else if (message.startsWith("JOIN_LOBBY:")) {
                        lobbyName = message.substring(11);
                        joinLobby(lobbyName);
                    } else if (message.startsWith("SEND_MESSAGE:")) {
                        String[] parts = message.split(":", 4);
                        if (parts.length == 4) {
                            String targetLobby = parts[1];
                            String sender = parts[2];
                            String messageText = parts[3];
                            sendMessageToLobby(targetLobby, sender, messageText);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                    out.close();
                    clientSocket.close();
                    leaveLobby();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        /**
         * Функция отправки списка лобби клиенту
         */

        private void sendLobbyList() {
            StringBuilder lobbyListStr = new StringBuilder();
            for (String lobby : lobbyList) {
                lobbyListStr.append(lobby).append(",");
            }
            if (lobbyListStr.length() > 0) {
                lobbyListStr.deleteCharAt(lobbyListStr.length() - 1);
            }
            out.println(lobbyListStr);
        }

        /**
         * Функция присоединения клиента к лобби
         */
        private void joinLobby(String lobbyName) {
            lobbyClients.computeIfAbsent(lobbyName, k -> new ArrayList<>()).add(this);
            sendLobbyMessage("Server", username + " has joined the lobby.");
        }

        /**
         * Функция отключения клиента от лобби
         */
        private void leaveLobby() {
            if (lobbyName != null && lobbyClients.containsKey(lobbyName)) {
                lobbyClients.get(lobbyName).remove(this);
                sendLobbyMessage("Server", username + " has left the lobby.");

            }
        }

        /**
         * Функция обработки сообщений клиента
         */

        private void sendMessageToLobby(String targetLobby, String sender, String message) {
            if (lobbyClients.containsKey(targetLobby)) {
                for (ClientHandler client : lobbyClients.get(targetLobby)) {
                    client.out.println(sender + ": " + message);
                }
            }
        }
        /**
         *Функция отправки соббщений от сервера
         */
        private void sendLobbyMessage(String sender, String message) {
            sendMessageToLobby(lobbyName, sender, message);
        }
    }
}
