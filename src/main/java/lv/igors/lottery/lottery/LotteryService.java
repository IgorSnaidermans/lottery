package lv.igors.lottery.lottery;

import lombok.RequiredArgsConstructor;
import lv.igors.lottery.code.Code;
import lv.igors.lottery.code.CodeDTO;
import lv.igors.lottery.code.CodeService;
import lv.igors.lottery.statusResponse.Responses;
import lv.igors.lottery.statusResponse.StatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class LotteryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LotteryService.class);
    private final LotteryDAO lotteryDAO;
    private final CodeService codeService;

    public StatusResponse newLottery(String title, int limit) {
        LocalDateTime currentTimeStamp = LocalDateTime.now();

        if (lotteryDAO.findByTitle(title).isPresent()) {
            return StatusResponse.builder()
                    .status("Fail")
                    .reason("This lottery title already exist")
                    .build();
        }

        Lottery lottery = Lottery.builder()
                .active(true)
                .title(title)
                .startTimeStamp(currentTimeStamp)
                .limit(limit)
                .build();

        lotteryDAO.save(lottery);

        return StatusResponse.builder()
                .id(lottery.getId())
                .status("OK")
                .build();
    }

    public StatusResponse registerCode(RegistrationDTO registrationDTO) throws LotteryException {
        Lottery lottery = getLotteryById(registrationDTO.getLotteryId());

        if (!lottery.isActive()) {
            LOGGER.warn("Unsuccessful code register due to lottery inactive from " + registrationDTO.getEmail() +
                    ". Lottery #" + registrationDTO.getLotteryId());
            return StatusResponse.builder()
                    .status(Responses.FAIL.getResponse())
                    .reason(Responses.LOTTERY_REGISTER_INACTIVE.getResponse())
                    .build();
        } else if (lottery.getParticipants() >= lottery.getLimit()) {
            LOGGER.warn("Unsuccessful code register due to excess participants from " + registrationDTO.getEmail() +
                    ". Lottery #" + registrationDTO.getLotteryId());
            return StatusResponse.builder()
                    .status(Responses.FAIL.getResponse())
                    .reason(Responses.LOTTERY_EXCESS_PARTICIPANTS.getResponse())
                    .build();
        }

        CodeDTO codeDTO = CodeDTO.builder()
                .code(registrationDTO.getCode())
                .email(registrationDTO.getEmail())
                .lotteryId(registrationDTO.getLotteryId())
                .lotteryStartTimestamp(lottery.getStartTimeStamp())
                .build();

        LOGGER.info("Code registration from " + registrationDTO.getEmail() + ".Code:"
                + registrationDTO.getCode() + ". Lottery #" + lottery.getId());

        return codeService.addCode(codeDTO);
    }

    public StatusResponse stopRegistration(Long lotteryId) throws LotteryException {
        Lottery lottery = getLotteryById(lotteryId);

        if (lottery.isActive()) {
            LOGGER.info("Lottery #" + lottery.getId() + " stopped");
            lottery.setActive(false);
            return StatusResponse.builder()
                    .status(Responses.OK.getResponse())
                    .build();
        } else {
            LOGGER.warn("Unsuccessful lottery register stop due to already stopped. Lottery #" + lottery.getId());
            return StatusResponse.builder()
                    .status(Responses.FAIL.getResponse())
                    .reason(Responses.LOTTERY_REGISTER_INACTIVE.getResponse())
                    .build();
        }
    }

    public StatusResponse chooseWinner(Long id) throws LotteryException {
        Lottery lottery = getLotteryById(id);

        if (!lottery.isActive() && null == lottery.getWinnerCode()) {
            Random winnerChooser = new Random();
            List<Code> participatingCodes = codeService.getAllCodesByLotteryId(id);
            int winnerCodeInList = winnerChooser.nextInt(lottery.getLimit());

            String winnerCode = participatingCodes.get(winnerCodeInList).getParticipatingCode();
            LOGGER.info("Lottery #" + lottery.getId() + ". Chosen winner code: " + winnerCode);
            return StatusResponse.builder()
                    .status(Responses.OK.getResponse())
                    .winnerCode(winnerCode)
                    .build();
        } else if (lottery.isActive()) {
            LOGGER.warn("Unsuccessful choose winner due to registration active. Lottery #" + lottery.getId());
            return StatusResponse.builder()
                    .status(Responses.FAIL.getResponse())
                    .reason(Responses.LOTTERY_REGISTER_ACTIVE.getResponse())
                    .build();
        } else if (null != lottery.getWinnerCode()) {
            LOGGER.warn("Unsuccessful choose winner due to winner chosen. Lottery #" + lottery.getId());
            return StatusResponse.builder()
                    .status(Responses.FAIL.getResponse())
                    .reason(Responses.LOTTERY_FINISHED.getResponse())
                    .build();
        }

        LOGGER.error("Unsuccessful choose winner due to unexpected error. Lottery #" + lottery.getId());
        return StatusResponse.builder()
                .status(Responses.FAIL.getResponse())
                .reason(Responses.UNKNOWN_ERROR.getResponse())
                .build();
    }

    public StatusResponse getWinnerStatus(RegistrationDTO registrationDTO) {
        try {
            Lottery lottery = getLotteryById(registrationDTO.getLotteryId());
            String lotteryWinningCode = lottery.getWinnerCode();

            if (null == lottery.getWinnerCode()) {
                LOGGER.info("Responded winner is pending. Lottery #" + lottery.getId() +
                        ". To " + registrationDTO.getEmail());
                return StatusResponse.builder()
                        .status(Responses.LOTTERY_STATUS_PENDING.getResponse())
                        .build();
            }

            CodeDTO codeDTO = CodeDTO.builder()
                    .code(registrationDTO.getCode())
                    .email(registrationDTO.getEmail())
                    .lotteryStartTimestamp(lottery.getStartTimeStamp())
                    .build();

            LOGGER.info("Responded winner status. Lottery #" + lottery.getId() +
                    ". To " + registrationDTO.getEmail());
            return codeService.checkWinnerCode(codeDTO, lotteryWinningCode);
        } catch (LotteryException e) {
            return StatusResponse.builder()
                    .status(e.getMessage())
                    .build();
        }
    }

    public List<StatisticsDTO> getAllLotteryStatistics(){
        List<StatisticsDTO> statisticsList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("DD.MM.YY HH:mm");

        for(Lottery lottery: lotteryDAO.findAll()){
            statisticsList.add(StatisticsDTO.builder()
                    .id(lottery.getId())
                    .title(lottery.getTitle())
                    .participants(lottery.getParticipants())
                    .startTimestamp(lottery.getStartTimeStamp().format(formatter))
                    .endTimestamp(lottery.getEndTimeStamp().format(formatter))
                    .build());
        }
        return statisticsList;
    }

    public List<Lottery> getAllLotteries() {
        List<Lottery> lotteryList = new ArrayList<>();

        for (Lottery lottery : lotteryDAO.findAll()) {
            lotteryList.add(lottery);
        }

        return lotteryList;
    }

    public Lottery getLotteryById(Long id) throws LotteryException {

        Optional<Lottery> possibleLottery = lotteryDAO.findById(id);

        if (possibleLottery.isPresent()) {
            return possibleLottery.get();
        } else {
            LOGGER.warn("Could not find lottery #" + id);
            throw new LotteryException(Responses.LOTTERY_NON_EXIST.getResponse());
        }
    }
}
