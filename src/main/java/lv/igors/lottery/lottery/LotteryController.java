package lv.igors.lottery.lottery;

import lombok.RequiredArgsConstructor;
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

    @PostMapping("/admin/start-registration")
    public String startRegistration(Model model, BindingResult bindingResult,
                                    @Valid @ModelAttribute NewLotteryDTO newLotteryDTO) {

        if (bindingResult.hasErrors()) {
            model.addAttribute(bindingResult.getFieldErrors());
        }

        StatusResponse statusResponse = lotteryService.newLottery(newLotteryDTO);
        model.addAttribute(statusResponse);
        return "admin-panel";
    }

    @PostMapping("/register")
    public String registerToLottery(Model model, BindingResult bindingResult,
                                    @Valid @ModelAttribute RegistrationDTO registrationDTO) throws LotteryException {

        if (bindingResult.hasErrors()) {
            model.addAttribute(bindingResult.getFieldErrors());
        }

        StatusResponse statusResponse = lotteryService.registerCode(registrationDTO);
        model.addAttribute(statusResponse);
        if (statusResponse.getStatus().equals(Responses.OK.getResponse())) {
            model.addAttribute(lotteryService.getLotteryById(registrationDTO.getLotteryId()));
        }
        return "lottery";
    }

    @PostMapping("/admin/stop-registration")
    public String stopRegistration(Model model,
                                   @PathVariable Long lotteryId) {

        StatusResponse statusResponse = lotteryService.stopRegistration(lotteryId);
        model.addAttribute(statusResponse);
        return "admin-panel";
    }

    @PostMapping("/admin/choose-winner")
    public String chooseWinner(Model model,
                               @PathVariable Long lotteryId) {

        StatusResponse statusResponse = lotteryService.chooseWinner(lotteryId);
        model.addAttribute(statusResponse);
        return "admin-panel";
    }

    @GetMapping("/status")
    public String checkWinnerStatus(Model model, BindingResult bindingResult,
                                    @Valid @ModelAttribute CheckStatusDTO checkStatusDTO) throws LotteryException {

        if (bindingResult.hasErrors()) {
            model.addAttribute(bindingResult.getFieldErrors());
        }

        StatusResponse statusResponse = lotteryService.getWinnerStatus(checkStatusDTO);
        model.addAttribute(statusResponse);
        if (statusResponse.getStatus().equals(Responses.OK.getResponse())) {
            model.addAttribute(lotteryService.getLotteryById(checkStatusDTO.getLotteryId()));
        }
        return "lottery";
    }

    @GetMapping("/stats")
    public String getStatistics(Model model) {
        model.addAttribute(lotteryService.getAllLotteryStatistics());
        return "statistics";
    }

    @GetMapping("/lottery")
    public String getLottery(Model model,
                             @RequestParam Long lotteryId) {

        try {
            model.addAttribute(lotteryService.getLotteryById(lotteryId));
        } catch (LotteryException e) {
            model.addAttribute(StatusResponse.builder()
                    .reason(e.getMessage())
                    .status(Responses.FAIL.getResponse()));
        }

        return "lottery";
    }

    @GetMapping("/")
    public String homePage(Model model) {
        //model.addAttribute(lotteryService.getAllLotteries());
        return "lotteries";
    }
}
