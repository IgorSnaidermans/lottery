package lv.igors.lottery.lottery;

import lv.igors.lottery.lottery.dto.LotteryAdminDTO;
import lv.igors.lottery.lottery.dto.LotteryDTO;
import lv.igors.lottery.lottery.dto.StatisticsDTO;

import java.util.List;

public interface LotteryDAO {
    List<LotteryDTO> getAllLotteriesToLotteryDTO();

    List<LotteryAdminDTO> getAllLotteriesAdminDTO();

    Lottery getLotteryById(Long id) throws LotteryException;

    List<StatisticsDTO> getAllLotteryStatisticsDTO();

    Lottery findByTitle(String title) throws LotteryException;

    void save(Lottery lottery);
}
