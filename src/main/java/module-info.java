module com.example.messandger2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.logging.log4j;


    opens com.example.messandger2 to javafx.fxml;
    exports com.example.messandger2;
}