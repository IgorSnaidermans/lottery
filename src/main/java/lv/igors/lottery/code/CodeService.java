package lv.igors.lottery.code;

import lombok.RequiredArgsConstructor;
import lv.igors.lottery.statusResponse.Responses;
import lv.igors.lottery.statusResponse.StatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CodeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CodeService.class);
    private final CodeDAO codeDAO;

    public StatusResponse addCode(Code code) {

        if (!findSimilarCodes(code)) {
            LOGGER.info("Code saved " + code);

            codeDAO.save(code);
            return StatusResponse.builder()
                    .status(Responses.OK.getResponse())
                    .build();
        }
        LOGGER.warn("Unsuccessful code save due to already exist" + code);
        return StatusResponse.builder()
                .status(Responses.FAIL.getResponse())
                .reason(Responses.CODE_EXIST.getResponse())
                .build();
    }

    public boolean findSimilarCodes(Code code) {
        Optional<Code> possibleCode = codeDAO.findCodeByParticipatingCodeAndLotteryId(code.getParticipatingCode(),
                code.getLotteryId());
        return possibleCode.map(value -> value.equals(code)).orElse(false);
    }

    public StatusResponse checkWinnerCode(CodeDTO codeDTO, String lotteryWinningCode) {
        LOGGER.info("Checking winning status for " + codeDTO);

        Code winnerCode;
        Code requestedCode = Code.builder()
                .ownerEmail(codeDTO.getEmail())
                .participatingCode(codeDTO.getCode())
                .lotteryId(codeDTO.getLotteryId())
                .build();

        try {
            if (!checkCodeOwner(requestedCode)) {
                LOGGER.warn("Foreign code was requested by " + codeDTO.toString());
                return StatusResponse.builder()
                        .status(Responses.FAIL.getResponse())
                        .reason(Responses.CODE_FOREIGN_CODE.getResponse())
                        .build();
            }
            winnerCode = getCodeByParticipatingCode(lotteryWinningCode);
        } catch (CodeDoesntExistException e) {
            return StatusResponse.builder()
                    .status(Responses.FAIL.getResponse())
                    .reason(e.getMessage())
                    .build();
        }

        if (winnerCode.equalsWithoutDatabaseId(requestedCode)) {
            return StatusResponse.builder()
                    .status(Responses.CODE_WIN.getResponse())
                    .build();
        } else {
            return StatusResponse.builder()
                    .status(Responses.CODE_LOSE.getResponse())
                    .build();
        }

    }

    private boolean checkCodeOwner(Code code) throws CodeDoesntExistException {
        LOGGER.info("Checking code owner for " + code);
        String requestedCodeOwnerEmail = getCodeByParticipatingCodeAndLotteryId(code.getParticipatingCode(),
                code.getLotteryId())
                .getOwnerEmail();
        return requestedCodeOwnerEmail.equals(code.getOwnerEmail());
    }

    public Code getCodeByParticipatingCode(String code) throws CodeDoesntExistException {
        LOGGER.info("Getting code information for code:" + code);
        Optional<Code> possibleCode = codeDAO.findCodeByParticipatingCode(code);

        if (possibleCode.isPresent()) {
            return possibleCode.get();
        } else {
            LOGGER.warn("Could not find code information for code:" + code);
            throw new CodeDoesntExistException(Responses.CODE_NON_EXIST.getResponse());
        }
    }

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
}
