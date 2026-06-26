package vn.edu.fpt.service;

import org.imgscalr.Scalr;
import org.springframework.stereotype.Component;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class ImageService {
    public void resizeAndConvertToWebP(
            InputStream input,
            Path outputPath,
            int maxWidth,
            float quality
    ) throws IOException {

        BufferedImage original = ImageIO.read(input);
        if (original == null) {
            throw new IOException("Không đọc được ảnh");
        }

        BufferedImage resized = Scalr.resize(
                original,
                Scalr.Method.QUALITY,
                Scalr.Mode.FIT_TO_WIDTH,
                maxWidth
        );

        ImageWriter writer = ImageIO.getImageWritersByFormatName("webp").next();
        ImageWriteParam param = writer.getDefaultWriteParam();

        try (ImageOutputStream ios =
                     ImageIO.createImageOutputStream(Files.newOutputStream(outputPath))) {

            writer.setOutput(ios);
            writer.write(null, new IIOImage(resized, null, null), param);
        } finally {
            writer.dispose();
        }
    }
}
