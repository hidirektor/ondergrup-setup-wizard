package me.t3sl4.installer.utils;

import javafx.application.Platform;

public class GeneralUtil {

    public static void systemShutdown() {
        Platform.exit();

        System.exit(0);
    }
}
