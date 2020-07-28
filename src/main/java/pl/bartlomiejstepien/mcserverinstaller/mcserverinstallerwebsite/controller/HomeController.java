package pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model.Server;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model.User;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.repository.dto.ServerDto;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.service.ServerService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController
{
    private final ServerService serverService;

    @Autowired
    public HomeController(final ServerService serverService)
    {
        this.serverService = serverService;
    }

    @GetMapping("/")
    public String root(final Model model, final Authentication authentication)
    {
        // Return user's list of servers here...
        final List<Server> servers = ((User)authentication.getPrincipal()).getServers();

        model.addAttribute("servers", servers);
        return "index";
    }

    @GetMapping("/home")
    public String home()
    {
        return "redirect:/";
    }
}
