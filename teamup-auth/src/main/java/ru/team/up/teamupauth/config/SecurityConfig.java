package ru.team.up.teamupauth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import ru.team.up.teamupauth.repository.UserRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private SuccessHandler successHandler;
    private UserDetailsService userDetailsService;

    public SecurityConfig(SuccessHandler successHandler,UserDetailsService userDetailsService){
        this.successHandler = successHandler;
        this.userDetailsService= userDetailsService;
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        //auth.userDetailsService (userDetailsService);
        auth.inMemoryAuthentication()
                .withUser("User")
                .password("$2a$12$YiROvFRAbeIWWz5XHJAfAedV5hVfb4NFKZs5Atw0yrB3yldjRTVmK")
                .authorities("ROLE_USER")
                .and()
                .withUser("Admin")
                .password("$2a$12$KwmQ4mBkrb70yebG.yjvKu2Cj5nf6iemWOvo6vOtNExZgMGgpdJte")
                .authorities("ROLE_ADMIN")
                .and ()
                .withUser ("Moderator")
                .password ("$2a$12$VeRlXvgPPjIzNayOwopCmOu01sjPQWCidCRa5wGEDiQWmUuElQDYG")
                .authorities ("ROLE_MODERATOR");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/login", "/oauth2/authorization/google").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/user").hasRole("USER")
                .antMatchers("/moderator").hasRole("MODERATOR")
                .anyRequest().authenticated()
                .and().formLogin()
                .successHandler(successHandler)
                .and ().oauth2Login()
                .successHandler (new SuccessHandler());

        http.logout ()//URL выхода из системы безопасности Spring - только POST. Вы можете поддержать выход из системы без POST, изменив конфигурацию Java
                .logoutRequestMatcher (new AntPathRequestMatcher ("/logout"))//выход из системы гет запрос на /logout
                .logoutSuccessUrl ("/login")//успешный выход из системы
                .and().csrf().disable();
    }
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}