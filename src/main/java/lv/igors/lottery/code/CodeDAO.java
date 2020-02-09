package lv.igors.lottery.code;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CodeDAO extends CrudRepository<Code, Long> {
    //@Query("SELECT * FROM codes WHERE lotteryid= :lotteryId")
    List<Code> findCodesByLotteryId(@Param("lotteryId") Long lotteryId);

    //@Query("SELECT * FROM codes WHERE participatingcode= :participatingCode")
    Optional<Code> findCodeByParticipatingCode(@Param("participatingCode") String participatingCode);
}
