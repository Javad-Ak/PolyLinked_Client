module org.aut.polylinked_client {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires com.jfoenix;
    requires org.json;
    requires org.jetbrains.annotations;


    opens org.aut.polylinked_client to javafx.fxml;
    exports org.aut.polylinked_client;
}