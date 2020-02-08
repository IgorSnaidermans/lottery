package lv.igors.lottery.lottery;

import lombok.RequiredArgsConstructor;
import lv.igors.lottery.lottery.dto.CheckStatusDTO;
import lv.igors.lottery.lottery.dto.LotteryIdDTO;
import lv.igors.lottery.lottery.dto.NewLotteryDTO;
import lv.igors.lottery.lottery.dto.RegistrationDTO;
import lv.igors.lottery.statusResponse.Responses;
import lv.igors.lottery.statusResponse.StatusResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
    public String startRegistration(Model model,
                                    @Valid @ModelAttribute NewLotteryDTO newLotteryDTO,
                                    BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            model.addAttribute(bindingResult.getFieldErrors());
        }

        StatusResponse statusResponse = lotteryService.newLottery(newLotteryDTO);
        if (statusResponse.getStatus().equals(Responses.FAIL.getResponse())) {
            model.addAttribute("statusResponse", statusResponse);
            model.addAttribute("back", "admin");
            return "error";
        }
        return "redirect:/admin";
    }

    @PostMapping("/admin/stop-registration")
    public String stopRegistration(Model model,
                                   @ModelAttribute LotteryIdDTO lotteryId) {

        StatusResponse statusResponse = lotteryService.stopRegistration(lotteryId);
        if (statusResponse.getStatus().equals(Responses.FAIL.getResponse())) {
            model.addAttribute("statusResponse", statusResponse);
            model.addAttribute("back", "admin");
            return "error";
        }
        return "redirect:/admin";
    }

    @PostMapping("/admin/choose-winner")
    public String chooseWinner(Model model,
                               @ModelAttribute LotteryIdDTO lotteryId) {

        StatusResponse statusResponse = lotteryService.chooseWinner(lotteryId);

        if (statusResponse.getStatus().equals(Responses.FAIL.getResponse())) {
            model.addAttribute("statusResponse", statusResponse);
            model.addAttribute("back", "admin");
            return "error";
        }
        return "redirect:/admin";
    }

    @PostMapping("/register")
    public String registerToLottery(Model model,
                                    @Valid @ModelAttribute RegistrationDTO registrationDTO,
                                    BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            model.addAttribute(bindingResult.getFieldErrors());
        }

        StatusResponse statusResponse = lotteryService.registerCode(registrationDTO);
        if (statusResponse.getStatus().equals(Responses.FAIL.getResponse())) {
            model.addAttribute("statusResponse", statusResponse);
            return "error";
        }
        return "redirect:/";
    }

    @GetMapping("/status")
    public String checkWinnerStatus(Model model,
                                    @RequestParam("lotteryId") Long id,
                                    @RequestParam("email") String email,
                                    @RequestParam("code") String code) {

        CheckStatusDTO checkStatusDTO = CheckStatusDTO.builder()
                .code(code)
                .email(email)
                .lotteryId(id)
                .build();

        StatusResponse statusResponse = lotteryService.getWinnerStatus(checkStatusDTO);
        if (statusResponse.getStatus().equals(Responses.FAIL.getResponse())) {
            model.addAttribute("statusResponse", statusResponse);
            return "error";
        }
        return "redirect:/";
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
}
