package lv.igors.lottery.security;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SecurityController {

    @GetMapping("/admin-login")
    public String adminLogin(Model model/*,
                             @RequestParam(value = "error", required = false) boolean error*/) {
        /*if (error) {
            model.addAttribute("error", true);
        }*/
        return "admin-login";
    }

}
