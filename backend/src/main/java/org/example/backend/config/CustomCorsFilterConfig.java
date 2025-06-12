package org.example.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Configuration class for CORS (Cross-Origin Resource Sharing) settings.
 * This class provides a custom CORS filter configuration for the application
 * to handle cross-origin requests securely.
 */
@Configuration
public class CustomCorsFilterConfig {

    /**
     * Creates and configures a CORS filter bean.
     * This filter allows cross-origin requests from the specified origin
     * with all headers and methods permitted.
     *
     * @return CorsFilter configured with specific CORS settings
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("https://fallestlaww.netlify.app");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
