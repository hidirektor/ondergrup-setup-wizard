module me.t3sl4.installer {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires mslinks;
    requires java.desktop;


    opens me.t3sl4.installer to javafx.fxml;
    exports me.t3sl4.installer;
    opens me.t3sl4.installer.controller to javafx.fxml;
    exports me.t3sl4.installer.controller;
    opens me.t3sl4.installer.app to javafx.fxml;
    exports me.t3sl4.installer.app;
}