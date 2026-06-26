package vn.edu.fpt.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import javax.imageio.ImageIO;

@Configuration
public class ImageIOConfig {
    @PostConstruct
    public void initImageIO() {
        ImageIO.scanForPlugins();
        System.out.println("WebP writer = " +
                ImageIO.getImageWritersByFormatName("webp").hasNext());
    }
}
