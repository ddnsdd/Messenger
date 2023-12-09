module com.example.messandger2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.messandger2 to javafx.fxml;
    exports com.example.messandger2;
}