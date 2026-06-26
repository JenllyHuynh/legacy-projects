package vn.edu.fpt.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${upload.root}")
    private String uploadRoot;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Must change if deploy to server:
        registry.addResourceHandler("/upload/event/banner/**")
                .addResourceLocations("file:" + uploadRoot + "/upload/event/banner/")
                .setCachePeriod(31536000);

        registry.addResourceHandler("/upload/event/thumbnail/**")
                .addResourceLocations("file:" + uploadRoot +  "/upload/event/thumbnail/")
                .setCachePeriod(31536000);

        registry.addResourceHandler("/upload/temporal/event/thumbnail/**")
                .addResourceLocations("file:" + uploadRoot + "/upload/temporal/event/thumbnail/")
                .setCachePeriod(31536000);

        registry.addResourceHandler("/upload/temporal/event/banner/**")
                .addResourceLocations("file:" + uploadRoot + "/upload/temporal/event/banner/")
                .setCachePeriod(31536000);

        registry.addResourceHandler("/upload/avatars/**")
                .addResourceLocations("file:upload/avatars/")
                .setCachePeriod(31536000);

        registry.addResourceHandler("/upload/event/organizer/**")
                .addResourceLocations("file:" + uploadRoot + "/upload/event/organizer/")
                .setCachePeriod(31536000);
    }
}
