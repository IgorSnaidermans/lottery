package lv.igors.lottery.lottery;

import lombok.RequiredArgsConstructor;
import lv.igors.lottery.lottery.dto.CheckStatusDTO;
import lv.igors.lottery.lottery.dto.LotteryIdDTO;
import lv.igors.lottery.lottery.dto.NewLotteryDTO;
import lv.igors.lottery.lottery.dto.RegistrationDTO;
import lv.igors.lottery.statusResponse.Responses;
import lv.igors.lottery.statusResponse.StatusResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;


@Controller
@RequiredArgsConstructor
public class LotteryController {
    private final LotteryService lotteryService;

    @GetMapping("/admin")
    public String adminPage(Model model) {
        model.addAttribute("lotteries", lotteryService.getAllLotteries());
        return "admin-panel";
    }

    @PostMapping("/admin/start-registration")
    public ResponseEntity<String> startRegistration(Model model,
                                                    @Valid @ModelAttribute NewLotteryDTO newLotteryDTO,
                                                    BindingResult bindingResult) {

        if (validationError(model, bindingResult)) return new ResponseEntity<>("error", HttpStatus.BAD_REQUEST);

        StatusResponse statusResponse = lotteryService.newLottery(newLotteryDTO);
        if (serviceError(model, statusResponse)) return new ResponseEntity<>("error", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>("redirect:/admin", HttpStatus.OK);
    }

    private boolean serviceError(Model model, StatusResponse statusResponse) {
        if (statusResponse.getStatus().equals(Responses.FAIL.getResponse())) {
            model.addAttribute("statusResponse", statusResponse);
            return true;
        }
        return false;
    }


    @PostMapping("/admin/stop-registration")
    public ResponseEntity<String> stopRegistration(Model model,
                                                   @Valid @ModelAttribute LotteryIdDTO lotteryId,
                                                   BindingResult bindingResult) {


        if (validationError(model, bindingResult)) return new ResponseEntity<>("error", HttpStatus.BAD_REQUEST);
        StatusResponse statusResponse = lotteryService.stopRegistration(lotteryId);
        if (serviceError(model, statusResponse)) return new ResponseEntity<>("error", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>("redirect:/admin", HttpStatus.OK);
    }

    @PostMapping("/admin/choose-winner")
    public ResponseEntity<String> chooseWinner(Model model,
                                               @Valid @ModelAttribute LotteryIdDTO lotteryId,
                                               BindingResult bindingResult) {

        if (validationError(model, bindingResult)) return new ResponseEntity<>("error", HttpStatus.BAD_REQUEST);
        StatusResponse statusResponse = lotteryService.chooseWinner(lotteryId);
        if (serviceError(model, statusResponse)) return new ResponseEntity<>("error", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>("redirect:/admin", HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerToLottery(Model model,
                                                    @Valid @ModelAttribute RegistrationDTO registrationDTO,
                                                    BindingResult bindingResult) {


        if (validationError(model, bindingResult)) return new ResponseEntity<>("error", HttpStatus.BAD_REQUEST);
        StatusResponse statusResponse = lotteryService.registerCode(registrationDTO);
        if (serviceError(model, statusResponse)) return new ResponseEntity<>("error", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>("redirect:/", HttpStatus.OK);
    }

    @GetMapping("/status")
    public ResponseEntity<String> checkWinnerStatus(Model model,
                                                    @RequestParam("lotteryId") Long id,
                                                    @RequestParam("email") String email,
                                                    @RequestParam("code") String code,
                                                    @RequestParam("age") Byte age,
                                                    BindingResult bindingResult) {

        CheckStatusDTO checkStatusDTO = CheckStatusDTO.builder()
                .code(code)
                .email(email)
                .lotteryId(id)
                .age(age)
                .build();

        if (validationError(model, bindingResult)) return new ResponseEntity<>("error", HttpStatus.BAD_REQUEST);
        StatusResponse statusResponse = lotteryService.getWinnerStatus(checkStatusDTO);
        if (serviceError(model, statusResponse)) return new ResponseEntity<>("error", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>("redirect:/", HttpStatus.OK);
    }

    @GetMapping("/stats")
    public String getStatistics(Model model) {
        model.addAttribute("lotteries", lotteryService.getAllLotteryStatistics());
        return "statistics";
    }

    @GetMapping("/lottery")
    public String getLottery(Model model,
                             @RequestParam Long lotteryId) {

        try {
            model.addAttribute(lotteryService.getLotteryById(lotteryId));
        } catch (LotteryException e) {
            model.addAttribute("statusResponse", StatusResponse.builder()
                    .reason(e.getMessage())
                    .status(Responses.FAIL.getResponse()));
            return "error";
        }

        return "lottery";
    }

    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("lotteries", lotteryService.getAllLotteries());
        return "lotteries";
    }

    private boolean validationError(Model model, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = new ArrayList<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.add(error.getDefaultMessage());
            }

            model.addAttribute("errors", errors);
            return true;
        }
        return false;
    }
}
