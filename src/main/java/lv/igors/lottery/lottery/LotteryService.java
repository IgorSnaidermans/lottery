package lv.igors.lottery.lottery;

import lombok.RequiredArgsConstructor;
import lv.igors.lottery.code.Code;
import lv.igors.lottery.code.CodeDTO;
import lv.igors.lottery.code.CodeService;
import lv.igors.lottery.lottery.dto.*;
import lv.igors.lottery.statusResponse.Responses;
import lv.igors.lottery.statusResponse.StatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Clock;
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
    private final Clock clock;

    public StatusResponse newLottery(NewLotteryDTO newLotteryDTO) {
        LocalDateTime currentTimeStamp = LocalDateTime.now(clock);

        if (lotteryDAO.findByTitle(newLotteryDTO.getTitle()).isPresent()) {
            return StatusResponse.builder()
                    .status("Fail")
                    .reason("This lottery title already exist")
                    .build();
        }

        Lottery lottery = Lottery.builder()
                .active(true)
                .title(newLotteryDTO.getTitle())
                .startTimestamp(currentTimeStamp)
                .participantsLimit(newLotteryDTO.getLimit())
                .build();

        lotteryDAO.save(lottery);

        return StatusResponse.builder()
                .id(lottery.getId())
                .status("OK")
                .build();
    }

    public StatusResponse registerCode(RegistrationDTO registrationDTO) {
        Lottery lottery;

        try {
            lottery = getLotteryById(registrationDTO.getLotteryId());
        } catch (LotteryException e) {
            return StatusResponse.builder()
                    .status(Responses.FAIL.getResponse())
                    .reason(e.getMessage())
                    .build();
        }

        if (!lottery.isActive()) {
            LOGGER.warn("Unsuccessful code register due to lottery inactive from " + registrationDTO.getEmail() +
                    ". Lottery #" + registrationDTO.getLotteryId());
            return StatusResponse.builder()
                    .status(Responses.FAIL.getResponse())
                    .reason(Responses.LOTTERY_REGISTER_INACTIVE.getResponse())
                    .build();
        } else if (lottery.getParticipants() >= lottery.getParticipantsLimit()) {
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
                .lotteryStartTimestamp(lottery.getStartTimestamp())
                .build();

        LOGGER.info("Code registration from " + registrationDTO.getEmail() + ".Code:"
                + registrationDTO.getCode() + ". Lottery #" + lottery.getId());

        StatusResponse statusResponse = codeService.addCode(codeDTO);

        if (statusResponse.getStatus().equals(Responses.OK.getResponse())) {
            lottery.setParticipants(lottery.getParticipants() + 1);
            lotteryDAO.save(lottery);
        }
        return statusResponse;
    }

    public StatusResponse stopRegistration(LotteryIdDTO lotteryId) {

        Lottery lottery;
        try {
            lottery = getLotteryById(lotteryId.getLotteryId());
        } catch (LotteryException e) {
            return StatusResponse.builder()
                    .status(Responses.FAIL.getResponse())
                    .reason(e.getMessage())
                    .build();
        }

        if (lottery.isActive()) {
            LOGGER.info("Lottery #" + lottery.getId() + " stopped");
            lottery.setActive(false);
            lotteryDAO.save(lottery);
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

    public StatusResponse chooseWinner(LotteryIdDTO id) {
        Lottery lottery;

        try {
            lottery = getLotteryById(id.getLotteryId());
        } catch (LotteryException e) {
            return StatusResponse.builder()
                    .status(Responses.FAIL.getResponse())
                    .reason(e.getMessage())
                    .build();
        }

        if (!lottery.isActive() && null == lottery.getWinnerCode()) {
            Random winnerChooser = new Random();
            List<Code> participatingCodes = codeService.getAllCodesByLotteryId(id.getLotteryId());
            int winnerCodeInList = winnerChooser.nextInt(lottery.getParticipantsLimit());

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

    public StatusResponse getWinnerStatus(CheckStatusDTO checkStatusDTO) {
        try {
            Lottery lottery = getLotteryById(checkStatusDTO.getLotteryId());
            String lotteryWinningCode = lottery.getWinnerCode();

            CodeDTO codeDTO = CodeDTO.builder()
                    .code(checkStatusDTO.getCode())
                    .email(checkStatusDTO.getEmail())
                    .lotteryStartTimestamp(lottery.getStartTimestamp())
                    .build();


            if (!codeService.isCodeValid(codeDTO)) {
                LOGGER.info("Code did not pass validation " + checkStatusDTO.toString());
                return StatusResponse.builder()
                        .status(Responses.FAIL.getResponse())
                        .reason(Responses.CODE_INVALID.getResponse())
                        .build();
            }


            if (null == lottery.getWinnerCode()) {
                LOGGER.info("Responded winner is pending. Lottery #" + lottery.getId() +
                        ". To " + checkStatusDTO.getEmail());
                return StatusResponse.builder()
                        .status(Responses.LOTTERY_STATUS_PENDING.getResponse())
                        .build();
            }

            LOGGER.info("Responded winner status. Lottery #" + lottery.getId() +
                    ". To " + checkStatusDTO.getEmail());
            return codeService.checkWinnerCode(codeDTO, lotteryWinningCode);
        } catch (LotteryException e) {
            return StatusResponse.builder()
                    .status(e.getMessage())
                    .build();
        }
    }

    public List<StatisticsDTO> getAllLotteryStatistics() {
        List<StatisticsDTO> statisticsList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("DD.MM.YY HH:mm");

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

    public List<LotteryDTO> getAllLotteries() {
        List<LotteryDTO> lotteryList = new ArrayList<>();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.YY HH:mm");

        for (Lottery lottery : lotteryDAO.findAll()) {
            LotteryDTO lotteryDTO = LotteryDTO.builder()
                    .id(lottery.getId())
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
