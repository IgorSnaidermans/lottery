package lv.igors.lottery.lottery;

import lombok.RequiredArgsConstructor;
import lv.igors.lottery.code.Code;
import lv.igors.lottery.code.CodeService;
import lv.igors.lottery.code.dto.CodeDTO;
import lv.igors.lottery.lottery.dto.*;
import lv.igors.lottery.statusResponse.Responses;
import lv.igors.lottery.statusResponse.StatusResponse;
import lv.igors.lottery.statusResponse.StatusResponseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class LotteryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LotteryService.class);
    private final CodeService codeService;
    private final LotteryEntityManager lotteryEntityManager;
    private final Clock clock;
    private final StatusResponseManager statusResponseManager;

    public LocalDateTime getCurrentTimeStamp() {
        return LocalDateTime.now(clock);
    }

    public StatusResponse newLottery(NewLotteryDTO newLotteryDTO) {
        LOGGER.info("Creating new lottery for " + newLotteryDTO);

        if (tryFindSimilarLotteryTitle(newLotteryDTO)) {
            return statusResponseManager.buildFailWithMessage(Responses.LOTTERY_TITLE_EXISTS.getResponse());
        }

        LOGGER.info("Created lottery successfully for " + newLotteryDTO);
        Lottery lottery = buildNewLottery(newLotteryDTO);
        lotteryEntityManager.save(lottery);
        return statusResponseManager.buildOkWithLotteryId(lottery.getId());
    }

    private boolean tryFindSimilarLotteryTitle(NewLotteryDTO newLotteryDTO) {
        try {
            lotteryEntityManager.findByTitle(newLotteryDTO.getTitle());
            LOGGER.warn("Create lottery failed due to title already exist for " + newLotteryDTO);
            return true;
        } catch (LotteryException ignored) {

        }
        return false;
    }

    private Lottery buildNewLottery(NewLotteryDTO newLotteryDTO) {
        return Lottery.builder()
                .active(true)
                .title(newLotteryDTO.getTitle())
                .startTimestamp(getCurrentTimeStamp())
                .participantsLimit(newLotteryDTO.getLimit())
                .build();
    }

    public StatusResponse registerCode(RegistrationDTO registrationDTO) {
        LOGGER.info("Registering code for " + registrationDTO);
        Lottery lottery;

        try {
            lottery = lotteryEntityManager.getLotteryById(registrationDTO.getLotteryId());
            checkLotteryRegistrationPossibility(lottery, registrationDTO);
        } catch (LotteryException e) {
            return statusResponseManager.buildFailWithMessage(e.getMessage());
        }

        Code code = buildCode(registrationDTO);

        LOGGER.info("Code registration from " + registrationDTO.getEmail() + ".Code:"
                + registrationDTO.getCode() + ". Lottery #" + lottery.getId());

        StatusResponse statusResponse = codeService.addCode(code);

        if (statusResponse.getStatus().equals(Responses.OK.getResponse())) {
            lottery.setParticipants(lottery.getParticipants() + 1);
            lotteryEntityManager.save(lottery);
        }
        return statusResponse;
    }

    private void checkLotteryRegistrationPossibility(Lottery lottery, RegistrationDTO registrationDTO) throws LotteryException {
        if (!lottery.isActive()) {
            LOGGER.warn("Unsuccessful code register due to lottery inactive from " + registrationDTO.getEmail() +
                    ". Lottery #" + registrationDTO.getLotteryId());
            throw new LotteryException(Responses.LOTTERY_REGISTER_INACTIVE.getResponse());
        } else if (lottery.getParticipants() >= lottery.getParticipantsLimit()) {
            LOGGER.warn("Unsuccessful code register due to excess participants from " + registrationDTO.getEmail() +
                    ". Lottery #" + registrationDTO.getLotteryId());
            throw new LotteryException(Responses.LOTTERY_EXCESS_PARTICIPANTS.getResponse());
        }
    }

    private Code buildCode(RegistrationDTO registrationDTO) {
        return Code.builder()
                .participatingCode(registrationDTO.getCode())
                .ownerEmail(registrationDTO.getEmail())
                .lotteryId(registrationDTO.getLotteryId())
                .build();
    }

    public StatusResponse endRegistration(LotteryIdDTO lotteryId) {
        LOGGER.info("Stopping registration for " + lotteryId);
        Lottery lottery;

        try {
            lottery = lotteryEntityManager.getLotteryById(lotteryId.getLotteryId());
        } catch (LotteryException e) {
            return statusResponseManager.buildFailWithMessage(e.getMessage());
        }

        if (lottery.isActive()) {
            stopLotteryRegistration(lottery);
            return statusResponseManager.buildOk();
        } else {
            LOGGER.warn("Unsuccessful lottery register stop due to already stopped. Lottery #" + lottery.getId());
            return statusResponseManager.buildFailWithMessage(Responses.LOTTERY_REGISTER_INACTIVE.getResponse());
        }
    }

    private void stopLotteryRegistration(Lottery lottery) {
        LOGGER.info("Lottery #" + lottery.getId() + " stopped");
        lottery.setActive(false);
        lottery.setEndTimestamp(getCurrentTimeStamp());
        lotteryEntityManager.save(lottery);
    }

    public StatusResponse chooseWinner(LotteryIdDTO id) {
        LOGGER.info("Choosing winner for lottery #" + id);
        Lottery lottery;

        try {
            lottery = lotteryEntityManager.getLotteryById(id.getLotteryId());
            checkChooseWinnerPossibility(lottery);
        } catch (LotteryException e) {
            return statusResponseManager.buildFailWithMessage(e.getMessage());
        }

        String winnerCode = determineWinner(id.getLotteryId());

        LOGGER.info("Lottery #" + lottery.getId() + ". Chosen winner code: " + winnerCode);
        lottery.setWinnerCode(winnerCode);
        lotteryEntityManager.save(lottery);
        return statusResponseManager.buildOkWithWinnerCode(winnerCode);
    }

    private void checkChooseWinnerPossibility(Lottery lottery) throws LotteryException {
        if (lottery.isActive()) {
            LOGGER.warn("Unsuccessful choose winner due to registration active. Lottery #" + lottery.getId());
            throw new LotteryException(Responses.LOTTERY_REGISTER_ACTIVE.getResponse());
        } else if (null != lottery.getWinnerCode()) {
            LOGGER.warn("Unsuccessful choose winner due to winner chosen. Lottery #" + lottery.getId());
            throw new LotteryException(Responses.LOTTERY_FINISHED.getResponse());
        } else if (lottery.getParticipants() <= 0) {
            LOGGER.warn("Unsuccessful choose winner due to no participants in it. Lottery #" + lottery.getId());
            throw new LotteryException(Responses.LOTTERY_NO_PARTICIPANTS.getResponse());
        }
    }

    private String determineWinner(Long lotteryId) {
        List<Code> participatingCodes = codeService.getAllCodesByLotteryId(lotteryId);
        int winnerPosition = generateWinner(participatingCodes.size());
        return participatingCodes.get(winnerPosition).getParticipatingCode();
    }

    private int generateWinner(int participatorCount) {
        Random winnerChooser = new Random();
        return winnerChooser.nextInt(participatorCount);
    }

    public StatusResponse getWinnerStatus(CheckStatusDTO registrationDTO) {
        LOGGER.info("Getting winner status for" + registrationDTO);
        Lottery lottery;
        try {
            lottery = lotteryEntityManager.getLotteryById(registrationDTO.getLotteryId());
        } catch (LotteryException e) {
            return statusResponseManager.buildFailWithMessage(e.getMessage());
        }

        String lotteryWinningCode = lottery.getWinnerCode();
        if (checkWinnerExist(registrationDTO, lottery, lotteryWinningCode)) {
            return statusResponseManager.buildWithMessage(Responses.LOTTERY_STATUS_PENDING.getResponse());
        }

        CodeDTO codeDTO = buildCode(registrationDTO, lottery);
        LOGGER.info("Responded winner status. Lottery #" + lottery.getId() +
                ". To " + registrationDTO.getEmail());
        return codeService.checkWinnerCode(codeDTO, lotteryWinningCode);
    }

    private boolean checkWinnerExist(CheckStatusDTO registrationDTO, Lottery lottery, String lotteryWinningCode) {
        if (null == lotteryWinningCode) {
            LOGGER.info("Responded winner is pending. Lottery #" + lottery.getId() +
                    ". To " + registrationDTO.getEmail());
            return true;
        }
        return false;
    }


    private CodeDTO buildCode(CheckStatusDTO registrationDTO, Lottery lottery) {
        return CodeDTO.builder()
                .lotteryId(registrationDTO.getLotteryId())
                .code(registrationDTO.getCode())
                .email(registrationDTO.getEmail())
                .lotteryStartTimestamp(lottery.getStartTimestamp())
                .build();
    }

    public List<LotteryAdminDTO> getAllLotteriesAdminDTO() {
        return lotteryEntityManager.getAllLotteriesAdminDTO();
    }

    public List<StatisticsDTO> getAllLotteryStatisticsDTO() {
        return lotteryEntityManager.getAllLotteryStatisticsDTO();
    }

    public Lottery getLotteryById(Long id) throws LotteryException {
        return lotteryEntityManager.getLotteryById(id);
    }

    public List<LotteryDTO> getAllLotteriesToLotteryDTO() {
        return lotteryEntityManager.getAllLotteriesToLotteryDTO();
    }
}
