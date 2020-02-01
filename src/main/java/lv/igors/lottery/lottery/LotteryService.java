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

    public void newLottery(String title, int limit) throws LotteryException {
        LocalDateTime currentTimeStamp = LocalDateTime.now();

        if (lotteryDAO.findByTitle(title).isPresent()) {
            throw new LotteryException("This lottery title already exist");
        }

        Lottery lottery = Lottery.builder()
                .active(true)
                .title(title)
                .startTimeStamp(currentTimeStamp)
                .limit(limit)
                .build();

        lotteryDAO.save(lottery);
    }

    public void registerCode(Long id, Code code) throws LotteryException {
        Lottery lottery = getLottery(id);

        if (!lottery.isActive() || lottery.getParticipants() == lottery.getLimit()) /*todo check for same code*/ {
            throw new LotteryException("This lottery is still not started");
        }

        //todo code service
    }

    public void stopRegistration(Long id) throws LotteryException {
        Lottery lottery = getLottery(id);

        lottery.setActive(false);
    }

    public String chooseWinner(Long id) throws LotteryException {
        Lottery lottery = getLottery(id);

        if (!lottery.isActive() || lottery.getWinnerCode().equals("")) {
            Random winnerChooser = new Random();
            List<Code> participatingCodes = codeService.getAllCodesByLotteryId(id);
            int winnerCodeInList = winnerChooser.nextInt(lottery.getLimit());
            return participatingCodes.get(winnerCodeInList).getParticipatingCode();
        } else if (lottery.isActive()) {
            throw new LotteryException("To choose winner, registration should be stopped");
        } else if (!lottery.getWinnerCode().equals("")) {
            throw new LotteryException("The winner is already chosen");
        }
        return "";
    }

    public boolean getWinnerStatus(Code requestedCode) throws LotteryException, CodeException {
        Lottery lottery = getLottery(requestedCode.getLotteryId());

        String lotteryWinningCode = lottery.getWinnerCode();

        if (lottery.getWinnerCode().equals("")) {
            throw new LotteryException("The winner is not chosen still");
        }

        return codeService.checkWinnerCode(requestedCode, lotteryWinningCode);
    }

    public List<Lottery> getAllLotteries() {
        List<Lottery> lotteryList = new ArrayList<>();

        for (Lottery lottery : lotteryDAO.findAll()) {
            lotteryList.add(lottery);
        }

        return lotteryList;
    }

    public Lottery getLottery(Long id) throws LotteryException {

        Optional<Lottery> possibleLottery = lotteryDAO.findById(id.toString());

        if (possibleLottery.isPresent()) {
            return possibleLottery.get();
        } else {
            throw new LotteryException("Lottery with id #" + id + " does not exist");
        }
    }
}
