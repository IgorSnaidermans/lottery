package lv.igors.lottery.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
    private final LotteryAuthenticationProvider lotteryAuthenticationProvider;

    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .and()
                .csrf().disable()
                .authenticationProvider(lotteryAuthenticationProvider)
                .authorizeRequests()
                .antMatchers("/admin/**").permitAll()
                .antMatchers("/**", "/register", "/status", "/stats", "/lottery/**").permitAll()
                .and()
                .formLogin().loginPage("/admin-login");
    }
}
