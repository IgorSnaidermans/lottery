package lv.igors.lottery.code;

import lv.igors.lottery.code.dto.CodeDTO;
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
    @Mock
    private CodeEntityManager codeEntityManager;
    private StatusResponseManager statusResponseManager;
    private CodeService codeService;
    private Code code;
    private CodeDTO codeDTO;
    private Code winnerCode;

    @BeforeEach
    void setUp() {
        statusResponseManager = new StatusResponseManager();
        codeService = new CodeService(codeEntityManager, statusResponseManager);

        winnerCode = Code.builder()
                .ownerEmail("test@test.lv")
                .participatingCode(REG_CODE)
                .lotteryId(0L)
                .build();

        code = Code.builder()
                .participatingCode(REG_CODE)
                .lotteryId(LOTTERY_ID)
                .participatingCode(EMAIL)
                .build();

        codeDTO = CodeDTO.builder()
                .email(EMAIL)
                .code(REG_CODE)
                .lotteryId(LOTTERY_ID)
                .lotteryStartTimestamp(localDateTime)
                .build();
    }

    @Test
    void ShouldThrowException_BecauseNoCodeFoundInRepository() throws CodeDoesntExistException {
        when(codeEntityManager.getCodeByParticipatingCodeAndLotteryId(any(), any()))
                .thenThrow(CodeDoesntExistException.class);

        assertThrows(CodeDoesntExistException.class, () ->
                codeService.getCodeByParticipatingCodeAndLotteryId(any(), any()));
    }

    @Test
    void addCode_ShouldSuccess() throws CodeDoesntExistException {
        when(codeEntityManager.findCodeByParticipatingCodeAndLotteryId(any(), any()))
                .thenThrow(CodeDoesntExistException.class);
        StatusResponse result = codeService.addCode(code);
        assertEquals("OK", result.getStatus());
    }

    @Test
    void shouldValidateCodeWithEmailLessThan10Symbols() throws CodeDoesntExistException {
        code.setOwnerEmail("12@456.89");
        code.setParticipatingCode(lotteryStartDate + "0912345678");
        when(codeEntityManager.findCodeByParticipatingCodeAndLotteryId(any(), any()))
                .thenThrow(CodeDoesntExistException.class);

        StatusResponse result = codeService.addCode(code);
        assertEquals("OK", result.getStatus());
    }

    @Test
    void addCode_ShouldFailDueToCodeAlreadyExist() throws CodeDoesntExistException {
        Code code = codeDtoToCode();

        when(codeEntityManager.findCodeByParticipatingCodeAndLotteryId(any(), any()))
                .thenReturn(code);

        StatusResponse result = codeService.addCode(code);

        assertEquals("Fail", result.getStatus());
        assertEquals("Code already exists", result.getReason());
    }

    @Test
    void checkWinnerCode_ShouldReturnWin() throws CodeDoesntExistException {
        Code codeCheck = codeDtoToCode();

        when(codeEntityManager.getCodeByParticipatingCodeAndLotteryId(any(), any()))
                .thenReturn(codeCheck);

        StatusResponse statusResponse = codeService.checkWinnerCode(codeDTO, winnerCode.getParticipatingCode());

        assertEquals("WIN", statusResponse.getStatus());
    }

    private Code codeDtoToCode() {
        return Code.builder()
                .lotteryId(codeDTO.getLotteryId())
                .participatingCode(codeDTO.getCode())
                .ownerEmail(codeDTO.getEmail())
                .build();
    }

    @Test
    void checkWinnerCode_ShouldReturnLose() throws CodeDoesntExistException {
        Code code = codeDtoToCode();
        code.setParticipatingCode("0502981212345678");

        when(codeEntityManager.getCodeByParticipatingCodeAndLotteryId(winnerCode.getParticipatingCode(), winnerCode.getLotteryId()))
                .thenReturn(winnerCode);
        when(codeEntityManager.getCodeByParticipatingCodeAndLotteryId(codeDTO.getCode(), codeDTO.getLotteryId()))
                .thenReturn(code);


        StatusResponse statusResponse = codeService.checkWinnerCode(codeDTO, winnerCode.getParticipatingCode());

        assertEquals("LOSE", statusResponse.getStatus());
        assertNull(statusResponse.getReason());
    }

    @Test
    void checkWinnerCode_ShouldReturnCodeIsNotYours() throws CodeDoesntExistException {
        when(codeEntityManager.getCodeByParticipatingCodeAndLotteryId(any(), any()))
                .thenReturn(winnerCode);


        StatusResponse statusResponse = codeService.checkWinnerCode(codeDTO, winnerCode.getParticipatingCode());

        assertEquals("Fail", statusResponse.getStatus());
        assertEquals("The code is not yours", statusResponse.getReason());
    }

}

