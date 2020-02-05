package lv.igors.lottery.lottery;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LotteryController {

    @PostMapping("/start-registration")
    public String startRegistration(){
        return null;
    }

    @PostMapping("/register")
    public String registerToLottery(){
        return null;
    }

    @PostMapping("/stop-registration")
    public String stopRegistration(){
        return null;
    }

    @PostMapping("/choose-winner")
    public String chooseWinner(){
        return null;
    }

    @GetMapping("/status")
    public String checkWinnerStatus(){
        return null;
    }

    @GetMapping("/stats")
    public String getStatistics(){
        return null;
    }
}
