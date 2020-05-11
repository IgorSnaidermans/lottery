package lv.igors.lottery.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {


    @Configuration
    @Order(1)
    @RequiredArgsConstructor
    public static class ApiWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {
        private final LotteryAuthenticationProvider lotteryAuthenticationProvider;

        protected void configure(HttpSecurity http) throws Exception {
            http.sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

            http
                    .antMatcher("/rest/admin/**")
                    .csrf().disable()
                    .authenticationProvider(lotteryAuthenticationProvider)
                    .authorizeRequests()
                    .antMatchers("/rest/admin/**").hasAuthority("ADMIN")
                    .antMatchers("/rest/register", "/rest/status",
                            "/rest/stats", "/rest/lottery/**").permitAll()
                    .and()
                    .httpBasic();
        }
    }


    @Configuration
    @Order(2)
    @RequiredArgsConstructor
    public static class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

        private final LotteryAuthenticationProvider lotteryAuthenticationProvider;

        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests()
                    .and()
                    .authenticationProvider(lotteryAuthenticationProvider)
                    .csrf().disable()
                    .authorizeRequests()
                    .antMatchers("/", "/register", "/status", "/stats", "/lottery/**").permitAll()
                    .and()
                    .authorizeRequests()
                    .antMatchers("/admin/**").hasAuthority("ADMIN")
                    .and()
                    .formLogin()
                    .loginPage("/admin-login").permitAll()
                    .loginProcessingUrl("/login").permitAll()
                    .defaultSuccessUrl("/admin")
                    .failureUrl("/admin-login?error=true")
                    .and()
                    .logout().permitAll();
        }
    }

}
