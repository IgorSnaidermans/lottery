package lv.igors.lottery.code;

import lv.igors.lottery.code.dto.ValidateCodeDTO;
import lv.igors.lottery.lottery.Lottery;
import lv.igors.lottery.lottery.LotteryException;
import lv.igors.lottery.lottery.LotteryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CodeValidatorTest {

    final String REG_CODE = "0502981392837465";
    final String EMAIL = "some@mail.com";
    final Long LOTTERY_ID = 0L;
    DataBinder dataBinder;
    @Mock
    LotteryService lotteryService;
    private BindingResult bindingResult;
    private ValidateCodeDTO validateCodeDTO;
    private CodeValidator codeValidator;
    private LocalDateTime localDateTime;

    @BeforeEach
    void setUp() {
        codeValidator = new CodeValidator(lotteryService);

        validateCodeDTO = ValidateCodeDTO.builder()
                .code(REG_CODE)
                .lotteryId(LOTTERY_ID)
                .email(EMAIL)
                .build();
        dataBinder = new DataBinder(validateCodeDTO);
        bindingResult = dataBinder.getBindingResult();
    }


    @Test
    void validateShouldFail_DueToEmailLettersDoesntMatch() throws LotteryException {
        localDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMYY");
        String formattedTime = localDateTime.format(formatter);

        validateCodeDTO.setCode(formattedTime + "0712345678");
        validateCodeDTO.setEmail("a@a.lv");


        when(lotteryService.getLotteryById(any())).thenReturn(Lottery.builder()
                .startTimestamp(localDateTime)
                .build());

        codeValidator.validate(validateCodeDTO, bindingResult);

        assertTrue(bindingResult.hasErrors());
    }

    @Test
    void validateCodeShouldFail_DueToTimeNotMatchLottery() throws LotteryException {
        localDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMYY");
        String formattedTime = localDateTime.format(formatter);

        validateCodeDTO.setCode(formattedTime + "0612345678");
        validateCodeDTO.setEmail("a@a.lv");

        when(lotteryService.getLotteryById(any())).thenReturn(Lottery.builder()
                .startTimestamp(localDateTime.minusMonths(22L))
                .build());

        codeValidator.validate(validateCodeDTO, bindingResult);
        assertTrue(bindingResult.hasErrors());
    }


    @Test
    void validateShouldFail_DueToTooSmallCode() {
        validateCodeDTO.setCode("123456789012345");

        codeValidator.validate(validateCodeDTO, bindingResult);
        assertTrue(bindingResult.hasErrors());
    }

    @Test
    void validateCodeShouldFail_DueToLotteryNotExist() throws LotteryException {
        localDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMYY");
        String formattedTime = localDateTime.format(formatter);

        validateCodeDTO.setCode(formattedTime + "0612345678");
        validateCodeDTO.setEmail("a@a.lv");
        validateCodeDTO.setLotteryId(123L);

        when(lotteryService.getLotteryById(validateCodeDTO.getLotteryId())).thenThrow(LotteryException.class);

        codeValidator.validate(validateCodeDTO, bindingResult);
        assertTrue(bindingResult.hasErrors());
    }

    @Test
    void validateCodeShouldSuccessWithNoErrors() throws LotteryException {
        localDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMYY");
        String formattedTime = localDateTime.format(formatter);

        validateCodeDTO.setCode(formattedTime + "0612345678");
        validateCodeDTO.setEmail("a@a.lv");

        when(lotteryService.getLotteryById(any())).thenReturn(Lottery.builder()
                .startTimestamp(localDateTime)
                .build());

        codeValidator.validate(validateCodeDTO, bindingResult);
        assertFalse(bindingResult.hasErrors());
    }

    @Test
    void validateCodeShouldSuccessWithNoErrors_WithEmailLettersMoreThanTen() throws LotteryException {
        localDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMYY");
        String formattedTime = localDateTime.format(formatter);

        validateCodeDTO.setCode(formattedTime + "1112345678");
        validateCodeDTO.setEmail("aaaa@aaa.co");

        when(lotteryService.getLotteryById(any())).thenReturn(Lottery.builder()
                .startTimestamp(localDateTime)
                .build());

        codeValidator.validate(validateCodeDTO, bindingResult);
        assertFalse(bindingResult.hasErrors());
    }

}