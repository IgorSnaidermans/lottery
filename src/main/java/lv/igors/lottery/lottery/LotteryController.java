package lv.igors.lottery.lottery;

import lombok.RequiredArgsConstructor;
import lv.igors.lottery.code.CodeValidator;
import lv.igors.lottery.lottery.dto.LotteryIdDTO;
import lv.igors.lottery.lottery.dto.NewLotteryDTO;
import lv.igors.lottery.lottery.dto.RegistrationDTO;
import lv.igors.lottery.statusResponse.Responses;
import lv.igors.lottery.statusResponse.StatusResponse;
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
    private final CodeValidator codeValidator;

    @GetMapping("/admin")
    public String adminPage(Model model) {
        model.addAttribute("lotteries", lotteryService.getAllLotteriesAdminDTO());
        return "admin-panel";
    }

    @PostMapping("/admin/start-registration")
    public String startRegistration(Model model,
                                    @Valid @ModelAttribute NewLotteryDTO newLotteryDTO,
                                    BindingResult bindingResult) {

        if (isValidationError(model, bindingResult)) return "error";
        StatusResponse statusResponse = lotteryService.newLottery(newLotteryDTO);
        if (isServiceError(model, statusResponse)) return ("error");
        return "redirect:/admin";
    }

    @PostMapping("/admin/stop-registration")
    public String stopRegistration(Model model,
                                   @Valid @ModelAttribute LotteryIdDTO lotteryId,
                                   BindingResult bindingResult) {


        if (isValidationError(model, bindingResult)) return "error";
        StatusResponse statusResponse = lotteryService.stopRegistration(lotteryId);
        if (isServiceError(model, statusResponse)) return ("error");
        return "redirect:/admin";
    }

    @PostMapping("/admin/choose-winner")
    public String chooseWinner(Model model,
                               @Valid @ModelAttribute LotteryIdDTO lotteryId,
                               BindingResult bindingResult) {

        if (isValidationError(model, bindingResult)) return "error";
        StatusResponse statusResponse = lotteryService.chooseWinner(lotteryId);
        if (isServiceError(model, statusResponse)) return ("error");
        return "redirect:/admin";
    }

    @PostMapping("/register")
    public String registerToLottery(Model model,
                                    @Valid @ModelAttribute RegistrationDTO registrationDTO,
                                    BindingResult bindingResult) {

        codeValidator.validate(registrationDTO, bindingResult);

        if (isValidationError(model, bindingResult)) return "error";
        StatusResponse statusResponse = lotteryService.registerCode(registrationDTO);
        if (isServiceError(model, statusResponse)) return ("error");
        return "redirect:/admin";
    }

    @GetMapping("/status")
    public String checkWinnerStatus(Model model,
                                    @RequestParam("lotteryId") Long id,
                                    @RequestParam("email") String email,
                                    @RequestParam("code") String code,
                                    @RequestParam("age") Byte age,
                                    BindingResult bindingResult) {

        RegistrationDTO registrationDTO = RegistrationDTO.builder()
                .code(code)
                .email(email)
                .lotteryId(id)
                .age(age)
                .build();

        codeValidator.validate(registrationDTO, bindingResult);

        if (isValidationError(model, bindingResult)) return "error";
        StatusResponse statusResponse = lotteryService.getWinnerStatus(registrationDTO);
        if (isServiceError(model, statusResponse)) return ("error");
        return "redirect:/admin";
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
        model.addAttribute("lotteries", lotteryService.getAllLotteriesToLotteryDTO());
        return "lotteries";
    }

    private boolean isServiceError(Model model, StatusResponse statusResponse) {
        if (statusResponse.getStatus().equals(Responses.FAIL.getResponse())) {
            model.addAttribute("statusResponse", statusResponse);
            return true;
        }
        return false;
    }

    private boolean isValidationError(Model model, BindingResult bindingResult) {
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
