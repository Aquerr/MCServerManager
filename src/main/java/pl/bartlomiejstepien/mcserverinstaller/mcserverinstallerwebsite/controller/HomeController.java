package pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model.Server;

import java.security.Principal;
import java.util.ArrayList;

@Controller
public class HomeController
{
    @GetMapping("/")
    public String root(final Model model, final Principal principal)
    {
        // Return user's list of servers here...
        final ArrayList<Server> servers = new ArrayList<>();
        model.addAttribute("servers", servers);
        return "index";
    }

    @GetMapping("/home")
    public String home()
    {
        return "redirect:/";
    }
}
