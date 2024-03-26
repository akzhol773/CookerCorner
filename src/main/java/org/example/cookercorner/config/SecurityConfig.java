package org.example.cookercorner.config;


import org.example.cookercorner.util.CustomUserDetails;
import org.example.cookercorner.util.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
@Configuration

public class SecurityConfig {


    private final CustomUserDetails customUserDetails;
    private final JwtAuthFilter jwtAuthFilter;
    private final LogoutHandler logoutHandler;


    @Autowired
    public SecurityConfig(CustomUserDetails customUserDetails, JwtAuthFilter jwtAuthFilter, LogoutHandler logoutHandler) {
        this.customUserDetails = customUserDetails;
        this.jwtAuthFilter = jwtAuthFilter;
        this.logoutHandler = logoutHandler;
    }


    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
       return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration configuration = new CorsConfiguration();
                    configuration.setAllowCredentials(true);
                    configuration.addAllowedOriginPattern("https://*");
                    configuration.addAllowedOriginPattern("http://*");
                    configuration.addAllowedHeader("*");
                    configuration.addAllowedMethod("*");
                    return configuration;
                }))
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
               .userDetailsService(customUserDetails)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
               .logout(logout -> logout
                       .logoutUrl("/api/auth/logout")
                       .addLogoutHandler(logoutHandler)
                       .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
               )
               .build();
    }
}
