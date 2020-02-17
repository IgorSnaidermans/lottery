package lv.igors.lottery.code;


import lv.igors.lottery.code.dto.ValidateCodeDTO;
import lv.igors.lottery.lottery.LotteryException;
import lv.igors.lottery.lottery.LotteryService;
import org.apache.tomcat.jni.Local;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class CodeValidator implements Validator {
    private static final Logger LOGGER = LoggerFactory.getLogger(CodeValidator.class);
    LotteryService lotteryService;

    public CodeValidator(LotteryService lotteryService) {
        this.lotteryService = lotteryService;
    }

    @Override
    public boolean supports(Class clazz) {
        return ValidateCodeDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidateCodeDTO validateCodeDTO = (ValidateCodeDTO) target;

        LOGGER.info("Validating code " + validateCodeDTO);

        if (!onlyNumbersCheck(errors, validateCodeDTO)) return;

        if (!codeLengthCheck(errors, validateCodeDTO)) return;

        LocalDateTime lotteryStartTimeStamp = findLotteryStartTimestamp(validateCodeDTO.getLotteryId(), errors);

        if (lotteryStartTimeStamp == null) return;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMYY");

        String datePart = validateCodeDTO.getCode().substring(0, 6);
        String emailPart = validateCodeDTO.getCode().substring(6, 8);
        String lotteryStartDate = lotteryStartTimeStamp.format(formatter);

        if (!datePart.equals(lotteryStartDate)) {
            LOGGER.warn("Code did not pass validation lottery start date doesn't match " + validateCodeDTO);
            errors.rejectValue("code", "lottery start date", "invalid code");
        } else if (!emailPart.equals(emailLetterCount(validateCodeDTO.getEmail()))) {
            LOGGER.warn("Code did not pass validation email letters doesn't match " + validateCodeDTO);
            errors.rejectValue("code", "email letters", "invalid code");
        }
    }

    private LocalDateTime findLotteryStartTimestamp(Long lotteryId, Errors errors) {
        try {
            return lotteryService.getLotteryById(lotteryId)
                    .getStartTimestamp();
        } catch (LotteryException e) {
            errors.rejectValue("lotteryId", "Lottery doesn't exist", "Lottery doesn't exist");
            return null;
        }
    }

    private String emailLetterCount(String email) {
        String emailLetterCount;

        if (email.length() < 10) {
            emailLetterCount = "0" + email.length();
        } else {
            emailLetterCount = "" + email.length();
        }
        return emailLetterCount;
    }

    private boolean codeLengthCheck(Errors errors, ValidateCodeDTO validateCodeDTO) {
        if (validateCodeDTO.getCode().length() != 16) {
            LOGGER.warn("Could not validate code - too small " + validateCodeDTO);
            errors.rejectValue("code", "min", "invalid code");
            return false;
        }
        return true;
    }

    private boolean onlyNumbersCheck(Errors errors, ValidateCodeDTO validateCodeDTO) {
        if (!validateCodeDTO.getCode().matches("[0-9]+")) {
            LOGGER.warn("Could not validate code - letter detected " + validateCodeDTO);
            errors.rejectValue("code", "numsonly", "invalid code");
            return false;
        }
        return true;
    }
}
