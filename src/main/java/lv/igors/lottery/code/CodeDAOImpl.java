package lv.igors.lottery.code;

import lombok.AllArgsConstructor;
import lv.igors.lottery.statusResponse.Responses;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.List;

@AllArgsConstructor
@Repository
public class CodeDAOImpl implements CodeDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(CodeDAOImpl.class);
    private final SessionFactory sessionFactory;

    @Override
    @Transactional
    public List<Code> getAllCodesByLotteryId(Long id) {
        LOGGER.info("Getting all codes information by lottery id #" + id);
        Session session = sessionFactory.getCurrentSession();

        return session.createQuery("from codes c where c.lottery='" + id + "'", Code.class).getResultList();
    }

    @Override
    @Transactional
    public Code getCodeByParticipatingCodeAndLotteryId(String participatingCode, Long lotteryId) throws CodeDoesntExistException {
        Session session = sessionFactory.getCurrentSession();
        Query<Code> query =
                session.createQuery("from codes c where c.lottery='" + lotteryId +
                        "' and c.participatingCode='" + participatingCode + "'");

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            LOGGER.warn("Could not find code information for code:" + participatingCode + " & lotteryId=" + lotteryId);
            throw new CodeDoesntExistException(Responses.CODE_NON_EXIST.getResponse());
        }
    }

    @Override
    @Transactional
    public void save(Code code) {
        Session session = sessionFactory.openSession();
        session.save(code);
    }
}
