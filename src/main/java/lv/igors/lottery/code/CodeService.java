package lv.igors.lottery.code;

import lv.igors.lottery.lottery.Lottery;
import lv.igors.lottery.lottery.LotteryException;
import lv.igors.lottery.lottery.LotteryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CodeService {
    private CodeDAO codeDAO;

    public CodeService(CodeDAO codeDAO, LotteryService lotteryService) {
        this.codeDAO = codeDAO;
    }

    public boolean checkWinnerCode(Code code, String lotteryWinningCode) throws CodeException {
        Code winnerCode = getCodeByParticipatingCode(lotteryWinningCode);


        if(winnerCode.equals(code)) {
            return true;
        }else{
            throw new CodeException("Invalid input data or the code is not yours.");
        }
    }

    public List<Code> getAllCodesByLotteryId(Long id) {

        return new ArrayList<>(codeDAO.findAllByLotteryId(id.toString()));
    }

    public Code getCodeByParticipatingCode(String code) throws CodeException {
        Optional<Code> possibleCode = codeDAO.findCodeByParticipatingCode(code);

        if (possibleCode.isPresent()) {
            return possibleCode.get();
        } else {
            throw new CodeException("Invalid input data or the code is not yours.");
        }
    }


}
