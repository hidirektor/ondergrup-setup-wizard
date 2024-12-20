package me.t3sl4.installer.utils;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

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
        SystemVariables.mainPath = basePath + "OnderGrup/";

        createDirectory(SystemVariables.mainPath);
    }

    private static void createDirectory(String path) throws IOException {
        Path dirPath = Paths.get(path);
        if (Files.notExists(dirPath)) {
            Files.createDirectories(dirPath);
        }
    }

    public static void createFile(String path) throws IOException {
        Path filePath = Paths.get(path);
        if (Files.notExists(filePath)) {
            Files.createFile(filePath);
        }
    }

    public static void fileCopy(String sourcePath, String destPath, boolean isRefresh) throws IOException {
        File destinationFile = new File(destPath);

        if(isRefresh) {
            InputStream resourceAsStream = FileUtil.class.getResourceAsStream(sourcePath);

            if (resourceAsStream == null) {
                throw new FileNotFoundException("Kaynak bulunamadı: " + sourcePath);
            }

            Path destination = Paths.get(destPath);
            Files.copy(resourceAsStream, destination, StandardCopyOption.REPLACE_EXISTING);
            resourceAsStream.close();
        } else {
            if (!destinationFile.exists()) {
                InputStream resourceAsStream = FileUtil.class.getResourceAsStream(sourcePath);

                if (resourceAsStream == null) {
                    throw new FileNotFoundException("Kaynak bulunamadı: " + sourcePath);
                }

                Path destination = Paths.get(destPath);
                Files.copy(resourceAsStream, destination, StandardCopyOption.REPLACE_EXISTING);
                resourceAsStream.close();
            } else {
                System.out.println("File already exists: " + destPath);
            }
        }
    }

    /**
     * Verilen bir URL'den dosyayı indirir ve hedef dosya konumuna kaydeder.
     *
     * @param sourceUrl URL'den dosya alınır.
     * @param destinationFile Dosyanın kaydedileceği hedef konum.
     * @throws IOException Eğer indirme veya yazma sırasında bir hata oluşursa.
     */
    public static void copyURLToFile(URL sourceUrl, File destinationFile) throws IOException {
        try (InputStream inputStream = new BufferedInputStream(sourceUrl.openStream());
             FileOutputStream outputStream = new FileOutputStream(destinationFile)) {

            byte[] buffer = new byte[1024]; // 1 KB tampon boyutu
            int bytesRead;

            // Veri akışını okuyup yaz
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            System.out.println("Dosya başarıyla indirildi: " + destinationFile.getAbsolutePath());
        }
    }
}