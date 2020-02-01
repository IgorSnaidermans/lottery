package lv.igors.lottery.lottery;

import lv.igors.lottery.code.Code;
import lv.igors.lottery.code.CodeService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
//todo Exception and enum with errors.

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

        if (lotteryDAO.findByTitle().isPresent()) {
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

        if(!lottery.isActive() || lottery.getParticipants()==lottery.getLimit()) /*todo check for same code*/ {
            throw new LotteryException("This lottery is still not started");
        }

        //todo lottery service
    }

    public void stopRegistration(Long id) throws LotteryException {
        Lottery lottery = getLottery(id);

        lottery.setActive(false);
    }

    public String chooseWinner(Long id) throws LotteryException {
        Lottery lottery = getLottery(id);

        if (lottery.isActive() || lottery.getWinnerCodeId().equals("")) {
            Random winnerChooser = new Random();
            List<Code> participatingCodes = codeService.getCodesWithLotteryId(id);
            int winnerCodeInList = winnerChooser.nextInt(lottery.getLimit());
            return participatingCodes.get(winnerCodeInList).getCode();
        }else {
            throw new LotteryException("To choose winner, registration should be stopped");
        }

        //todo winner is already chosen
    }

    public boolean getWinnerStatus(Long id, String requestedCode, String userEmail) throws LotteryException {
        Lottery lottery = getLottery(id);

        //todo equal code with parameters
        return false;
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
