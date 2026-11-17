package br.com.mvc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SegurancaConfig {
    @Bean
    public PasswordEncoder senhas() { return new BCryptPasswordEncoder(); }

    @Bean
    public UserDetailsService usuarios(PasswordEncoder senhas) {
        return new InMemoryUserDetailsManager(
            User.withUsername("admin").password(senhas.encode("admin123")).roles("ADM").build(),
            User.withUsername("estoquista").password(senhas.encode("estoque123")).roles("ESTOQUISTA").build(),
            User.withUsername("vendedor").password(senhas.encode("vendas123")).roles("VENDEDOR").build());
    }

    @Bean
    public SecurityFilterChain acesso(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/error").permitAll()
                .requestMatchers(HttpMethod.POST, "/produtos/*/descontinuar").hasRole("ADM")
                .anyRequest().authenticated())
            .formLogin(Customizer.withDefaults())
            .httpBasic(Customizer.withDefaults())
            .build();
    }
}
