package ru.kata.spring.boot_security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.kata.spring.boot_security.security.SecurityUserDetailsService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final SuccessUserHandler successUserHandler;
    private final SecurityUserDetailsService securityUserDetailsService;

    public WebSecurityConfig(SuccessUserHandler successUserHandler, SecurityUserDetailsService securityUserDetailsService) {
        this.successUserHandler = successUserHandler;
        this.securityUserDetailsService = securityUserDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/", "/login", "/user.html", "/admin.html").permitAll()
                .antMatchers("/api/users/current").hasAnyRole("ADMIN", "USER")
                .antMatchers("/api/user").hasAnyRole("ADMIN", "USER")
                .antMatchers("/admin.html", "/user.html").hasAnyRole("ADMIN", "USER")
                .antMatchers(HttpMethod.POST, "/api/users/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin().successHandler(successUserHandler)
                .permitAll()
                .and()
                .logout().logoutSuccessUrl("/")
                .permitAll();
    }

    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(securityUserDetailsService).passwordEncoder(passwordEncoder());
    }
}