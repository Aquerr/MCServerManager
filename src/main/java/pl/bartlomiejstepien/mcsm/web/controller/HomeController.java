package pl.bartlomiejstepien.mcsm.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pl.bartlomiejstepien.mcsm.auth.AuthenticatedUser;
import pl.bartlomiejstepien.mcsm.auth.AuthenticationFacade;
import pl.bartlomiejstepien.mcsm.domain.dto.ServerDto;
import pl.bartlomiejstepien.mcsm.service.ServerService;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

    private final ServerService serverService;
    private AuthenticationFacade authenticationFacade;

    @Autowired
    public HomeController(final AuthenticationFacade authenticationFacade, final ServerService serverService)
    {
        this.authenticationFacade = authenticationFacade;
        this.serverService = serverService;
    }

    @GetMapping("/")
    public String root(final Model model, HttpServletRequest httpServletRequest)
    {
        final AuthenticatedUser authenticatedUser = authenticationFacade.getCurrentUser();

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
