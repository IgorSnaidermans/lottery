package lv.igors.lottery.security;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SecurityController {

    @GetMapping("/admin-login")
    public String adminLogin(Model model) {
        return "admin-login";
    }

}
