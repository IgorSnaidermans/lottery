package lv.igors.lottery.code;

import lv.igors.lottery.StatusResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CodeService {
    private CodeDAO codeDAO;

    public CodeService(CodeDAO codeDAO) {
        this.codeDAO = codeDAO;
    }

    public StatusResponse addCode(Code code) {
        if (!findSimilarCodes(code.getParticipatingCode())) {
            codeDAO.save(code);
            return StatusResponse.builder()
                    .status("OK")
                    .build();
        }
        return StatusResponse.builder()
                .status("Fail")
                .reason("Code already exists")
                .build();
    }

    public boolean findSimilarCodes(String code) {
        Optional<Code> possibleCode = codeDAO.findCodeByParticipatingCode(code);

        return possibleCode.isPresent();
    }

    public StatusResponse checkWinnerCode(Code code, String lotteryWinningCode) {
        Code winnerCode;

        try {
            if (!checkCodeOwner(code)) {
                return StatusResponse.builder()
                        .status("Fail")
                        .reason("The code is not yours")
                        .build();
            }
            winnerCode = getCodeByParticipatingCode(lotteryWinningCode);
        } catch (CodeException e) {
            return StatusResponse.builder()
                    .status("Fail")
                    .reason(e.getMessage())
                    .build();
        }

        if (winnerCode.equals(code)) {
            return StatusResponse.builder()
                    .status("WIN")
                    .build();
        } else {
            return StatusResponse.builder()
                    .status("LOSE")
                    .build();
        }

    }

    private boolean checkCodeOwner(Code code) throws CodeException {
        String requestedCodeOwnerEmail = getCodeByParticipatingCode(code.getParticipatingCode())
                .getOwnerEmail();
        return requestedCodeOwnerEmail.equals(code.getOwnerEmail());
    }

    public Code getCodeByParticipatingCode(String lotteryWinningCode) throws CodeException {
        Optional<Code> possibleCode = codeDAO.findCodeByParticipatingCode(lotteryWinningCode);

        if (possibleCode.isPresent()) {
            return possibleCode.get();
        } else {
            throw new CodeException("Code doesnt exists");
        }
    }

    public List<Code> getAllCodesByLotteryId(Long id) {
        return new ArrayList<>(codeDAO.findAllByLotteryId(id.toString()));
    }
}
