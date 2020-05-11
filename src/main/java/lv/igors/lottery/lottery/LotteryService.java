package lv.igors.lottery.lottery;

import lombok.RequiredArgsConstructor;
import lv.igors.lottery.code.Code;
import lv.igors.lottery.code.CodeService;
import lv.igors.lottery.lottery.dto.*;
import lv.igors.lottery.statusResponse.Responses;
import lv.igors.lottery.statusResponse.StatusResponse;
import lv.igors.lottery.statusResponse.StatusResponseManager;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class LotteryService {
    private final CodeService codeService;
    private final LotteryDAO lotteryDAO;
    private final Clock clock;
    private final StatusResponseManager statusResponseManager;

    public LocalDateTime getCurrentTimeStamp() {
        return LocalDateTime.now(clock);
    }

    public StatusResponse newLottery(NewLotteryDTO newLotteryDTO) {

        if (tryFindSimilarLotteryTitle(newLotteryDTO)) {
            return statusResponseManager.buildFailWithMessage(Responses.LOTTERY_TITLE_EXISTS.getResponse());
        }

        Lottery lottery = buildNewLottery(newLotteryDTO);
        lotteryDAO.save(lottery);
        return statusResponseManager.buildOkWithLotteryId(lottery.getId());
    }

    private boolean tryFindSimilarLotteryTitle(NewLotteryDTO newLotteryDTO) {
        try {
            lotteryDAO.findByTitle(newLotteryDTO.getTitle());

            return true;
        } catch (LotteryException e) {
            return false;
        }

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
        Lottery lottery;

        try {
            lottery = lotteryDAO.getLotteryById(registrationDTO.getLotteryId());

            checkLotteryRegistrationPossibility(lottery, registrationDTO);
        } catch (LotteryException e) {
            return statusResponseManager.buildFailWithMessage(e.getMessage());
        }

        Code newRegistrationCode = buildCode(registrationDTO, lottery);


        StatusResponse statusResponse = codeService.addCode(newRegistrationCode);

        if (statusResponse.getStatus().equals(Responses.OK.getResponse())) {
            lottery.setParticipants(lottery.getParticipants() + 1);
            lottery.addCode(newRegistrationCode);
            lotteryDAO.save(lottery);
        }
        return statusResponse;
    }

    private void checkLotteryRegistrationPossibility(Lottery lottery, RegistrationDTO registrationDTO) throws LotteryException {
        if (!lottery.isActive()) {
            throw new LotteryException(Responses.LOTTERY_REGISTER_INACTIVE.getResponse());
        } else if (lottery.getParticipants() >= lottery.getParticipantsLimit()) {
            throw new LotteryException(Responses.LOTTERY_EXCESS_PARTICIPANTS.getResponse());
        }
    }

    private Code buildCode(RegistrationDTO registrationDTO, Lottery lottery) {
        return Code.builder()
                .participatingCode(registrationDTO.getCode())
                .ownerEmail(registrationDTO.getEmail())
                .lottery(lottery)
                .build();
    }

    public StatusResponse endRegistration(LotteryIdDTO lotteryId) {
        Lottery lottery;

        try {
            lottery = lotteryDAO.getLotteryById(lotteryId.getLotteryId());
        } catch (LotteryException e) {
            return statusResponseManager.buildFailWithMessage(e.getMessage());
        }

        if (lottery.isActive()) {
            stopLotteryRegistration(lottery);
            return statusResponseManager.buildOk();
        } else {
            return statusResponseManager.buildFailWithMessage(Responses.LOTTERY_REGISTER_INACTIVE.getResponse());
        }
    }

    private void stopLotteryRegistration(Lottery lottery) {
        lottery.setActive(false);
        lottery.setEndTimestamp(getCurrentTimeStamp());
        lotteryDAO.save(lottery);
    }

    @Transactional
    public StatusResponse chooseWinner(LotteryIdDTO id) {
        Lottery lottery;

        try {
            lottery = lotteryDAO.getLotteryById(id.getLotteryId());
            checkChooseWinnerPossibility(lottery);
        } catch (LotteryException e) {
            return statusResponseManager.buildFailWithMessage(e.getMessage());
        }

        Code winnerCode = determineWinner(lottery.getRegisteredCodes());

        lottery.setWinnerCode(winnerCode);
        lotteryDAO.save(lottery);
        return statusResponseManager.buildOkWithWinnerCode(winnerCode.toString());
    }

    private void checkChooseWinnerPossibility(Lottery lottery) throws LotteryException {
        if (lottery.isActive()) {
            throw new LotteryException(Responses.LOTTERY_REGISTER_ACTIVE.getResponse());
        } else if (null != lottery.getWinnerCode()) {
            throw new LotteryException(Responses.LOTTERY_FINISHED.getResponse());
        } else if (lottery.getParticipants() <= 0) {
            throw new LotteryException(Responses.LOTTERY_NO_PARTICIPANTS.getResponse());
        }
    }

    private Code determineWinner(List<Code> participatingCodes) {
        int winnerPosition = generateWinner(participatingCodes.size());
        return participatingCodes.get(winnerPosition);
    }

    private int generateWinner(int participatorCount) {
        Random winnerChooser = new Random();
        return winnerChooser.nextInt(participatorCount);
    }

    public StatusResponse getWinnerStatus(CheckStatusDTO registrationDTO) {
        Lottery lottery;

        try {
            lottery = lotteryDAO.getLotteryById(registrationDTO.getLotteryId());
        } catch (LotteryException e) {
            return statusResponseManager.buildFailWithMessage(e.getMessage());
        }

        Code lotteryWinningCode = lottery.getWinnerCode();
        if (null == lotteryWinningCode) {
            return statusResponseManager.buildWithMessage(Responses.LOTTERY_STATUS_PENDING.getResponse());
        }

        Code requestedCode = buildCode(registrationDTO, lottery);


        return codeService.checkWinnerCode(lottery.getWinnerCode(), requestedCode);
    }

    private Code buildCode(CheckStatusDTO registrationDTO, Lottery lottery) {
        return Code.builder()
                .lottery(lottery)
                .participatingCode(registrationDTO.getCode())
                .ownerEmail(registrationDTO.getEmail())
                .build();
    }

    public List<LotteryAdminDTO> getAllLotteriesAdminDTO() {
        return lotteryDAO.getAllLotteriesAdminDTO();
    }

    public List<StatisticsDTO> getAllLotteryStatisticsDTO() {
        return lotteryDAO.getAllLotteryStatisticsDTO();
    }

    public Lottery getLotteryById(Long id) throws LotteryException {
        return lotteryDAO.getLotteryById(id);
    }

    public List<LotteryDTO> getAllLotteriesToLotteryDTO() {
        return lotteryDAO.getAllLotteriesToLotteryDTO();
    }
}
