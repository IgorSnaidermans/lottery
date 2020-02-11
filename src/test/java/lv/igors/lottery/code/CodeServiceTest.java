package lv.igors.lottery.code;

import lv.igors.lottery.statusResponse.StatusResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CodeServiceTest {

    @Mock
    private CodeDAO codeDao;
    private CodeService codeService;
    private CodeDTO codeDTO;
    private Code winnerCode;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMYY");
    LocalDateTime localDateTime = LocalDateTime.now();
    String lotteryStartDate = localDateTime.format(formatter);

    private final String REG_CODE = lotteryStartDate + "1392837465";
    private final String EMAIL = "some@mail.com";
    private final Long LOTTERY_ID = 0L;

    @BeforeEach
    void setUp() {
        codeService = new CodeService(codeDao);

        winnerCode = Code.builder()
                .ownerEmail("test@test.lv")
                .participatingCode(REG_CODE)
                .lotteryId(0L)
                .build();

        codeDTO = CodeDTO.builder()
                .code(REG_CODE)
                .lotteryId(LOTTERY_ID)
                .lotteryStartTimestamp(localDateTime)
                .email(EMAIL)
                .build();
    }

    @Test
    void ShouldThrowException_BecauseNoCodeFoundInRepository(){
        when(codeDao.findCodeByParticipatingCode(any()))
                .thenReturn(Optional.empty());

        assertThrows(CodeException.class, () -> codeService.getCodeByParticipatingCode(any()));
    }

    @Test
    void addCode_ShouldSuccess() {
        StatusResponse result = codeService.addCode(codeDTO);
        assertEquals("OK", result.getStatus());
    }

    @Test
    void shouldValidateCodeWithEmailLessThan10Symbols(){
        codeDTO.setEmail("12@456.89");
        codeDTO.setCode(lotteryStartDate + "0912345678");

        StatusResponse result = codeService.addCode(codeDTO);
        assertEquals("OK", result.getStatus());
    }

    @Test
    void addCode_ShouldFailDueToCodeAlreadyExist(){
        Code code = codeDtoToCode();

        when(codeDao.findCodeByParticipatingCode(any()))
                .thenReturn(Optional.ofNullable(code));

        StatusResponse result = codeService.addCode(codeDTO);

        assertEquals("Fail", result.getStatus());
        assertEquals("Code already exists", result.getReason());
    }

    @Test
    void checkWinnerCode_ShouldReturnWin(){
        Code code = codeDtoToCode();

        when(codeDao.findCodeByParticipatingCode(any()))
                .thenReturn(Optional.ofNullable(code));

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
    void checkWinnerCode_ShouldReturnLose(){
        Code code = codeDtoToCode();
        code.setParticipatingCode("0502981212345678");

        when(codeDao.findCodeByParticipatingCode(winnerCode.getParticipatingCode()))
                .thenReturn(Optional.ofNullable(winnerCode));
        when(codeDao.findCodeByParticipatingCode(codeDTO.getCode()))
                .thenReturn(Optional.ofNullable(code));


        StatusResponse statusResponse = codeService.checkWinnerCode(codeDTO, winnerCode.getParticipatingCode());

        assertEquals("LOSE", statusResponse.getStatus());
        assertNull(statusResponse.getReason());
    }

    @Test
    void checkWinnerCode_ShouldReturnCodeIsNotYours(){
        when(codeDao.findCodeByParticipatingCode(any()))
                .thenReturn(Optional.ofNullable(winnerCode));


        StatusResponse statusResponse = codeService.checkWinnerCode(codeDTO, winnerCode.getParticipatingCode());

        assertEquals("Fail", statusResponse.getStatus());
        assertEquals("The code is not yours", statusResponse.getReason());
    }

}

