package org.example.vehicle.config;

import lombok.RequiredArgsConstructor;
import org.example.vehicle.utils.auditing.ApplicationAuditAware;
import org.example.vehicle.utils.filters.MyCorsFilter;
import org.example.vehicle.utils.services.AppEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.web.bind.annotation.RequestParam;

import static org.example.vehicle.utils.services.AppEndpoint.PUBLIC_ENDPOINTS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors(Customizer.withDefaults())
                .csrf().disable()
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll();
                    for(AppEndpoint.EndpointPermission endpoint : PUBLIC_ENDPOINTS) {
                        auth.requestMatchers(endpoint.method(), endpoint.path()).permitAll();
                    }
                    auth.anyRequest().permitAll();
                });
        httpSecurity.oauth2ResourceServer(resource -> resource.jwt(Customizer.withDefaults()));
        return httpSecurity.build();
    }

    @Bean
    public AuditorAware<String> auditorAware() {
        return new ApplicationAuditAware();
    }
}
