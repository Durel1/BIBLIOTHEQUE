package com.durel.bibliotheque.config;

import com.durel.bibliotheque.security.JwtAuthenticationFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpStatus;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Defines HTTP security rules for the REST API.
 */
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter) {

        this.jwtAuthenticationFilter =
                jwtAuthenticationFilter;
    }

    /**
     * Configures Spring Security.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            CorsConfigurationSource corsConfigurationSource)
            throws Exception {

        http
                /*
                 * Enables CORS so that the frontend running
                 * on another origin can call the API.
                 */
                .cors(cors ->
                        cors.configurationSource(
                                corsConfigurationSource
                        )
                )

                /*
                 * The API uses JWT authentication rather than
                 * cookie-based sessions.
                 */
                .csrf(csrf ->
                        csrf.disable()
                )

                /*
                 * Spring Security must not create HTTP sessions.
                 * Each authenticated request contains its JWT.
                 */
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                /*
                 * Requests requiring authentication return
                 * HTTP 401 when no valid authentication exists.
                 */
                .exceptionHandling(exceptions ->
                        exceptions.authenticationEntryPoint(
                                new HttpStatusEntryPoint(
                                        HttpStatus.UNAUTHORIZED
                                )
                        )
                )

                /*
                 * Registration and login remain public.
                 * Every other endpoint requires authentication.
                 */
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers(
                                        "/api/auth/register",
                                        "/api/auth/login"
                                )
                                .permitAll()

                                .anyRequest()
                                .authenticated()
                )

                /*
                 * The JWT filter runs before Spring Security's
                 * username/password authentication filter.
                 */
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    /**
     * Defines which frontend is allowed to communicate
     * with the backend API.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        /*
         * Local frontend development server.
         *
         * Later, the deployed frontend URL will also
         * be configured here.
         */
        configuration.setAllowedOrigins(
                List.of(
                        "http://localhost:5500"
                )
        );

        /*
         * HTTP methods used by our REST API.
         */
        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "OPTIONS"
                )
        );

        /*
         * Content-Type:
         * required when sending JSON.
         *
         * Authorization:
         * required when sending the JWT.
         */
        configuration.setAllowedHeaders(
                List.of(
                        "Content-Type",
                        "Authorization"
                )
        );

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        /*
         * Apply this CORS configuration to all API paths.
         */
        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }
}