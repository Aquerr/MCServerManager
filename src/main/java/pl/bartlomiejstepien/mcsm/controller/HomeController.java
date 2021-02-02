package pl.bartlomiejstepien.mcsm.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pl.bartlomiejstepien.mcsm.auth.AuthenticatedUser;
import pl.bartlomiejstepien.mcsm.dto.ServerDto;
import pl.bartlomiejstepien.mcsm.service.ServerService;
import pl.bartlomiejstepien.mcsm.service.UserService;

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
        final AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();

        LOGGER.info("Accessing url / by user " + authenticatedUser.getUsername() + " " + httpServletRequest.getLocalAddr());

        List<ServerDto> serverDtos = new ArrayList<>(this.serverService.getServersForUser(authenticatedUser.getId()));

        model.addAttribute("servers", serverDtos);
        return "index";
    }

    @GetMapping("/home")
    public String home()
    {
        return "redirect:/";
    }
}
