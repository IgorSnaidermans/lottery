package lv.igors.lottery.lottery;

import lv.igors.lottery.code.Code;
import lv.igors.lottery.code.CodeException;
import lv.igors.lottery.code.CodeService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
//todo enum with errors.

@Service
public class LotteryService {
    private LotteryDAO lotteryDAO;
    private CodeService codeService;

    public LotteryService(LotteryDAO lotteryDAO, CodeService codeService) {
        this.lotteryDAO = lotteryDAO;
        this.codeService = codeService;
    }

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
        Lottery lottery = getLottery(registrationDTO.getLotteryId());

        if (!lottery.isActive()) {
            return StatusResponse.builder()
                    .status("Fail")
                    .reason("Registration is not started")
                    .build();
        } else if (lottery.getParticipants() >= lottery.getLimit()) {
            return StatusResponse.builder()
                    .status("Fail")
                    .reason("Too many participants")
                    .build();
        }

        codeService.addCode(Code.builder()
                .lotteryId(registrationDTO.getLotteryId())
                .ownerEmail(registrationDTO.getEmail())
                .participatingCode(registrationDTO.getCode())
                .build());

        return StatusResponse.builder()
                .id(lottery.getId())
                .status("OK")
                .build();
    }

    public StatusResponse stopRegistration(Long id) throws LotteryException {
        Lottery lottery = getLottery(id);

        if (lottery.isActive()) {
            lottery.setActive(false);
            return StatusResponse.builder()
                    .status("OK")
                    .build();
        }else {
            return StatusResponse.builder()
                    .status("FAIL")
                    .reason("Lottery is already stopped")
                    .build();
        }
    }

    public StatusResponse chooseWinner(Long id) throws LotteryException {
        Lottery lottery = getLottery(id);

        if (!lottery.isActive() && null == lottery.getWinnerCode()) {
            Random winnerChooser = new Random();
            List<Code> participatingCodes = codeService.getAllCodesByLotteryId(id);
            int winnerCodeInList = winnerChooser.nextInt(lottery.getLimit());

            String winnerCode = participatingCodes.get(winnerCodeInList).getParticipatingCode();
            return StatusResponse.builder()
                    .status("OK")
                    .winnerCode(winnerCode)
                    .build();
        } else if (lottery.isActive()) {
            return StatusResponse.builder()
                    .status("FAIL")
                    .reason("Lottery is active")
                    .build();
        } else if (null != lottery.getWinnerCode()) {
            return StatusResponse.builder()
                    .status("FAIL")
                    .reason("Lottery is finished")
                    .build();
        }
        return StatusResponse.builder()
                .status("FAIL")
                .reason("Unexpected error")
                .build();
    }

    public StatusResponse getWinnerStatus(Code requestedCode) {

        try {
            Lottery lottery = getLottery(requestedCode.getLotteryId());

            String lotteryWinningCode = lottery.getWinnerCode();

            if (null == lottery.getWinnerCode()) {
                return StatusResponse.builder()
                        .status("PENDING")
                        .build();
            }
            if (codeService.checkWinnerCode(requestedCode, lotteryWinningCode)) {
                return StatusResponse.builder()
                        .status("WIN")
                        .build();
            } else {
                return StatusResponse.builder()
                        .status("LOSE")
                        .build();
            }
        } catch (CodeException | LotteryException e) {
            return StatusResponse.builder()
                    .status("ERROR" + e.getMessage())
                    .build();
        }
    }

    public List<Lottery> getAllLotteries() {
        List<Lottery> lotteryList = new ArrayList<>();

        for (Lottery lottery : lotteryDAO.findAll()) {
            lotteryList.add(lottery);
        }

        return lotteryList;
    }

    public Lottery getLottery(Long id) throws LotteryException {

        Optional<Lottery> possibleLottery = lotteryDAO.findById(id);

        if (possibleLottery.isPresent()) {
            return possibleLottery.get();
        } else {
            throw new LotteryException("Lottery with id #" + id + " does not exist");
        }
    }
}
