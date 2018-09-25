package be.gerard.starter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * WebMvcSecurityConfig
 *
 * @author bartgerard
 * @version v0.0.1
 */
@Configuration
@EnableWebSecurity
//@EnableWebFluxSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(
            final AuthenticationManagerBuilder auth
    ) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("user").password("password").roles("USER")
                .and()
                .withUser("admin").password("admin").roles("USER", "ADMIN", "READER", "WRITER");
    }

    @Override
    protected void configure(
            final HttpSecurity http
    ) throws Exception {
        http.headers()
                .frameOptions()
                .disable() // FAR FROM IDEAL
                //.sameOrigin()
                .and()
                .authorizeRequests()
                .antMatchers("/index.html", "/").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().permitAll();//.authenticated();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            final ServerHttpSecurity http
    ) {
        return http.authorizeExchange()
                .pathMatchers("/actuator/**").permitAll()
                .anyExchange().authenticated()
                .and()
                .build();
    }

}
