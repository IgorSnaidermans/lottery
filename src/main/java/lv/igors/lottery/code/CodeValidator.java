package lv.igors.lottery.code;


import lv.igors.lottery.lottery.LotteryException;
import lv.igors.lottery.lottery.LotteryService;
import lv.igors.lottery.lottery.dto.RegistrationDTO;
import lv.igors.lottery.statusResponse.Responses;
import lv.igors.lottery.statusResponse.StatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
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
        return CodeDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        RegistrationDTO registrationDTO = (RegistrationDTO) target;

        LOGGER.info("Validating code " + registrationDTO);
        if (registrationDTO.getCode().length() != 16) {
            LOGGER.warn("Could not validate code - too small " + registrationDTO);
            errors.rejectValue("code", "min", "invalid code");
            return;
        }
        String requestedCode = registrationDTO.getCode();
        String datePart = requestedCode.substring(0, 6);
        String emailPart = requestedCode.substring(6, 8);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMYY");
        LocalDateTime lotteryStartTimeStamp;

        try {
            lotteryStartTimeStamp = lotteryService.getLotteryById(registrationDTO.getLotteryId())
                    .getStartTimestamp();
        } catch (LotteryException e) {
            errors.rejectValue("id", "Lottery doesn't exist", "Lottery doesn't exist");
            return;
        }

        String lotteryStartDate = lotteryStartTimeStamp.format(formatter);
        String emailLetterCount;

        if (registrationDTO.getEmail().length() < 10) {
            emailLetterCount = "0" + registrationDTO.getEmail().length();
        } else {
            emailLetterCount = "" + registrationDTO.getEmail().length();
        }

        if (!datePart.equals(lotteryStartDate)) {
            LOGGER.warn("Code did not pass validation lottery start date doesn't match " + registrationDTO);
            errors.rejectValue("code", "lottery start date", "invalid code");
        }

        if (!emailPart.equals(emailLetterCount)) {
            LOGGER.warn("Code did not pass validation email letters doesn't match " + registrationDTO);
            errors.rejectValue("code", "email letters", "invalid code");
        }
    }

    public void validate(Object target, StatusResponse statusResponse) {
        RegistrationDTO registrationDTO = (RegistrationDTO) target;

        LOGGER.info("Validating code " + registrationDTO);
        if (registrationDTO.getCode().length() != 16) {
            LOGGER.warn("Could not validate code - too small " + registrationDTO);

            statusResponse.setStatus(Responses.FAIL.getResponse());
            statusResponse.setReason("Invalid code");
            return;
        }
        String requestedCode = registrationDTO.getCode();
        String datePart = requestedCode.substring(0, 6);
        String emailPart = requestedCode.substring(6, 8);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMYY");
        LocalDateTime lotteryStartTimeStamp;

        try {
            lotteryStartTimeStamp = lotteryService.getLotteryById(registrationDTO.getLotteryId())
                    .getStartTimestamp();
        } catch (LotteryException e) {
            statusResponse.setStatus(Responses.FAIL.getResponse());
            statusResponse.setReason("Invalid code");
            return;
        }

        String lotteryStartDate = lotteryStartTimeStamp.format(formatter);
        String emailLetterCount;

        if (registrationDTO.getEmail().length() < 10) {
            emailLetterCount = "0" + registrationDTO.getEmail().length();
        } else {
            emailLetterCount = "" + registrationDTO.getEmail().length();
        }

        if (!datePart.equals(lotteryStartDate)) {
            LOGGER.warn("Code did not pass validation lottery start date doesn't match " + registrationDTO);
            statusResponse.setStatus(Responses.FAIL.getResponse());
            statusResponse.setReason("Invalid code");
        }

        if (!emailPart.equals(emailLetterCount)) {
            LOGGER.warn("Code did not pass validation email letters doesn't match " + registrationDTO);
            statusResponse.setStatus(Responses.FAIL.getResponse());
            statusResponse.setReason("Invalid code");
        }
    }

}
