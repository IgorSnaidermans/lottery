package lv.igors.lottery.security;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDAO {
    Optional<User> findByName(String name);
}
