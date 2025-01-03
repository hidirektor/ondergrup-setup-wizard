package me.t3sl4.installer.utils.file;

import me.t3sl4.installer.utils.system.Definitions;
import mslinks.ShellLink;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {
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

        createDirectory(Definitions.mainPath);
    }

    private static void createDirectory(String path) throws IOException {
        Path dirPath = Paths.get(path);
        if (Files.notExists(dirPath)) {
            Files.createDirectories(dirPath);
        }
    }

    public static void deleteIfExists(File file) {
        if (file.exists() && file.delete()) {
            System.out.println("Mevcut dosya silindi: " + file.getAbsolutePath());
        }
    }

    public static void createDesktopShortcut(String fileName, String targetPath, String iconPath, String workingDirectory) throws IOException {
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
                .setIconLocation(iconPath); // İkon olarak aynı dosya ayarlanıyor
        sl.getHeader().setIconIndex(0); // İkonun dizin numarası

        // Kısayolu kaydet
        sl.saveTo(shortcutPath);
        System.out.println("Kısayol oluşturuldu: " + shortcutPath);
    }

    public static void addToStartup(String fileName, String targetPath, String iconPath, String workingDirectory) throws IOException {
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
                .setIconLocation(iconPath); // İkon olarak aynı dosya ayarlanıyor
        sl.getHeader().setIconIndex(0); // İkonun dizin numarası

        // Kısayolu kaydet
        sl.saveTo(shortcutPath);
        System.out.println("Başlangıç klasörüne eklendi: " + shortcutPath);
    }
}