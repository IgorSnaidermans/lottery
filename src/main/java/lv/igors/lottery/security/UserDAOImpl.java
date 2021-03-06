package lv.igors.lottery.security;

import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class UserDAOImpl implements UserDAO {
    private final SessionFactory sessionFactory;

    @Override
    @Transactional
    public Optional<User> findByName(String name) {
        Session session = sessionFactory.getCurrentSession();

        try {
            Query<User> query = session.createQuery("from users u where u.username='" + name + "'");
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException exception) {
            return Optional.empty();
        }
    }
}