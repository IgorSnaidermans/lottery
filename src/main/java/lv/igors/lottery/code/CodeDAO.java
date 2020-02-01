package lv.igors.lottery.code;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CodeDAO extends CrudRepository<Code, Long> {
    List<Code> findAllByLotteryId(String lotteryId);
    Optional<Code> findCodeByParticipatingCode(String code);
}
