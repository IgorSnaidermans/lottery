package lv.igors.lottery.code;

import lombok.AllArgsConstructor;
import lv.igors.lottery.statusResponse.Responses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class CodeEntityManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(CodeEntityManager.class);
    private final CodeDAO codeDAO;

    public Code getCodeByParticipatingCodeAndLotteryId(String code, Long id) throws CodeDoesntExistException {
        LOGGER.info("Getting code information for code:" + code);
        Optional<Code> possibleCode = codeDAO.findCodeByParticipatingCodeAndLotteryId(code, id);

        if (possibleCode.isPresent()) {
            return possibleCode.get();
        } else {
            LOGGER.warn("Could not find code information for code:" + code);
            throw new CodeDoesntExistException(Responses.CODE_NON_EXIST.getResponse());
        }
    }

    public List<Code> getAllCodesByLotteryId(Long id) {
        LOGGER.info("Getting all codes information by lottery id #" + id);
        return new ArrayList<>(codeDAO.findCodesByLotteryId(id));
    }

    public String getEmailByCodeAndLotteryId(String code, Long id) throws CodeDoesntExistException {
        LOGGER.info("Getting email by code" + code + " and lottery id #" + id);

        Optional<Code> possibleCode = codeDAO.findCodeByParticipatingCodeAndLotteryId(code,
                id);

        if (possibleCode.isPresent()) {
            return possibleCode.get().getOwnerEmail();
        } else {
            LOGGER.warn("Could not find code information for code:" + code);
            throw new CodeDoesntExistException(Responses.CODE_NON_EXIST.getResponse());
        }
    }

    public Code findCodeByParticipatingCodeAndLotteryId(String participatingCode, Long lotteryId) throws CodeDoesntExistException {
        Optional<Code> possibleCode = codeDAO.findCodeByParticipatingCodeAndLotteryId(participatingCode, lotteryId);
        if (possibleCode.isPresent()) {
            return possibleCode.get();
        } else {
            throw new CodeDoesntExistException("No code");
        }
    }

    public void save(Code code) {
        codeDAO.save(code);
    }
}
