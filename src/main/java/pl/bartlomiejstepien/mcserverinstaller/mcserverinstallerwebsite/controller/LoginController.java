package pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.Routes;

@Controller
public class LoginController
{
    @GetMapping(Routes.LOGIN)
    public String showLogin()
    {
        return "login";
    }

//    @GetMapping(Routes.LOGOUT)
//    public String logout()
//    {
//        return "redirect:/";
//    }
}
