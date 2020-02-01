package lv.igors.lottery.lottery;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LotteryDAO extends CrudRepository<Lottery, String> {
    Optional<Lottery> findByTitle(String title);
}
