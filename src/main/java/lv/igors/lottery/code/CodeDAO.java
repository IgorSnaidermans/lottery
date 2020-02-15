package lv.igors.lottery.code;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CodeDAO extends CrudRepository<Code, Long> {

    List<Code> findCodesByLotteryId(@Param("lotteryId") Long lotteryId);

    Optional<Code> findCodeByParticipatingCode(@Param("participatingCode") String participatingCode);

    Optional<Code> findCodeByParticipatingCodeAndLotteryId(@Param("participatingCode") String participatingCode,
                                                           Long lotteryId);

}
