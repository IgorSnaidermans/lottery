package lv.igors.lottery.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LotteryAuthenticationProvider implements AuthenticationProvider {

    private final UserDAO userDAO;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication.getCredentials() == null) {
            throw new BadCredentialsException("No password was provided");
        }

        Optional<User> possibleUser = userDAO.findByName(authentication.getName());

        if (possibleUser.isPresent()) {
            User user = possibleUser.get();

            String requestedPassword = authentication.getCredentials().toString();

            if (encodedPasswordMatcher(requestedPassword, user.getPassword())) {
                return new UsernamePasswordAuthenticationToken(
                        user.getName(),
                        user.getPassword(),
                        List.of(new SimpleGrantedAuthority("ADMIN")));
            } else {
                throw new BadCredentialsException("Wrong username or password");
            }
        } else {
            throw new BadCredentialsException("Wrong username or password");
        }
    }

    private boolean encodedPasswordMatcher(String requestedPassword, String password) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        return bCryptPasswordEncoder.matches(requestedPassword, password);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
