package lv.igors.lottery.code;

import lv.igors.lottery.StatusResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CodeServiceTest {

    @Mock
    private CodeDAO codeDao;
    private CodeService codeService;
    private Code code;
    private Code winnerCode;


    @BeforeEach
    void setUp() {
        codeService = new CodeService(codeDao);

        final String REG_CODE = "0502981392837465";
        final String EMAIL = "some@mail.com";
        final Long LOTTERY_ID = 0L;

        winnerCode = Code.builder()
                .ownerEmail("test@test.lv")
                .participatingCode("123")
                .lotteryId(0L)
                .build();

        code = Code.builder()
                .participatingCode(REG_CODE)
                .lotteryId(LOTTERY_ID)
                .ownerEmail(EMAIL)
                .build();
    }

    @Test
    void addCode_ShouldSuccess() {
        StatusResponse result = codeService.addCode(code);
        assertEquals("OK", result.getStatus());
    }

    @Test
    void addCode_ShouldFailDueToCodeAlreadyExist(){
        when(codeDao.findCodeByParticipatingCode(any()))
                .thenReturn(Optional.ofNullable(code));

        StatusResponse result = codeService.addCode(code);

        assertEquals("Fail", result.getStatus());
        assertEquals("Code already exists", result.getReason());
    }

    @Test
    void checkWinnerCode_ShouldReturnWin(){
        when(codeDao.findCodeByParticipatingCode(any()))
                .thenReturn(Optional.ofNullable(code));

        StatusResponse statusResponse = codeService.checkWinnerCode(code, code.getParticipatingCode());

        assertEquals("WIN", statusResponse.getStatus());
    }

    @Test
    void checkWinnerCode_ShouldReturnLose(){

        when(codeDao.findCodeByParticipatingCode(winnerCode.getParticipatingCode()))
                .thenReturn(Optional.ofNullable(winnerCode));
        when(codeDao.findCodeByParticipatingCode(code.getParticipatingCode()))
                .thenReturn(Optional.ofNullable(code));

        StatusResponse statusResponse = codeService.checkWinnerCode(code, winnerCode.getParticipatingCode());

        assertEquals("LOSE", statusResponse.getStatus());
        assertNull(statusResponse.getReason());
    }

    @Test
    void checkWinnerCode_ShouldReturnCodeIsNotYours(){
        when(codeDao.findCodeByParticipatingCode(code.getParticipatingCode()))
                .thenReturn(Optional.ofNullable(winnerCode));

        StatusResponse statusResponse = codeService.checkWinnerCode(code, winnerCode.getParticipatingCode());

        assertEquals("Fail", statusResponse.getStatus());
        assertEquals("The code is not yours", statusResponse.getReason());
    }

}

