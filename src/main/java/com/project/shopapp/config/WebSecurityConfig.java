package com.project.shopapp.config;

import com.project.shopapp.filters.JwtTokenFilter;
import com.project.shopapp.models.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor

public class WebSecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;
    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(request -> {
                    request
                            .requestMatchers(
                                    "/api/v1/users/register",
                                    "/api/v1/users/login",
                                    "api/v1/roles",
                                    "api/v1/products/**",
                                    "api/v1/products/images/*",
                                    "api/v1/products?**",
                                    "api/v1/categories",
                                    "api/v1/products/view/images/*"
                            )
                            .permitAll()
                            .requestMatchers(
                                    HttpMethod.POST, "api/v1/categories/**").hasRole(Role.ADMIN)
                            .requestMatchers(
                                    HttpMethod.PUT, "api/v1/categories/**").hasAnyRole(Role.ADMIN)
                            .requestMatchers(
                                    HttpMethod.DELETE, "api/v1/categories/**").hasAnyRole(Role.ADMIN)
                            .requestMatchers(
                                    HttpMethod.POST, "api/v1/orders/**").hasRole(Role.USER)
                            .requestMatchers(
                                    HttpMethod.GET, "api/v1/orders/**").hasAnyRole(Role.USER,
                                    Role.ADMIN)
                            .requestMatchers(
                                    HttpMethod.PUT, "api/v1/orders/**").hasRole(Role.ADMIN)
                            .requestMatchers(
                                    HttpMethod.DELETE, "api/v1/orders/**").hasRole(Role.ADMIN)
                            .requestMatchers(
                                    HttpMethod.POST, "api/v1/order_details/**").hasRole(Role.USER)
                            .requestMatchers(
                                    HttpMethod.GET, "api/v1/order_details/**").hasAnyRole(Role.USER,
                                    Role.ADMIN)
                            .requestMatchers(
                                    HttpMethod.PUT, "api/v1/order_details/**").hasRole(Role.ADMIN)
                            .requestMatchers(
                                    HttpMethod.DELETE, "api/v1/order_details/**").hasRole(Role.ADMIN)
                            .anyRequest().authenticated();

                });
        http.cors(new Customizer<CorsConfigurer<HttpSecurity>>() {
            @Override
            public void customize(CorsConfigurer<HttpSecurity> httpSecurityCorsConfigurer) {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(Arrays.asList("*"));
                configuration.setAllowedMethods(Arrays.asList("GET","POST","PUT","PATCH","DELETE","OPTION"));
                configuration.setAllowedHeaders(Arrays.asList(
                        "content-type",
                        "X-Requested-With",
                        "accept",
                        "Origin",
                        "Access-Control-Request-Method",
                        "authorization",
                        "x-auth-token",
                        "Access-Control-Request-Headers"));
                configuration.setExposedHeaders(Arrays.asList(
                        "Access-Control-Allow-Origin",
                        "x-auth-token",
                        "Access-Control-Allow-Credentials"));
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                httpSecurityCorsConfigurer.configurationSource(source);
            }
        });
        return http.build();
    }

}
