package lv.igors.lottery.lottery;

import lombok.AllArgsConstructor;
import lv.igors.lottery.code.CodeValidator;
import lv.igors.lottery.lottery.dto.*;
import lv.igors.lottery.statusResponse.Responses;
import lv.igors.lottery.statusResponse.StatusResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@Validated
@AllArgsConstructor
public class LotteryRestController {
    LotteryService lotteryService;
    CodeValidator codeValidator;

    @PostMapping("/rest/admin/start-registration")
    public ResponseEntity<StatusResponse> startRegistration(@Valid @RequestBody NewLotteryDTO newLotteryDTO,
                                                            BindingResult bindingResult) {
        if (isValidationError(bindingResult)) {
            return buildValidationError(bindingResult);
        }
        StatusResponse statusResponse = lotteryService.newLottery(newLotteryDTO);
        if (isServiceError(statusResponse)) return new ResponseEntity<>(statusResponse,
                HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(statusResponse, HttpStatus.OK);
    }

    @PostMapping("/rest/admin/stop-registration")
    public ResponseEntity<StatusResponse> stopRegistration(@Valid @RequestBody LotteryIdDTO lotteryId,
                                                           BindingResult bindingResult) {
        if (isValidationError(bindingResult)) {
            return buildValidationError(bindingResult);
        }
        StatusResponse statusResponse = lotteryService.stopRegistration(lotteryId);
        if (isServiceError(statusResponse)) return new ResponseEntity<>(statusResponse,
                HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(statusResponse, HttpStatus.OK);
    }

    @PostMapping("/rest/admin/choose-winner")
    public ResponseEntity<StatusResponse> chooseWinner(@Valid @RequestBody LotteryIdDTO lotteryId,
                                                       BindingResult bindingResult) {
        if (isValidationError(bindingResult)) {
            return buildValidationError(bindingResult);
        }
        StatusResponse statusResponse = lotteryService.chooseWinner(lotteryId);
        if (isServiceError(statusResponse)) return new ResponseEntity<>(statusResponse,
                HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(statusResponse, HttpStatus.OK);
    }

    @PostMapping("/rest/register")
    public ResponseEntity<StatusResponse> registerToLottery(@Valid @RequestBody RegistrationDTO registrationDTO,
                                                            BindingResult bindingResult) {
        codeValidator.validate(registrationDTO, bindingResult);

        if (isValidationError(bindingResult)) {
            return buildValidationError(bindingResult);
        }
        StatusResponse statusResponse = lotteryService.registerCode(registrationDTO);
        if (isServiceError(statusResponse)) return new ResponseEntity<>(statusResponse,
                HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(statusResponse, HttpStatus.OK);
    }

    @GetMapping("/rest/status")
    public ResponseEntity<StatusResponse> checkWinnerStatus(@Valid RegistrationDTO registrationDTO,
                                                            BindingResult bindingResult) {


        codeValidator.validate(registrationDTO, bindingResult);
        if (isValidationError(bindingResult)) {
            return buildValidationError(bindingResult);
        }

        StatusResponse statusResponse = lotteryService.getWinnerStatus(registrationDTO);
        if (isServiceError(statusResponse)) return new ResponseEntity<>(statusResponse,
                HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(statusResponse, HttpStatus.OK);
    }

    @GetMapping("/rest/stats")
    public ResponseEntity<List<StatisticsDTO>> getStatistics() {
        return new ResponseEntity<>(lotteryService.getAllLotteryStatistics(), HttpStatus.OK);
    }

    private boolean isServiceError(StatusResponse statusResponse) {
        return statusResponse.getStatus().equals(Responses.FAIL.getResponse());
    }

    private ResponseEntity<StatusResponse> buildValidationError(BindingResult bindingResult) {
        StringBuilder reasonBuilder = new StringBuilder();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            reasonBuilder.append(fieldError.getField()).append(": ")
                    .append(fieldError.getDefaultMessage()).append("  ");
        }

        return new ResponseEntity<>(StatusResponse.builder()
                .status(Responses.FAIL.getResponse())
                .reason(reasonBuilder.toString())
                .build(),
                HttpStatus.BAD_REQUEST);
    }

    private boolean isValidationError(BindingResult bindingResult) {
        return bindingResult.hasErrors();
    }

}
