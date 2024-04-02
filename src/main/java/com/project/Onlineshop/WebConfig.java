package com.project.Onlineshop;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**") // Change the resource URL pattern as needed
                .addResourceLocations("file:///C:/app_data/") // Specify the directory path
                .setCachePeriod(0); // Disable caching for dynamic content
    }
}