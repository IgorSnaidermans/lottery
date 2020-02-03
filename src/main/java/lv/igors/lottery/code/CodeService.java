package lv.igors.lottery.code;

import lv.igors.lottery.statusResponse.Responses;
import lv.igors.lottery.statusResponse.StatusResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;

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
                    .status(Responses.OK.getResponse())
                    .build();
        }
        return StatusResponse.builder()
                .status(Responses.FAIL.getResponse())
                .reason(Responses.CODE_EXIST.getResponse())
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

        if (winnerCode.equals(code)) {
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

    public Code getCodeByParticipatingCode(String lotteryWinningCode) throws CodeException {
        Optional<Code> possibleCode = codeDAO.findCodeByParticipatingCode(lotteryWinningCode);

        if (possibleCode.isPresent()) {
            return possibleCode.get();
        } else {
            throw new CodeException(Responses.CODE_NON_EXIST.getResponse());
        }
    }

    public List<Code> getAllCodesByLotteryId(Long id) {
        return new ArrayList<>(codeDAO.findAllByLotteryId(id.toString()));
    }
}
