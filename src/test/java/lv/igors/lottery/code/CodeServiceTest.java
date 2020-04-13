package lv.igors.lottery.code;

import lv.igors.lottery.code.dto.CodeDTO;
import lv.igors.lottery.lottery.Lottery;
import lv.igors.lottery.statusResponse.StatusResponse;
import lv.igors.lottery.statusResponse.StatusResponseManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CodeServiceTest {

    private final String EMAIL = "some@mail.com";
    private final Long LOTTERY_ID = 0L;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMYY");
    LocalDateTime localDateTime = LocalDateTime.now();
    String lotteryStartDate = localDateTime.format(formatter);
    private final String REG_CODE = lotteryStartDate + "1392837465";
    final int LIMIT = 1000;
    @Mock
    private CodeDAOImpl codeDAOImpl;
    private StatusResponseManager statusResponseManager;
    private CodeService codeService;
    private Code code;
    private Code winnerCode;
    private Lottery validLottery;

    @BeforeEach
    void setUp() {
        statusResponseManager = new StatusResponseManager();
        codeService = new CodeService(codeDAOImpl, statusResponseManager);

        validLottery = Lottery.builder()
                .id(LOTTERY_ID)
                .active(true)
                .participantsLimit(LIMIT)
                .participants(LIMIT - 1)
                .build();

        winnerCode = Code.builder()
                .ownerEmail("test@test.lv")
                .participatingCode(REG_CODE)
                .lottery(validLottery)
                .build();

        code = Code.builder()
                .participatingCode(REG_CODE)
                .lottery(validLottery)
                .ownerEmail(EMAIL)
                .build();
    }

    @Test
    void ShouldThrowException_BecauseNoCodeFoundInRepository() throws CodeDoesntExistException {
        when(codeDAOImpl.getCodeByParticipatingCodeAndLotteryId(any(), any()))
                .thenThrow(CodeDoesntExistException.class);

        assertThrows(CodeDoesntExistException.class, () ->
                codeService.getCodeByParticipatingCodeAndLotteryId(any(), any()));
    }

    @Test
    void addCode_ShouldSuccess() throws CodeDoesntExistException {
        when(codeDAOImpl.getCodeByParticipatingCodeAndLotteryId(any(), any()))
                .thenThrow(CodeDoesntExistException.class);
        StatusResponse result = codeService.addCode(code);
        assertEquals("OK", result.getStatus());
    }

    @Test
    void shouldValidateCodeWithEmailLessThan10Symbols() throws CodeDoesntExistException {
        code.setOwnerEmail("12@456.89");
        code.setParticipatingCode(lotteryStartDate + "0912345678");
        when(codeDAOImpl.getCodeByParticipatingCodeAndLotteryId(any(), any()))
                .thenThrow(CodeDoesntExistException.class);

        StatusResponse result = codeService.addCode(code);
        assertEquals("OK", result.getStatus());
    }

    @Test
    void addCode_ShouldFailDueToCodeAlreadyExist() throws CodeDoesntExistException {

        when(codeDAOImpl.getCodeByParticipatingCodeAndLotteryId(any(), any()))
                .thenReturn(code);

        StatusResponse result = codeService.addCode(code);

        assertEquals("Fail", result.getStatus());
        assertEquals("Code already exists", result.getReason());
    }

    @Test
    void checkWinnerCode_ShouldReturnWin() throws CodeDoesntExistException {

        when(codeDAOImpl.getCodeByParticipatingCodeAndLotteryId(any(), any()))
                .thenReturn(winnerCode);

        StatusResponse statusResponse = codeService.checkWinnerCode(winnerCode, winnerCode);

        assertEquals("WIN", statusResponse.getStatus());
    }

    @Test
    void checkWinnerCode_ShouldReturnLose() throws CodeDoesntExistException {
        code.setParticipatingCode("0502981212345678");

        Long lotteryId = winnerCode.getLottery().getId();

        when(codeDAOImpl.getCodeByParticipatingCodeAndLotteryId(code.getParticipatingCode(), lotteryId))
                .thenReturn(code);

        StatusResponse statusResponse = codeService.checkWinnerCode(winnerCode, code);

        assertEquals("LOSE", statusResponse.getStatus());
        assertNull(statusResponse.getReason());
    }

    @Test
    void checkWinnerCode_ShouldReturnCodeIsNotYours() throws CodeDoesntExistException {
        winnerCode.setOwnerEmail("random");
        code.setOwnerEmail("aaa");
        when(codeDAOImpl.getCodeByParticipatingCodeAndLotteryId(any(), any()))
                .thenReturn(winnerCode);


        StatusResponse statusResponse = codeService.checkWinnerCode(winnerCode, code);

        assertEquals("Fail", statusResponse.getStatus());
        assertEquals("The code is not yours", statusResponse.getReason());
    }
}

