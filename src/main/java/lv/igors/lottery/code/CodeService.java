package lv.igors.lottery.code;

import lombok.RequiredArgsConstructor;
import lv.igors.lottery.code.dto.CodeDTO;
import lv.igors.lottery.statusResponse.Responses;
import lv.igors.lottery.statusResponse.StatusResponse;
import lv.igors.lottery.statusResponse.StatusResponseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CodeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CodeService.class);
    private final CodeEntityManager codeEntityManager;
    private final StatusResponseManager statusResponseManager;

    public StatusResponse addCode(Code code) {

        if (!findSimilarCodes(code)) {
            LOGGER.info("Code saved " + code);
            codeEntityManager.save(code);
            return statusResponseManager.buildOk();
        }
        LOGGER.warn("Unsuccessful code save due to already exist" + code);
        return statusResponseManager.buildFailWithMessage(Responses.CODE_EXIST.getResponse());
    }

    private boolean findSimilarCodes(Code code) {
        try {
            codeEntityManager.findCodeByParticipatingCodeAndLotteryId(code.getParticipatingCode(), code.getLotteryId());
            return true;
        } catch (CodeDoesntExistException e){
            return false;
        }

    }

    public StatusResponse checkWinnerCode(CodeDTO codeDTO, String lotteryWinningCode) {
        LOGGER.info("Checking winning status for " + codeDTO);

        Code winnerCode;
        Code requestedCode = buildCode(codeDTO);

        try {
            if (!checkCodeOwner(requestedCode)) {
                LOGGER.warn("Foreign code was requested by " + codeDTO.toString());
                return statusResponseManager.buildFailWithMessage(Responses.CODE_FOREIGN_CODE.getResponse());
            }
            winnerCode = codeEntityManager.getCodeByParticipatingCodeAndLotteryId(lotteryWinningCode,
                    requestedCode.getLotteryId());
        } catch (CodeDoesntExistException e) {
            return statusResponseManager.buildFailWithMessage(e.getMessage());
        }

        return checkWin(winnerCode, requestedCode);
    }

    private Code buildCode(CodeDTO codeDTO) {
        return Code.builder()
                    .ownerEmail(codeDTO.getEmail())
                    .participatingCode(codeDTO.getCode())
                    .lotteryId(codeDTO.getLotteryId())
                    .build();
    }

    private boolean checkCodeOwner(Code code) throws CodeDoesntExistException {
        LOGGER.info("Checking code owner for " + code);
        String requestedCodeOwnerEmail = codeEntityManager.getCodeByParticipatingCodeAndLotteryId(code.getParticipatingCode(),
                code.getLotteryId())
                .getOwnerEmail();
        return requestedCodeOwnerEmail.equals(code.getOwnerEmail());
    }

    private StatusResponse checkWin(Code winnerCode, Code requestedCode) {
        if (winnerCode.equals(requestedCode)) {
            return statusResponseManager.buildWithMessage(Responses.CODE_WIN.getResponse());
        } else {
            return statusResponseManager.buildWithMessage(Responses.CODE_LOSE.getResponse());
        }
    }


    public String getEmailByCodeAndLotteryId(String winnerCode, Long id) throws CodeDoesntExistException {
        return codeEntityManager.getEmailByCodeAndLotteryId(winnerCode,id);
    }

    public Code getCodeByParticipatingCodeAndLotteryId(String winnerCode, Long id) throws CodeDoesntExistException {
        return codeEntityManager.getCodeByParticipatingCodeAndLotteryId(winnerCode,id);
    }

    public List<Code> getAllCodesByLotteryId(Long lotteryId) {
        return codeEntityManager.getAllCodesByLotteryId(lotteryId);
    }
}