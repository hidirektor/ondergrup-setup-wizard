package me.t3sl4.installer.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import me.t3sl4.installer.Launcher;
import me.t3sl4.installer.utils.Utils;
import me.t3sl4.installer.utils.system.Definitions;
import me.t3sl4.util.file.FileUtil;
import me.t3sl4.util.os.OSUtil;
import me.t3sl4.util.os.desktop.DesktopUtil;
import me.t3sl4.util.version.DownloadProgressListener;
import me.t3sl4.util.version.VersionUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private Stage currentStage;

    @FXML
    private HBox topHBox;

    @FXML
    private ImageView closeIcon, minimizeIcon, expandIcon;

    @FXML
    private Label userFolderPath;

    @FXML
    private AnchorPane applicationAnchor, destinationAnchor;

    @FXML
    private ImageView mainLogo;

    @FXML
    private Label mainLabel;

    @FXML
    private AnchorPane downloadProgressPane;

    @FXML
    private ProgressBar hydraulicProgress, updaterServiceProgress;

    private static ProgressBar hydraulicProgressBar;

    @FXML
    private Label downloadStartedLabel;

    @FXML
    private ImageView hydraulicLogo;

    //Ekran büyütüp küçültme
    private boolean stageMaximized = false;

    //Ekran sürükleme değişkenleri
    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Image hydraulicImage = new Image(Objects.requireNonNull(Launcher.class.getResourceAsStream("/assets/images/logo-hydraulic.png")), 16, 16, true, true);

        Platform.runLater(() -> {
            currentStage = (Stage) userFolderPath.getScene().getWindow();
            userFolderPath.setText(Definitions.mainPath);
            hydraulicLogo.setImage(hydraulicImage);
        });

        hydraulicProgressBar = hydraulicProgress;

        addHoverEffect(closeIcon, minimizeIcon, expandIcon);
        setupDragAndDrop();
        enableWindowDragging();
    }

    @FXML
    public void closeProgram() {
        Utils.systemShutdown();
    }

    @FXML
    public void minimizeProgram() {
        currentStage.setIconified(true);
    }

    @FXML
    public void expandProgram() {
        if(stageMaximized) {
            currentStage.setMaximized(false);
            stageMaximized = false;
        } else {
            currentStage.setMaximized(true);
            stageMaximized = true;
        }
    }

    private void addHoverEffect(ImageView... imageViews) {
        ColorAdjust darkenEffect = new ColorAdjust();
        darkenEffect.setBrightness(-0.5);

        for (ImageView imageView : imageViews) {
            imageView.setOnMouseEntered(event -> imageView.setEffect(darkenEffect));
            imageView.setOnMouseExited(event -> imageView.setEffect(null));
        }
    }

    private void setupDragAndDrop() {
        applicationAnchor.setOnDragDetected(event -> {
            Dragboard dragboard = applicationAnchor.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString("Dragging Application Anchor"); // Drag verisi
            dragboard.setContent(content);

            Image resizedImage = new Image(Objects.requireNonNull(Launcher.class.getResourceAsStream("/assets/images/logo-hydraulic.png")), 40, 40, true, true);

            double offsetX = resizedImage.getWidth() / 2;
            double offsetY = resizedImage.getHeight() / 2;

            dragboard.setDragView(resizedImage, offsetX, offsetY);

            event.consume();
        });

        destinationAnchor.setOnDragOver(event -> {
            if (event.getGestureSource() != destinationAnchor && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        destinationAnchor.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            boolean success = false;

            if (dragboard.hasString()) {
                startInstallation();
                success = true;
            }

            event.setDropCompleted(success);
            event.consume();
        });

        applicationAnchor.setOnDragDone(event -> {
            if (event.getTransferMode() == TransferMode.MOVE) {
                System.out.println("Drag işlemi tamamlandı.");
            }
            event.consume();
        });
    }

    private void enableWindowDragging() {
        topHBox.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        topHBox.setOnMouseDragged(event -> {
            currentStage.setX(event.getScreenX() - xOffset);
            currentStage.setY(event.getScreenY() - yOffset);
        });
    }

    public void startInstallation() {
        Platform.runLater(() -> {
            mainLogo.setVisible(false);
            mainLabel.setVisible(false);
            downloadProgressPane.setVisible(true);
        });

        Task<Void> installationTask = new Task<>() {
            @Override
            protected Void call() {
                try {
                    File mainPath = new File(Definitions.mainPath);
                    if (!mainPath.exists() && !mainPath.mkdirs()) {
                        throw new IOException("Ana dizin oluşturulamadı: " + Definitions.mainPath);
                    }

                    String os = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
                    String hydraulicFileName, updaterServiceFileName;

                    if (os.contains("win")) {
                        updaterServiceFileName = "windows_Updater.exe";
                        hydraulicFileName = "windows_Hydraulic.exe";
                    } else if (os.contains("mac")) {
                        updaterServiceFileName = "mac_Updater.jar";
                        hydraulicFileName = "mac_Hydraulic.jar";
                    } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
                        updaterServiceFileName = "unix_Updater.jar";
                        hydraulicFileName = "unix_Hydraulic.jar";
                    } else {
                        throw new UnsupportedOperationException("Bu işletim sistemi desteklenmiyor: " + os);
                    }

                    File updaterFile = new File(mainPath, updaterServiceFileName);
                    File hydraulicFile = new File(mainPath, hydraulicFileName);

                    if (FileUtil.fileExists(updaterFile.getAbsolutePath())) {
                        FileUtil.deleteFile(updaterFile.getAbsolutePath());
                    }

                    if (FileUtil.fileExists(hydraulicFile.getAbsolutePath())) {
                        FileUtil.deleteFile(hydraulicFile.getAbsolutePath());
                    }

                    startDownloadTask(updaterServiceFileName, updaterServiceProgress, Definitions.UPDATER_REPO_NAME, Definitions.PREF_UPDATER_KEY);
                    startDownloadTask(hydraulicFileName, hydraulicProgress, Definitions.HYDRAULIC_REPO_NAME, Definitions.PREF_HYDRAULIC_KEY);

                    if (os.contains("win")) {
                        String mainPathString = mainPath.getAbsolutePath();
                        DesktopUtil.createDesktopShortcut(hydraulicFileName, updaterFile.getAbsolutePath(), hydraulicFile.getAbsolutePath(), mainPathString);

                        DesktopUtil.addToStartup(hydraulicFileName, updaterFile.getAbsolutePath(), hydraulicFile.getAbsolutePath(), mainPathString);
                    }

                    System.out.println("Kurulum tamamlandı!");
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Kurulum sırasında bir hata oluştu: " + e.getMessage());
                } finally {
                    Platform.runLater(() -> {
                        downloadStartedLabel.setText("İndirme işlemi tamamlandı. Kurulum sihirbazını kapatıp dilediğiniz programı masaüstünde ki ikonuna çift tıklayarak çalıştırabilirsiniz.");
                    });
                }
                return null;
            }
        };

        Thread installationThread = new Thread(installationTask);
        installationThread.setDaemon(true);
        installationThread.start();
    }

    private void startDownloadTask(String fileName, ProgressBar currentProgressBar, String repoName, String prefKey) {
        Task<Void> downloadTask = new Task<>() {
            @Override
            protected Void call() {
                try {
                    DownloadProgressListener downloadListener = (bytesRead, totalBytes) -> {
                        Platform.runLater(() -> currentProgressBar.setProgress(0));

                        if (totalBytes > 0) {
                            double progress = (double) bytesRead / totalBytes;
                            Platform.runLater(() -> currentProgressBar.setProgress(progress));
                        } else {
                            Platform.runLater(() -> currentProgressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS)); // Indeterminate progress
                        }
                    };

                    VersionUtil.downloadLatestWithProgress(
                            Definitions.REPO_OWNER,
                            repoName,
                            Definitions.mainPath,
                            fileName,
                            downloadListener
                    );

                    String latestVersion = VersionUtil.getLatestVersion(Definitions.REPO_OWNER, repoName);
                    OSUtil.updatePrefData(Definitions.PREF_NODE_NAME, prefKey, latestVersion);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                return null;
            }
        };

        try {
            Thread downloadThread = new Thread(downloadTask);
            downloadThread.setDaemon(true);
            downloadThread.start();
            downloadThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
            // Handle the interruption appropriately
        }
    }
}
