package me.t3sl4.installer.utils;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import me.t3sl4.installer.Launcher;

import java.io.IOException;
import java.util.Objects;

public class SceneUtil {

    public static double x, y;

    public static void changeScreen(String newSceneSource, Screen currentScreen) throws IOException {
        Stage primaryStage = new Stage();
        Parent root = FXMLLoader.load(Objects.requireNonNull(Launcher.class.getResource(newSceneSource)));

        primaryStage.setScene(new Scene(root));
        primaryStage.initStyle(StageStyle.UNDECORATED);

        primaryStage.setTitle("Önder Grup Setup Wizard " + SystemVariables.getVersion());
        Image icon = new Image(Objects.requireNonNull(Launcher.class.getResourceAsStream("/assets/images/logo.png")));
        primaryStage.getIcons().add(icon);

        Rectangle clip = new Rectangle();
        clip.setWidth(850);
        clip.setHeight(670);
        clip.setArcWidth(30);
        clip.setArcHeight(30);
        root.setClip(clip);

        Rectangle2D bounds = currentScreen.getVisualBounds();

        primaryStage.setOnShown(event -> {
            double stageWidth = primaryStage.getWidth();
            double stageHeight = primaryStage.getHeight();

            double centerX = bounds.getMinX() + (bounds.getWidth() - stageWidth) / 2;
            double centerY = bounds.getMinY() + (bounds.getHeight() - stageHeight) / 2;

            primaryStage.setX(centerX);
            primaryStage.setY(centerY);
        });

        root.setOnMousePressed(event -> {
            x = event.getSceneX();
            y = event.getSceneY();
        });
        root.setOnMouseDragged(event -> {

            primaryStage.setX(event.getScreenX() - x);
            primaryStage.setY(event.getScreenY() - y);

        });
        primaryStage.show();
    }

    public static void openMainScreen(Screen currentScreen) throws IOException {
        Stage primaryStage = new Stage();

        // Load FXML
        Parent root = FXMLLoader.load(Objects.requireNonNull(Launcher.class.getResource("fxml/main.fxml")));

        // Transparan pencere ayarları
        Scene scene = new Scene(root);
        scene.setFill(null);
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.TRANSPARENT);

        // Başlık ve ikon
        primaryStage.setTitle("Önder Grup Setup Wizard " + SystemVariables.getVersion());
        Image icon = new Image(Objects.requireNonNull(Launcher.class.getResourceAsStream("/assets/images/logo.png")));
        primaryStage.getIcons().add(icon);

        Rectangle clip = new Rectangle();
        clip.setWidth(600);
        clip.setHeight(400);
        clip.setArcWidth(30);
        clip.setArcHeight(30);
        root.setClip(clip);

        // Ekran ortalama
        Rectangle2D bounds = currentScreen.getVisualBounds();
        primaryStage.setOnShown(event -> {
            double stageWidth = primaryStage.getWidth();
            double stageHeight = primaryStage.getHeight();

            double centerX = bounds.getMinX() + (bounds.getWidth() - stageWidth) / 2;
            double centerY = bounds.getMinY() + (bounds.getHeight() - stageHeight) / 2;

            primaryStage.setX(centerX);
            primaryStage.setY(centerY);
        });

        primaryStage.show();
    }

    public static Screen getScreenOfNode(Node node) {
        Window window = node.getScene().getWindow();

        double windowX = window.getX();
        double windowY = window.getY();

        for (Screen screen : Screen.getScreens()) {
            Rectangle2D bounds = screen.getVisualBounds();
            if (bounds.contains(windowX, windowY)) {
                return screen;
            }
        }

        return Screen.getPrimary();
    }
}