package org.learning.auction.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserLoginDetailsService userLoginDetailsService;
    @Override
    protected void configure(HttpSecurity http) throws Exception { // TODO
        http.authorizeRequests()
                .antMatchers("/static/**").permitAll()
                .antMatchers("/bidding").permitAll()
                .antMatchers("/admin-details").hasAuthority("ADMIN")
                .antMatchers("/properties-management").hasAuthority("ADMIN")
                .antMatchers("/auction-items-management").hasAuthority("ADMIN")
                .antMatchers("/login").permitAll()
                .antMatchers("/auction-items").permitAll()
                .anyRequest().authenticated()
                .and().formLogin().permitAll();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/h2-console/**");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception{
        authenticationManagerBuilder.userDetailsService(userLoginDetailsService).passwordEncoder(NoOpPasswordEncoder.getInstance());
    }

}