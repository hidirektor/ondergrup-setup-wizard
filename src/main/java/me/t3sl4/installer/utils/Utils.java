package me.t3sl4.installer.utils;

import javafx.application.Platform;
import me.t3sl4.installer.utils.system.Definitions;
import me.t3sl4.util.file.DirectoryUtil;

import java.io.IOException;

public class Utils {

    public static void systemShutdown() {
        Platform.exit();

        System.exit(0);
    }

    public static void criticalFileSystem() throws IOException {
        // İşletim sistemine göre dosya yollarını ayarla
        String userHome = System.getProperty("user.name");
        String os = System.getProperty("os.name").toLowerCase();
        String basePath;
        String programName;

        if (os.contains("win")) {
            basePath = "C:/Users/" + userHome + "/";
            programName = "windows_Hydraulic.exe";
        } else {
            basePath = "/Users/" + userHome + "/";
            programName = "unix_Hydraulic.jar";
        }

        // Dosya yollarını belirle
        Definitions.mainPath = basePath + "OnderGrup/";

        DirectoryUtil.createDirectory(Definitions.mainPath);
    }
}
