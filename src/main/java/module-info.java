module org.aut.polylinked_client {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires com.jfoenix;
    requires org.json;
    requires org.jetbrains.annotations;
    requires java.net.http;
    requires GNAvatarView;


    opens org.aut.polylinked_client.control to javafx.fxml;
    exports org.aut.polylinked_client;
    exports org.aut.polylinked_client.utils;
}