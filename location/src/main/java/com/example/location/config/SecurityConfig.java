package com.example.location.config;

import com.example.location.utils.filters.InternalApiFilter;
import com.example.location.utils.filters.MyCorsFilter;
import com.example.location.utils.services.AppEndpoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.List;

import static com.example.location.utils.services.AppEndpoint.PUBLIC_ENDPOINTS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final MyCorsFilter myCorsFilter;

    private final InternalApiFilter internalApiFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html", "/internal/**").permitAll();
                    auth.requestMatchers("/actuator/info").permitAll();
                    for (AppEndpoint.EndpointPermission endpoint : PUBLIC_ENDPOINTS) {
                        auth.requestMatchers(endpoint.method(), endpoint.path()).permitAll();
                    }
                    auth.anyRequest().authenticated();
                });
        httpSecurity.oauth2ResourceServer(resource -> resource.jwt(Customizer.withDefaults()));
        return httpSecurity.build();
    }
}
