package br.edu.ifpb.pweb2.retrato.config;

//import br.edu.ifpb.pweb2.retrato.model.Photographer;
//import br.edu.ifpb.pweb2.retrato.repository.PhotographerRepository;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

//@Configuration
//@EnableWebSecurity
public class ImageticoSecurityConfig {
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/photographer/login", "/photographer/register", "/photographer/form").permitAll()
//                        .requestMatchers("/administrator/**").hasRole("ADMIN")
//                        .anyRequest().authenticated()
//                )
//                .formLogin(form -> form
//                        .loginPage("/photographer/login")
//                        .defaultSuccessUrl("/photographer/dashboard", true)
//                        .permitAll()
//                )
//                .logout(logout -> logout
//                        .logoutUrl("/logout")
//                        .logoutSuccessUrl("/photographer/login")
//                        .invalidateHttpSession(true)
//                        .deleteCookies("JSESSIONID")
//                        .permitAll()
//                );
//
//        return http.build();
//    }
//
//    @Bean
//    public UserDetailsService userDetailsService(PhotographerRepository photographerRepository) {
//        return email -> {
//            Photographer photographer = photographerRepository.findByEmail(email)
//                    .orElseThrow((null));
//            return User.withUsername(photographer.getEmail())
//                    .password(photographer.getPassword())
//                    .roles(photographer.isAdmin() ? "ADMIN" : "USER")
//                    .build();
//        };
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
}
