package lv.igors.lottery.lottery;

import lombok.AllArgsConstructor;
import lv.igors.lottery.code.CodeDoesntExistException;
import lv.igors.lottery.code.CodeService;
import lv.igors.lottery.lottery.dto.LotteryAdminDTO;
import lv.igors.lottery.lottery.dto.LotteryDTO;
import lv.igors.lottery.lottery.dto.StatisticsDTO;
import lv.igors.lottery.statusResponse.Responses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class LotteryEntityManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(LotteryEntityManager.class);
    private final LotteryDAO lotteryDAO;
    private final CodeService codeService;

    public List<LotteryDTO> getAllLotteriesToLotteryDTO() {
        LOGGER.info("Getting all lotteries for user");
        List<LotteryDTO> lotteryList = new ArrayList<>();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.YY HH:mm");

        for (Lottery lottery : lotteryDAO.findAll()) {
            LotteryDTO lotteryDTO = LotteryDTO.builder()
                    .id(lottery.getId())
                    .active(lottery.isActive())
                    .startTimestamp(lottery.getStartTimestamp().format(dateTimeFormatter))
                    .title(lottery.getTitle())
                    .build();

            if (null != lottery.getEndTimestamp()) {
                lotteryDTO.setEndTimestamp(lottery.getEndTimestamp().format(dateTimeFormatter));
            }

            lotteryList.add(lotteryDTO);
        }

        return lotteryList;
    }

    public List<LotteryAdminDTO> getAllLotteriesAdminDTO() {
        LOGGER.info("Getting all lotteries for admin");
        List<LotteryAdminDTO> lotteryList = new ArrayList<>();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.YY HH:mm");

        for (Lottery lottery : lotteryDAO.findAll()) {

            LotteryAdminDTO lotteryAdminDTO = LotteryAdminDTO.builder()
                    .startTimestampFormatted(lottery.getStartTimestamp().format(dateTimeFormatter))
                    .active(lottery.isActive())
                    .id(lottery.getId())
                    .participants(lottery.getParticipants())
                    .participantsLimit(lottery.getParticipantsLimit())
                    .title(lottery.getTitle())
                    .winnerCode(lottery.getWinnerCode())
                    .build();

            try {
                lotteryAdminDTO.setWinnerEmail(codeService.getEmailByCodeAndLotteryId(lottery.getWinnerCode(),
                        lottery.getId()));
            } catch (CodeDoesntExistException ignored) {

            }

            if (null != lottery.getWinnerCode()) {
                try {
                    lottery.setWinnerCode(codeService.getCodeByParticipatingCodeAndLotteryId(lottery.getWinnerCode(),
                            lottery.getId())
                            .getOwnerEmail());
                } catch (CodeDoesntExistException ignored) {
                }

                if (null != lottery.getEndTimestamp()) {
                    lotteryAdminDTO.setEndTimestampFormatted(lottery.getEndTimestamp().format(dateTimeFormatter));
                }
            }

            lotteryList.add(lotteryAdminDTO);
        }

        return lotteryList;
    }

    public Lottery getLotteryById(Long id) throws LotteryException {
        LOGGER.info("Getting lottery #" + id);
        Optional<Lottery> possibleLottery = lotteryDAO.findById(id);

        if (possibleLottery.isPresent()) {
            return possibleLottery.get();
        } else {
            LOGGER.warn("Could not find lottery #" + id);
            throw new LotteryException(Responses.LOTTERY_NON_EXIST.getResponse());
        }
    }

    public List<StatisticsDTO> getAllLotteryStatisticsDTO() {
        LOGGER.info("Getting lottery statistics");
        List<StatisticsDTO> statisticsList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.YY HH:mm");

        for (Lottery lottery : lotteryDAO.findAll()) {
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

    public Lottery findByTitle(String title) throws LotteryException {
        Optional<Lottery> possibleLottery = lotteryDAO.findByTitle(title);
        if (possibleLottery.isPresent()) {
           return possibleLottery.get();
        }else {
            throw new LotteryException("No such Lottery");
        }
    }

    public void save(Lottery lottery) {
        lotteryDAO.save(lottery);
    }
}
