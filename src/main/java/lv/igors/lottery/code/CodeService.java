package lv.igors.lottery.code;

import lombok.RequiredArgsConstructor;
import lv.igors.lottery.statusResponse.Responses;
import lv.igors.lottery.statusResponse.StatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CodeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CodeService.class);
    private final CodeDAO codeDAO;

    private boolean validateCode(CodeDTO codeDTO) {
        if (codeDTO.getCode().length() != 16) {
            return false;
        }
        String requestedCode = codeDTO.getCode();
        String datePart = requestedCode.substring(0, 6);
        String emailPart = requestedCode.substring(6, 8);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("DDMMYY");
        String lotteryStartDate = codeDTO.getLotteryStartTimestamp().format(formatter);
        String emailLetterCount;

        if (codeDTO.getEmail().length() < 10) {
            emailLetterCount = "0" + codeDTO.getEmail().length();
        } else {
            emailLetterCount = "" + codeDTO.getEmail().length();
        }

        return datePart.equals(lotteryStartDate) && (emailPart.equals(emailLetterCount));
    }

    public StatusResponse addCode(CodeDTO codeDTO) {
        if (!validateCode(codeDTO)) {
            LOGGER.info("Code did not pass validation " + codeDTO.toString());
            return StatusResponse.builder()
                    .status(Responses.FAIL.getResponse())
                    .reason(Responses.CODE_INVALID.getResponse())
                    .build();
        }

        if (!findSimilarCodes(codeDTO.getCode())) {
            LOGGER.info("Code saved " + codeDTO.toString());

            Code code = Code.builder()
                    .ownerEmail(codeDTO.getEmail())
                    .participatingCode(codeDTO.getCode())
                    .lotteryId(codeDTO.getLotteryId())
                    .build();

            codeDAO.save(code);
            return StatusResponse.builder()
                    .status(Responses.OK.getResponse())
                    .build();
        }
        LOGGER.warn("Unsuccessful code save due to already exist" + codeDTO.getCode());
        return StatusResponse.builder()
                .status(Responses.FAIL.getResponse())
                .reason(Responses.CODE_EXIST.getResponse())
                .build();
    }

    public boolean findSimilarCodes(String code) {
        Optional<Code> possibleCode = codeDAO.findCodeByParticipatingCode(code);

        return possibleCode.isPresent();
    }

    public StatusResponse checkWinnerCode(CodeDTO codeDTO, String lotteryWinningCode) {
        if (!validateCode(codeDTO)) {
            return StatusResponse.builder()
                    .status(Responses.FAIL.getResponse())
                    .reason(Responses.CODE_INVALID.getResponse())
                    .build();
        }

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
        } catch (CodeException e) {
            return StatusResponse.builder()
                    .status(Responses.FAIL.getResponse())
                    .reason(e.getMessage())
                    .build();
        }

        if (winnerCode.equals(requestedCode)) {
            return StatusResponse.builder()
                    .status(Responses.CODE_WIN.getResponse())
                    .build();
        } else {
            return StatusResponse.builder()
                    .status(Responses.CODE_LOSE.getResponse())
                    .build();
        }

    }

    private boolean checkCodeOwner(Code code) throws CodeException {
        String requestedCodeOwnerEmail = getCodeByParticipatingCode(code.getParticipatingCode())
                .getOwnerEmail();
        return requestedCodeOwnerEmail.equals(code.getOwnerEmail());
    }

    public Code getCodeByParticipatingCode(String code) throws CodeException {
        Optional<Code> possibleCode = codeDAO.findCodeByParticipatingCode(code);

        if (possibleCode.isPresent()) {
            return possibleCode.get();
        } else {
            LOGGER.warn("Could not find code " + code);
            throw new CodeException(Responses.CODE_NON_EXIST.getResponse());
        }
    }

    public List<Code> getAllCodesByLotteryId(Long id) {
        return new ArrayList<>(codeDAO.findCodesByLotteryId(id));
    }
}
