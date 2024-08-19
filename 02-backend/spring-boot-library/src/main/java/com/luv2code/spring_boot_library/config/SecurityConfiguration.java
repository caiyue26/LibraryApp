// added notes on 2024.07.14

package com.luv2code.spring_boot_library.config;

import com.auth0.spring.security.api.JwtWebSecurityConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;
import java.util.Arrays;


@Configuration
public class SecurityConfiguration {
    // injects the value of the auth0.audience property from the application's config into the 'audience' variable
    @Value("${auth0.audience}")
    private String audience;

    @Value("${auth0.issuer}")
    private String issuer;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Disable Cross Site Request Forgery protection
        http.csrf().disable();

        // Protect endpoints at /api/<type>/secure, requiring authentication
        http.authorizeRequests(configurer -> configurer.
                antMatchers(
                        "/api/books/secure/**", "/api/reviews/secure/**",
                        "/api/messages/secure/**",
                        "/api/admin/secure/**")
                .authenticated())
                .oauth2ResourceServer()
                .jwt();

        // Configure Auth0 JWT authentication using the provided autdience and issuer.
        JwtWebSecurityConfigurer
                .forRS256(audience, issuer)
                .configure(http);

        // Add CORS filters
        http.cors();

        // Add content negotiation strategy to use header-based content negotiation.
        http.setSharedObject(ContentNegotiationStrategy.class, new HeaderContentNegotiationStrategy());
        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri(this.issuer + ".well-known/jwks.json").build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Allows requests from any origin
        configuration.setAllowedOrigins(Arrays.asList("*"));
        // Allows any headers
        configuration.setAllowedHeaders(Arrays.asList("*"));
        // Allows GET POST PUT DELETE and OPTIONS
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT","DELETE", "OPTIONS"));
        // Sets the max age for the preflight request cache to 3600 seconds
        configuration.setMaxAge(3600L);

        // Registers teh CORS configuration for all paths
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
