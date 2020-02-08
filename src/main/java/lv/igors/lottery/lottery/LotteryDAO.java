package lv.igors.lottery.lottery;

import lv.igors.lottery.lottery.Lottery;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LotteryDAO extends CrudRepository<Lottery, Long> {
    Optional<Lottery> findByTitle(String title);
}
