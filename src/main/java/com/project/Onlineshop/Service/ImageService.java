package com.project.Onlineshop.Service;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageService {
    //    public static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/uploads";
//public static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/src/main/resources/static/products/images";
//    public static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/src/main/resources/images/products";
    public static final String UPLOAD_DIRECTORY = "C:/app_data";


    public String uploadImage(Model model, MultipartFile file) throws IOException {
        StringBuilder nameOfUploadedFile = new StringBuilder();

        if (!Files.exists(Path.of(UPLOAD_DIRECTORY))) {
            Files.createDirectories(Path.of(UPLOAD_DIRECTORY));
        }

        String originalFileName = file.getOriginalFilename();
        nameOfUploadedFile.append(originalFileName);

        String nameOfFileToSave = generateUUIDFileName(originalFileName); // will keep the same extension

        Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, nameOfFileToSave);

        Files.write(fileNameAndPath, file.getBytes());

        model.addAttribute("msg", "Uploaded images: " + nameOfUploadedFile.toString() +
                "\n Saved at: " + fileNameAndPath);

        model.addAttribute("uploadedFileName", nameOfFileToSave);
        return "upload_test";
    }

    private String generateUUIDFileName(String originalFileName) {
        String extension = getFileExtension(originalFileName);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + extension;
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex >= 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1);
        }
        return "";  // TODO raise new error - pass it back to the frontend - no extension found...
    }
}
