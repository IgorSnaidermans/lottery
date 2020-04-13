package lv.igors.lottery.code;

import java.util.List;

public interface CodeDAO {
    List<Code> getAllCodesByLotteryId(Long id) throws CodeDoesntExistException;

    Code getCodeByParticipatingCodeAndLotteryId(String participatingCode, Long lotteryId) throws CodeDoesntExistException;

    void save(Code code);
}
