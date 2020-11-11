package pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model.Server;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model.User;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.repository.dto.ServerDto;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.service.ServerService;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

    private final ServerService serverService;
    private final UserService userService;

    @Autowired
    public HomeController(final ServerService serverService, final UserService userService)
    {
        this.serverService = serverService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String root(final Model model, final Authentication authentication)
    {
        final User user = (User)authentication.getPrincipal();

        LOGGER.info("Accessing url / by user " + user.getUsername());

        // Return user's list of servers here...
//        List<Server> servers = (user).getServers();
        LOGGER.info("");
        List<Server> servers = new ArrayList<>(serverService.getServersForUser(user.getId()));

        model.addAttribute("servers", servers);
        return "index";
    }

    @GetMapping("/home")
    public String home()
    {
        return "redirect:/";
    }
}
