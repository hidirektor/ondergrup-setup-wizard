package me.t3sl4.installer.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
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
import me.t3sl4.installer.utils.FileUtil;
import me.t3sl4.installer.utils.GeneralUtil;
import me.t3sl4.installer.utils.SystemVariables;
import mslinks.ShellLink;

import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
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

    //Ekran büyütüp küçültme
    private boolean stageMaximized = false;

    //Ekran sürükleme değişkenleri
    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            currentStage = (Stage) userFolderPath.getScene().getWindow();
            userFolderPath.setText(SystemVariables.mainPath);
        });

        addHoverEffect(closeIcon, minimizeIcon, expandIcon);
        setupDragAndDrop();
        enableWindowDragging();
    }

    @FXML
    public void closeProgram() {
        GeneralUtil.systemShutdown();
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
        darkenEffect.setBrightness(-0.5); // Karartma seviyesi

        for (ImageView imageView : imageViews) {
            imageView.setOnMouseEntered(event -> imageView.setEffect(darkenEffect));
            imageView.setOnMouseExited(event -> imageView.setEffect(null));
        }
    }

    private void setupDragAndDrop() {
        // Drag start (applicationAnchor)
        applicationAnchor.setOnDragDetected(event -> {
            Dragboard dragboard = applicationAnchor.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString("Dragging Application Anchor"); // Drag verisi
            dragboard.setContent(content);

            Image resizedImage = new Image(Objects.requireNonNull(Launcher.class.getResourceAsStream("/assets/images/logo-launcher.png")), 40, 40, true, true);

            double offsetX = resizedImage.getWidth() / 2;
            double offsetY = resizedImage.getHeight() / 2;

            dragboard.setDragView(resizedImage, offsetX, offsetY);

            event.consume();
        });

        // Drag over (destinationAnchor)
        destinationAnchor.setOnDragOver(event -> {
            if (event.getGestureSource() != destinationAnchor && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        // Drop (destinationAnchor)
        destinationAnchor.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            boolean success = false;

            if (dragboard.hasString()) {
                dragboard.setDragView(null);
                // Kurulumu başlat
                startInstallation();
                success = true;
            }

            event.setDropCompleted(success);
            event.consume();
        });

        // Drag done (applicationAnchor)
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

    public static void startInstallation() {
        try {
            // Ana dizini kontrol et ve oluştur
            File mainPath = new File(SystemVariables.mainPath);
            if (!mainPath.exists() && !mainPath.mkdirs()) {
                throw new IOException("Ana dizin oluşturulamadı: " + SystemVariables.mainPath);
            }

            // İşletim sistemi kontrolü
            String os = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
            String hydraulicFileName, launcherFileName;

            if (os.contains("win")) {
                hydraulicFileName = "windows_Hydraulic.exe";
                launcherFileName = "windows_Launcher.exe";
            } else if (os.contains("mac")) {
                hydraulicFileName = "mac_Hydraulic.jar";
                launcherFileName = "mac_Launcher.jar";
            } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
                hydraulicFileName = "unix_Hydraulic.jar";
                launcherFileName = "unix_Launcher.jar";
            } else {
                throw new UnsupportedOperationException("Bu işletim sistemi desteklenmiyor: " + os);
            }

            // GitHub'dan en son sürüm tag'lerini al
            String hydraulicVersion = getLatestVersionFromGitHub(SystemVariables.HYDRAULIC_RELEASE_URL);
            String launcherVersion = getLatestVersionFromGitHub(SystemVariables.LAUNCHER_RELEASE_URL);

            if (hydraulicVersion == null || launcherVersion == null) {
                throw new IOException("GitHub sürüm bilgisi alınamadı.");
            }

            // İndirilecek dosyaların URL'lerini oluştur
            String hydraulicDownloadUrl = SystemVariables.HYDRAULIC_RELEASE_BASE_URL + "/download/" + hydraulicVersion + "/" + hydraulicFileName;
            String launcherDownloadUrl = SystemVariables.LAUNCHER_RELEASE_BASE_URL + "/download/" + launcherVersion + "/" + launcherFileName;

            // Dosyaları indir
            File hydraulicFile = new File(mainPath, hydraulicFileName);
            File launcherFile = new File(mainPath, launcherFileName);

            deleteIfExists(hydraulicFile);
            deleteIfExists(launcherFile);

            downloadFile(hydraulicDownloadUrl, hydraulicFile);
            downloadFile(launcherDownloadUrl, launcherFile);

            // Windows için masaüstüne kısayol oluştur ve başlangıç ayarları yap
            if (os.contains("win")) {
                String mainPathString = mainPath.getAbsolutePath();
                createDesktopShortcut(hydraulicFileName, hydraulicFile.getAbsolutePath(), mainPathString);
                createDesktopShortcut(launcherFileName, launcherFile.getAbsolutePath(), mainPathString);

                addToStartup(hydraulicFileName, hydraulicFile.getAbsolutePath(), mainPathString);
                addToStartup(launcherFileName, launcherFile.getAbsolutePath(), mainPathString);
            }

            System.out.println("Kurulum tamamlandı!");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Kurulum sırasında bir hata oluştu: " + e.getMessage());
        }
    }

    private static String getLatestVersionFromGitHub(String releaseUrl) {
        try {
            HttpResponse<Void> response = httpHead(releaseUrl);

            if (response.statusCode() == 302) { // 302 Redirect
                String redirectedUrl = response.headers().firstValue("location").orElse(null);
                if (redirectedUrl != null) {
                    return extractTagFromURL(redirectedUrl);
                } else {
                    System.err.println("Yönlendirme URL'si alınamadı.");
                    return null;
                }
            } else if (response.statusCode() == 200) {
                return extractTagFromURL(releaseUrl);
            } else {
                System.err.println("GitHub sürüm bilgisi alınamadı: HTTP " + response.statusCode());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String extractTagFromURL(String url) {
        String tagPrefix = "/releases/tag/";
        int tagIndex = url.indexOf(tagPrefix);
        if (tagIndex == -1) {
            return null;
        }
        return url.substring(tagIndex + tagPrefix.length());
    }

    private static HttpResponse<Void> httpHead(String url) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .build();
        return client.send(request, HttpResponse.BodyHandlers.discarding());
    }

    private static void downloadFile(String fileUrl, File destination) throws IOException {
        System.out.println("Dosya indiriliyor: " + fileUrl);
        try (InputStream in = new URL(fileUrl).openStream();
             FileOutputStream out = new FileOutputStream(destination)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
        System.out.println("İndirme tamamlandı: " + destination.getAbsolutePath());
    }

    private static void deleteIfExists(File file) {
        if (file.exists() && file.delete()) {
            System.out.println("Mevcut dosya silindi: " + file.getAbsolutePath());
        }
    }

    public static void createDesktopShortcut(String fileName, String targetPath, String workingDirectory) throws IOException {
        // Masaüstü dizinini al
        File home = FileSystemView.getFileSystemView().getHomeDirectory();
        String desktopPath = home.getAbsolutePath();
        File desktopDir = new File(desktopPath);

        // Masaüstü dizinini kontrol et ve gerekirse oluştur
        if (!desktopDir.exists() && !desktopDir.mkdirs()) {
            throw new IOException("Masaüstü dizini oluşturulamadı: " + desktopPath);
        }

        String shortcutPath = desktopPath + "\\" + fileName + ".lnk";

        // Kısayolu oluştur
        ShellLink sl = new ShellLink()
                .setTarget(targetPath)
                .setWorkingDir(workingDirectory)
                .setIconLocation(targetPath); // İkon olarak aynı dosya ayarlanıyor
        sl.getHeader().setIconIndex(0); // İkonun dizin numarası

        // Kısayolu kaydet
        sl.saveTo(shortcutPath);
        System.out.println("Kısayol oluşturuldu: " + shortcutPath);
    }

    public static void addToStartup(String fileName, String targetPath, String workingDirectory) throws IOException {
        // Windows başlangıç klasörünü al
        String startupPath = System.getProperty("user.home") + "\\AppData\\Roaming\\Microsoft\\Windows\\Start Menu\\Programs\\Startup";
        File startupDir = new File(startupPath);

        // Başlangıç dizinini kontrol et ve gerekirse oluştur
        if (!startupDir.exists() && !startupDir.mkdirs()) {
            throw new IOException("Başlangıç dizini oluşturulamadı: " + startupPath);
        }

        String shortcutPath = startupPath + "\\" + fileName + ".lnk";

        // Kısayolu oluştur
        ShellLink sl = new ShellLink()
                .setTarget(targetPath)
                .setWorkingDir(workingDirectory)
                .setIconLocation(targetPath); // İkon olarak aynı dosya ayarlanıyor
        sl.getHeader().setIconIndex(0); // İkonun dizin numarası

        // Kısayolu kaydet
        sl.saveTo(shortcutPath);
        System.out.println("Başlangıç klasörüne eklendi: " + shortcutPath);
    }
}
