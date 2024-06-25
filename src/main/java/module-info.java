module org.aut.polylinked_client {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens org.aut.polylinked_client to javafx.fxml;
    exports org.aut.polylinked_client;
}