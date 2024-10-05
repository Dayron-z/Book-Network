package com.pageconnect.booknetwork.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration /*--> I tell Spring that this class can be used as a source of definitions. */
@EnableWebSecurity/* --> Enables web security in my application; without this annotation, the endpoints cannot be protected with Spring Security. */
@RequiredArgsConstructor /* Automatically generates a constructor that injects dependencies for final and non-null fields, improving readability and reducing boilerplate code. */
@EnableMethodSecurity(securedEnabled = true) /* Enables the @Secured annotation. */
public class SecurityConfig {


    private final JwtFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req -> /*Authentication rules*/
                        req.requestMatchers(
                                "/auth/**",
                                        "/api/v1/auth/**",
                                        "/v2/api-docs",
                                        "/v3/api-docs",
                                        "/v3/api-docs/**",
                                        "/swagger-resources",
                                        "/swagger-resources/**",
                                        "/configuration/ui",
                                        "/configuration/security",
                                        "/swagger-ui/**",
                                        "/webjars/**",
                                        "/swagger-ui.html"           /*---> Access is allowed to authorized users and to view documentation*/
                        ).permitAll()
                                .anyRequest()
                                .authenticated()                 /*---> .anyRequest().authenticated():  This means that any request that is not in the requestMatcher's only way of accessing it is through an authenticated user */
                        )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore( jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
