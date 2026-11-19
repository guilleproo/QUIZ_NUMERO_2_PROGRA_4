package com.ejemplo.personas.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Value("${app.security.enabled:true}")
    private boolean securityEnabled;

    // ===== Cadena 1: SOLO para la consola H2 (se evalúa primero) =====
    @Bean
    @Order(1)
    public SecurityFilterChain h2ConsoleChain(HttpSecurity http) throws Exception {
        http
            // Esta cadena aplica únicamente a las rutas que empiezan con /h2-console
            .securityMatcher(request ->
                request.getRequestURI().startsWith(request.getContextPath() + "/h2-console"))
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .csrf(csrf -> csrf.disable())                         // H2 no envía token CSRF
            .headers(h -> h.frameOptions(f -> f.sameOrigin()));   // H2 usa frames
        return http.build();
    }

    // ===== Cadena 2: el resto de la aplicación =====
    @Bean
    @Order(2)
    public SecurityFilterChain appChain(HttpSecurity http) throws Exception {

        if (!securityEnabled) {
            http
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .csrf(csrf -> csrf.disable());
            return http.build();
        }

        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/error").permitAll()
                .requestMatchers("/usuarios/**").hasRole("ADMIN")
                .requestMatchers("/mascotas/nuevo", "/mascotas/guardar",
                                 "/mascotas/*/editar", "/mascotas/*/actualizar",
                                 "/mascotas/*/eliminar").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/usuarios", true)
                .failureUrl("/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}