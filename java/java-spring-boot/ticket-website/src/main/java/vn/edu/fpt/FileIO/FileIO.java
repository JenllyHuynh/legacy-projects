package vn.edu.fpt.FileIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class FileIO {
    @Value("${upload.root}")
    private String uploadRoot;

    // filePath: /upload/temporal/event/banner/1.jpg
    public void deleteFile(String filePath) {
        if (filePath == null) return;
        try {
            String realPath = uploadRoot + filePath;
            Path path = Paths.get(realPath);
            System.out.println("XÓA FILE");
            System.out.println("DELETE REAL PATH = " + path.toAbsolutePath());
            boolean isSuccess =  Files.deleteIfExists(path);
            if (isSuccess) {
                System.out.println("XÓA file thành công!");
            }
            else {
                System.out.println("XÓA file không thành công!");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // sourceUrl, targetUrl: /upload/...
    public void moveFile(String sourceUrl, String targetUrl) {
        if (sourceUrl == null || targetUrl == null) return;

        try {
            String sourceRealPath = uploadRoot + sourceUrl;
            String targetRealPath = uploadRoot + targetUrl;
            Path s = Paths.get(sourceRealPath);
            Path t = Paths.get(targetRealPath);
            Files.createDirectories(t.getParent());
            System.out.println("Chuyển file từ " + s + " đến " + t);
            Path isSuccess = Files.move(s, t);
            System.out.println("Trạng thái chuyển file: " + isSuccess);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
