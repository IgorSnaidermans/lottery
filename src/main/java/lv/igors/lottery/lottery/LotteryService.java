package lv.igors.lottery.lottery;

import lombok.RequiredArgsConstructor;
import lv.igors.lottery.code.Code;
import lv.igors.lottery.code.CodeDTO;
import lv.igors.lottery.code.CodeDoesntExistException;
import lv.igors.lottery.code.CodeService;
import lv.igors.lottery.lottery.dto.*;
import lv.igors.lottery.statusResponse.Responses;
import lv.igors.lottery.statusResponse.StatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
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
    private LocalDateTime currentTimeStamp;


    public LocalDateTime getCurrentTimeStamp() {
        return currentTimeStamp = LocalDateTime.now(clock);
    }

    public StatusResponse newLottery(NewLotteryDTO newLotteryDTO) {
        LOGGER.info("Creating new lottery for " + newLotteryDTO);
        if (lotteryDAO.findByTitle(newLotteryDTO.getTitle()).isPresent()) {
            LOGGER.warn("Create lottery failed due to title already exist for " + newLotteryDTO);
            return StatusResponse.builder()
                    .status("Fail")
                    .reason("This lottery title already exist")
                    .build();
        }

        Lottery lottery = Lottery.builder()
                .active(true)
                .title(newLotteryDTO.getTitle())
                .startTimestamp(getCurrentTimeStamp())
                .participantsLimit(newLotteryDTO.getLimit())
                .build();
        lotteryDAO.save(lottery);
        LOGGER.info("Created lottery successfully for " + newLotteryDTO);
        return StatusResponse.builder()
                .id(lottery.getId())
                .status("OK")
                .build();
    }

    public StatusResponse registerCode(RegistrationDTO registrationDTO) {
        LOGGER.info("Registering code for " + registrationDTO);
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

        Code code = Code.builder()
                .participatingCode(registrationDTO.getCode())
                .ownerEmail(registrationDTO.getEmail())
                .lotteryId(registrationDTO.getLotteryId())
                .build();

        LOGGER.info("Code registration from " + registrationDTO.getEmail() + ".Code:"
                + registrationDTO.getCode() + ". Lottery #" + lottery.getId());

        StatusResponse statusResponse = codeService.addCode(code);

        if (statusResponse.getStatus().equals(Responses.OK.getResponse())) {
            lottery.setParticipants(lottery.getParticipants() + 1);
            lotteryDAO.save(lottery);
        }
        return statusResponse;
    }

    public StatusResponse stopRegistration(LotteryIdDTO lotteryId) {
        LOGGER.info("Stopping registration for " + lotteryId);
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
            lottery.setEndTimestamp(getCurrentTimeStamp());
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
        LOGGER.info("Choosing winner for lottery #" + id);
        Lottery lottery;

        try {
            lottery = getLotteryById(id.getLotteryId());
        } catch (LotteryException e) {
            return StatusResponse.builder()
                    .status(Responses.FAIL.getResponse())
                    .reason(e.getMessage())
                    .build();
        }

        if (!lottery.isActive() && null == lottery.getWinnerCode() && lottery.getParticipants() > 0) {
            Random winnerChooser = new Random();
            List<Code> participatingCodes = codeService.getAllCodesByLotteryId(id.getLotteryId());
            int winnerCodeInList = winnerChooser.nextInt(lottery.getParticipants());

            String winnerCode = participatingCodes.get(winnerCodeInList).getParticipatingCode();

            LOGGER.info("Lottery #" + lottery.getId() + ". Chosen winner code: " + winnerCode);
            lottery.setWinnerCode(winnerCode);
            lotteryDAO.save(lottery);
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
        } else if (lottery.getParticipants() <= 0) {
            LOGGER.warn("Unsuccessful choose winner due to no participants in it. Lottery #" + lottery.getId());
            return StatusResponse.builder()
                    .status(Responses.FAIL.getResponse())
                    .reason(Responses.LOTTERY_NO_PARTICIPANTS.getResponse())
                    .build();
        }

        LOGGER.error("Unsuccessful choose winner due to unexpected error. Lottery #" + lottery.getId());
        return StatusResponse.builder()
                .status(Responses.FAIL.getResponse())
                .reason(Responses.UNKNOWN_ERROR.getResponse())
                .build();
    }

    public StatusResponse getWinnerStatus(RegistrationDTO registrationDTO) {
        LOGGER.info("Getting winner status for" + registrationDTO);
        try {
            Lottery lottery = getLotteryById(registrationDTO.getLotteryId());
            String lotteryWinningCode = lottery.getWinnerCode();

            CodeDTO codeDTO = CodeDTO.builder()
                    .lotteryId(registrationDTO.getLotteryId())
                    .code(registrationDTO.getCode())
                    .email(registrationDTO.getEmail())
                    .lotteryStartTimestamp(lottery.getStartTimestamp())
                    .build();

            if (null == lottery.getWinnerCode()) {
                LOGGER.info("Responded winner is pending. Lottery #" + lottery.getId() +
                        ". To " + registrationDTO.getEmail());
                return StatusResponse.builder()
                        .status(Responses.LOTTERY_STATUS_PENDING.getResponse())
                        .build();
            }

            LOGGER.info("Responded winner status. Lottery #" + lottery.getId() +
                    ". To " + registrationDTO.getEmail());
            return codeService.checkWinnerCode(codeDTO, lotteryWinningCode);
        } catch (LotteryException e) {
            return StatusResponse.builder()
                    .status(e.getMessage())
                    .build();
        }
    }

    public List<StatisticsDTO> getAllLotteryStatistics() {
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
                lotteryAdminDTO.setWinnerEmail(codeService.getEmailByCode(lottery.getWinnerCode()));
            } catch (CodeDoesntExistException ignored) {

            }

            if (null != lottery.getWinnerCode()) {
                try {
                    lottery.setWinnerCode(codeService.getCodeByParticipatingCode(lottery.getWinnerCode()).getOwnerEmail());
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

}
