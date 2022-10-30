package com.nextlabs.destiny.configservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration server security configuration.
 *
 * @author Sachindra Dasun
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${config.client.username}")
    private String username;

    @Value("${config.client.password}")
    private String password;

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        String encodedPassword = passwordEncoder().encode(password);
        auth.inMemoryAuthentication().withUser(username).password(encodedPassword).roles("CLIENT");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .httpBasic()
                .and()
                .authorizeRequests()
                .antMatchers("/actuator/health/liveness")
                .permitAll()
                .antMatchers("/actuator/health/readiness")
                .permitAll()
                .antMatchers("/status")
                .permitAll()
                .anyRequest()
                .authenticated();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
