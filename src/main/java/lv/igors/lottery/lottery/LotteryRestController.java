package lv.igors.lottery.lottery;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lv.igors.lottery.code.CodeValidator;
import lv.igors.lottery.lottery.dto.LotteryIdDTO;
import lv.igors.lottery.lottery.dto.NewLotteryDTO;
import lv.igors.lottery.lottery.dto.RegistrationDTO;
import lv.igors.lottery.lottery.dto.StatisticsDTO;
import lv.igors.lottery.statusResponse.Responses;
import lv.igors.lottery.statusResponse.StatusResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.AbstractBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;


@RestController
@Validated
@AllArgsConstructor
public class LotteryRestController {
    Gson gson;
    LotteryService lotteryService;
    CodeValidator codeValidator;

    @PostMapping("/rest/admin/start-registration")
    public ResponseEntity<StatusResponse> startRegistration(@Valid NewLotteryDTO newLotteryDTO, BindingResult bindingResult) {
        if (isValidationError(bindingResult)) {
            return buildValidationError(bindingResult);
        }
        StatusResponse statusResponse = lotteryService.newLottery(newLotteryDTO);
        if (isServiceError(statusResponse)) return new ResponseEntity<>(statusResponse,
                HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/rest/admin/stop-registration")
    public ResponseEntity<StatusResponse> stopRegistration(@Valid LotteryIdDTO lotteryId, BindingResult bindingResult) {
        if (isValidationError(bindingResult)) {
            return buildValidationError(bindingResult);
        }
        StatusResponse statusResponse = lotteryService.stopRegistration(lotteryId);
        if (isServiceError(statusResponse)) return new ResponseEntity<>(statusResponse,
                HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/rest/admin/choose-winner")
    public ResponseEntity<StatusResponse> chooseWinner(@Valid LotteryIdDTO lotteryId,
                                                       BindingResult bindingResult) {
        if (isValidationError(bindingResult)) {
            return buildValidationError(bindingResult);
        }
        StatusResponse statusResponse = lotteryService.chooseWinner(lotteryId);
        if (isServiceError(statusResponse)) return new ResponseEntity<>(statusResponse,
                HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/rest/register")
    public ResponseEntity<StatusResponse> registerToLottery(@Valid RegistrationDTO registrationDTO,
                                                            BindingResult bindingResult) {
        codeValidator.validate(registrationDTO, bindingResult);

        if (isValidationError(bindingResult)) {
            return buildValidationError(bindingResult);
        }
        StatusResponse statusResponse = lotteryService.registerCode(registrationDTO);
        if (isServiceError(statusResponse)) return new ResponseEntity<>(statusResponse,
                HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/rest/status")
    public ResponseEntity<StatusResponse> checkWinnerStatus(@NotNull(message = "Id must not be null")
                                                            @RequestParam("lotteryId") Long id,

                                                            @Email(message = "Email is invalid")
                                                            @NotEmpty(message = "email cannot be empty or null")
                                                            @RequestParam("email") String email,

                                                            @NotEmpty(message = "Code cannot be empty or null")
                                                            @RequestParam("code") String code,

                                                            @NotNull(message = "Age cannot be null")
                                                            @Min(value = 21, message = "Must be 21 or older")
                                                            @RequestParam("age") Byte age) {
        RegistrationDTO registrationDTO = RegistrationDTO.builder()
                .code(code)
                .email(email)
                .lotteryId(id)
                .age(age)
                .build();

        DataBinder dataBinder = new DataBinder(registrationDTO);
        codeValidator.validate(registrationDTO, dataBinder.getBindingResult());
        if (isValidationError(dataBinder.getBindingResult())) {
            return buildValidationError(dataBinder.getBindingResult());
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
