package lv.igors.lottery.lottery;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface LotteryDAO extends CrudRepository<Lottery, String> {
    Optional<Lottery> findByTitle();
}
