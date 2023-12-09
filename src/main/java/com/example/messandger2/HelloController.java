package com.example.messandger2;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;


class Client implements Runnable {
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private boolean done;


    @Override
    public void run() {
        try{
            client = new Socket("127.0.0.1",9999);
            out = new PrintWriter(client.getOutputStream(),true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            InputHandler inHandler = new InputHandler();
            Thread t = new Thread(inHandler);
            t.start();

            String inMessege;
            while ((inMessege = in.readLine()) != null){
                System.out.println(inMessege);

            }
        }catch (IOException e){
            shutdown();

        }
    }
    public void shutdown(){
        done = true;
        try {
            in.close();
            out.close();
            if (!client.isClosed()) {
                client.close();
            }

        }catch (IOException e){
            //ignore
        }
    }

    class InputHandler implements Runnable{
        @Override
        public void run() {
            try {
                BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
                while(!done){
                    String messege = inReader.readLine();
                    if(messege.equals("/quit")){
                        out.println(messege);
                        inReader.close();
                        shutdown();

                    }else{
                        out.println(messege);

                    }
                }
            }catch (IOException e){
                shutdown();

            }

        }
    }


    public class HelloController {

        @FXML
        private ResourceBundle resources;

        @FXML
        private URL location;

        @FXML
        private TextField etext;

        @FXML
        private VBox messeges;

        @FXML
        private Button send;

        @FXML
        private ListView<?> users;

        @FXML
        void initialize() {
            send.setOnAction(actionEvent -> {

            });

        }
    }
}
