package com.nextlabs.destiny.cc.installer.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import com.nextlabs.destiny.cc.installer.config.properties.CcProperties;

/**
 * Control Center installer security configuration.
 *
 * @author Sachindra Dasun
 */
@Configuration
@EnableWebSecurity
@ConditionalOnProperty(value = "nextlabs.cc.web-installer")
public class WebSecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

    @Autowired
    private CcProperties ccProperties;

    private PasswordEncoder delegatingPasswordEncoder;

    @Bean
    @Override
    public UserDetailsService userDetailsService() {

        final User.UserBuilder userBuilder = User.builder().passwordEncoder(delegatingPasswordEncoder::encode);
        UserDetails user = userBuilder
                .username("cc-user")
                .password(ccProperties.getAccessKey())
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService()).passwordEncoder(delegatingPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and()
                .csrf().disable()
                .requestCache().requestCache(new NullRequestCache()).and()
                .httpBasic()
                .and()
                .authorizeRequests()
                .antMatchers("/index.html", "/favicon.ico", "/ui/**", "/cc-installation-progress/**")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .exceptionHandling().authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/"))
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    // Redirect UI routes to Angular application.
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/installer/**")
                .addResourceLocations("/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        Resource requestedResource = location.createRelative("installer/" + resourcePath);
                        return requestedResource.exists() && requestedResource.isReadable() ? requestedResource
                                : location.createRelative("index.html");
                    }
                });
    }

    @Autowired
    public void setDelegatingPasswordEncoder(PasswordEncoder delegatingPasswordEncoder) {
        this.delegatingPasswordEncoder = delegatingPasswordEncoder;
    }
}
