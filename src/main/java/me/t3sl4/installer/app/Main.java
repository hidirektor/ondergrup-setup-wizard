package me.t3sl4.installer.app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Screen;
import javafx.stage.Stage;
import me.t3sl4.installer.utils.file.FileUtil;
import me.t3sl4.installer.utils.system.Definitions;
import me.t3sl4.installer.utils.ui.SceneUtil;
import me.t3sl4.util.os.OSUtil;

import java.io.IOException;
import java.util.List;

public class Main extends Application {
    List<Screen> screens = Screen.getScreens();

    public static Screen defaultScreen;

    @Override
    public void start(Stage primaryStage) throws IOException {
        FileUtil.criticalFileSystem();

        Platform.setImplicitExit(false);

        defaultScreen = screens.get(0);
        SceneUtil.openMainScreen(screens.get(0));

        OSUtil.updateLocalVersion(Definitions.PREF_NODE_NAME, Definitions.PREF_SETUP_WIZARD_KEY, Definitions.getVersion());

        System.out.println("Önder Grup Updater servisi başlatıldı.");
    }

    public static void main(String[] args) {
        launch(args);
    }
}