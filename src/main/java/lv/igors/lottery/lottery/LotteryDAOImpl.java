package lv.igors.lottery.lottery;

import lombok.AllArgsConstructor;
import lv.igors.lottery.lottery.dto.LotteryAdminDTO;
import lv.igors.lottery.lottery.dto.LotteryDTO;
import lv.igors.lottery.lottery.dto.StatisticsDTO;
import lv.igors.lottery.statusResponse.Responses;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
@Transactional
public class LotteryDAOImpl implements LotteryDAO {
    SessionFactory sessionFactory;
    DateTimeFormatter formatter;

    @Override
    public List<LotteryDTO> getAllLotteriesToLotteryDTO() {
        Session session = sessionFactory.getCurrentSession();

        List<Lottery> lotteryList = session.createQuery("from lotteries", Lottery.class).getResultList();
        List<LotteryDTO> lotteryDTOS = new ArrayList<>();

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.YY HH:mm");

        for (Lottery lottery : lotteryList) {
            LotteryDTO lotteryDTO = LotteryDTO.builder()
                    .id(lottery.getId())
                    .active(lottery.isActive())
                    .startTimestamp(lottery.getStartTimestamp().format(dateTimeFormatter))
                    .title(lottery.getTitle())
                    .build();

            if (null != lottery.getEndTimestamp()) {
                lotteryDTO.setEndTimestamp(lottery.getEndTimestamp().format(dateTimeFormatter));
            }

            lotteryDTOS.add(lotteryDTO);
        }

        return lotteryDTOS;
    }

    @Override
    public List<LotteryAdminDTO> getAllLotteriesAdminDTO() {
        Session session = sessionFactory.getCurrentSession();
        List<Lottery> lotteryList = session.createQuery("from lotteries", Lottery.class).getResultList();
        List<LotteryAdminDTO> adminDTOS = new ArrayList<>();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.YY HH:mm");

        for (Lottery lottery : lotteryList) {

            LotteryAdminDTO lotteryAdminDTO = LotteryAdminDTO.builder()
                    .startTimestampFormatted(lottery.getStartTimestamp().format(dateTimeFormatter))
                    .active(lottery.isActive())
                    .id(lottery.getId())
                    .participants(lottery.getParticipants())
                    .participantsLimit(lottery.getParticipantsLimit())
                    .title(lottery.getTitle())
                    .winnerCode(lottery.getWinnerCode())
                    .build();

            if (null != lottery.getEndTimestamp()) {
                lotteryAdminDTO.setEndTimestampFormatted(lottery.getEndTimestamp().format(dateTimeFormatter));
            }
            adminDTOS.add(lotteryAdminDTO);
        }


        return adminDTOS;
    }


    @Override
    public Lottery getLotteryById(Long id) throws LotteryException {
        Lottery lottery = sessionFactory.getCurrentSession().get(Lottery.class, id);

        Optional<Lottery> possibleLottery = Optional.ofNullable(lottery);

        if (possibleLottery.isPresent()) {
            return possibleLottery.get();
        } else {
            throw new LotteryException(Responses.LOTTERY_NON_EXIST.getResponse());
        }
    }

    @Override
    public List<StatisticsDTO> getAllLotteryStatisticsDTO() {
        Session session = sessionFactory.getCurrentSession();
        List<Lottery> lotteryList = session.createQuery("from lotteries", Lottery.class).getResultList();
        List<StatisticsDTO> statisticsList = new ArrayList<>();

        for (Lottery lottery : lotteryList) {
            StatisticsDTO statisticsDTO = StatisticsDTO.builder()
                    .id(lottery.getId())
                    .title(lottery.getTitle())
                    .participants(lottery.getParticipants())
                    .startTimestamp(lottery.getStartTimestamp().format(formatter))
                    .build();

            if (null != lottery.getEndTimestamp()) {
                statisticsDTO.setEndTimestamp(lottery.getEndTimestamp().format(formatter));
            }

            statisticsList.add(statisticsDTO);

        }
        return statisticsList;
    }

    @Override
    public Lottery findByTitle(String title) throws LotteryException {
        Session session = sessionFactory.getCurrentSession();

        Query<Lottery> query = session.createQuery("from lotteries l where l.title='" + title + "'", Lottery.class);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            throw new LotteryException("No such Lottery");
        }
    }

    @Override
    public void save(Lottery lottery) {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(lottery);
    }
}
