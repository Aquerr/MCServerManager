package pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model.ServerDto;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model.UserDto;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.service.ServerService;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

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
    public String root(final Model model, final Authentication authentication, HttpServletRequest httpServletRequest)
    {
        final UserDto userDto = (UserDto)authentication.getPrincipal();

        LOGGER.info("Accessing url / by user " + userDto.getUsername() + " " + httpServletRequest.getRemoteAddr());

        // Return user's list of servers here...
//        List<Server> servers = (user).getServers();
        List<ServerDto> serverDtos = new ArrayList<>(serverService.getServersForUser(userDto.getId()));

        model.addAttribute("servers", serverDtos);
        return "index";
    }

    @GetMapping("/home")
    public String home()
    {
        return "redirect:/";
    }
}
