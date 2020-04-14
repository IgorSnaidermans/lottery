package lv.igors.lottery.code;

import lombok.RequiredArgsConstructor;
import lv.igors.lottery.statusResponse.Responses;
import lv.igors.lottery.statusResponse.StatusResponse;
import lv.igors.lottery.statusResponse.StatusResponseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CodeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CodeService.class);
    private final CodeDAOImpl codeDAOImpl;
    private final StatusResponseManager statusResponseManager;


    public StatusResponse addCode(Code code) {

        if (!findSimilarCodes(code)) {
            LOGGER.info("Code saved " + code);
            codeDAOImpl.save(code);
            return statusResponseManager.buildOk();
        }
        LOGGER.warn("Unsuccessful code save due to already exist" + code);

        return statusResponseManager.buildFailWithMessage(Responses.CODE_EXIST.getResponse());
    }

    private boolean findSimilarCodes(Code code) {
        try {
            String possibleSimilarCode = codeDAOImpl
                    .getCodeByParticipatingCodeAndLotteryId(code.getParticipatingCode(), code.getLottery().getId())
                    .getParticipatingCode();
            return code.getParticipatingCode().equals(possibleSimilarCode);
        } catch (CodeDoesntExistException e) {
            return false;
        }

    }

    private StatusResponse checkWin(Code winnerCode, Code requestedCode) {
        if (winnerCode.equals(requestedCode)) {
            return statusResponseManager.buildWithMessage(Responses.CODE_WIN.getResponse());
        } else {
            return statusResponseManager.buildWithMessage(Responses.CODE_LOSE.getResponse());
        }
    }

    public Code getCodeByParticipatingCodeAndLotteryId(String winnerCode, Long id) throws CodeDoesntExistException {
        return codeDAOImpl.getCodeByParticipatingCodeAndLotteryId(winnerCode, id);
    }

    public List<Code> getAllCodesByLotteryId(Long lotteryId) {
        return codeDAOImpl.getAllCodesByLotteryId(lotteryId);
    }

    public StatusResponse checkWinnerCode(Code winnerCode, Code requestedCode) {
        LOGGER.info("Checking winning status for " + requestedCode);

        try {
            if (!checkCodeOwner(requestedCode)) {
                LOGGER.warn("Foreign code was requested by " + requestedCode);
                return statusResponseManager.buildFailWithMessage(Responses.CODE_FOREIGN_CODE.getResponse());
            }

        } catch (CodeDoesntExistException e) {
            return statusResponseManager.buildFailWithMessage(e.getMessage());
        }

        return checkWin(winnerCode, requestedCode);
    }

    private boolean checkCodeOwner(Code requestedCodeCredentials) throws CodeDoesntExistException {
        LOGGER.info("Checking code owner for " + requestedCodeCredentials);
        Code codeCredentials = codeDAOImpl.getCodeByParticipatingCodeAndLotteryId(requestedCodeCredentials.getParticipatingCode(),
                requestedCodeCredentials.getLottery().getId());

        return requestedCodeCredentials.getOwnerEmail().equals(codeCredentials.getOwnerEmail());
    }
}