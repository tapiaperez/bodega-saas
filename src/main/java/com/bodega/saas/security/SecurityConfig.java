package com.bodega.saas.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.bodega.saas.usuario.repository.UsuarioRepository;

@Configuration
public class SecurityConfig {

    private final UsuarioRepository usuarioRepository;

    public SecurityConfig(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/login",
                                "/tienda/**",
                                "/carrito/**",
                                "/checkout",
                                "/pedido-exitoso",
                                "/css/**",
                                "/js/**",
                                "/img/**").permitAll()
                        .requestMatchers("/admin/empleados/**")
                                .hasRole("ADMINISTRADOR")
                        .requestMatchers("/admin/reportes/**")
                                .hasAnyRole("ADMINISTRADOR", "SUPERVISOR")
                        .requestMatchers("/admin/ventas/**")
                                .hasAnyRole("ADMINISTRADOR", "SUPERVISOR", "CAJERO")
                        .requestMatchers("/admin/pedidos/**")
                                .hasAnyRole("ADMINISTRADOR", "SUPERVISOR", "CAJERO")
                        .requestMatchers("/admin/productos/**")
                                .hasAnyRole("ADMINISTRADOR", "SUPERVISOR")
                        .requestMatchers("/dashboard")
                                .authenticated()
                        .anyRequest()
                                .authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler((request, response, authentication) -> {
                            usuarioRepository.findByEmail(authentication.getName())
                                    .ifPresent(usuario -> request
                                            .getSession()
                                            .setAttribute("usuario", usuario));

                            response.sendRedirect("/dashboard");
                        })
                        .failureUrl("/login?error")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new LegacyPasswordEncoder();
    }
}
