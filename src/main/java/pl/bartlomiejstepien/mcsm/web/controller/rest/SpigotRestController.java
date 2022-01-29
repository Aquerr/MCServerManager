package pl.bartlomiejstepien.mcsm.web.controller.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiejstepien.mcsm.Routes;
import pl.bartlomiejstepien.mcsm.auth.AuthenticatedUser;
import pl.bartlomiejstepien.mcsm.domain.server.ServerManager;
import pl.bartlomiejstepien.mcsm.domain.server.spigot.SpigotInstallationRequest;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(Routes.API_SPIGOT)
public class SpigotRestController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SpigotRestController.class);

    private final ServerManager serverManager;

    @Autowired
    public SpigotRestController(final ServerManager serverManager)
    {
        this.serverManager = serverManager;
    }

    @PostMapping(value = "/{version}/install", produces = MediaType.TEXT_PLAIN_VALUE)
    public String installSpigot(@PathVariable("version") final String version,
                                 Authentication authentication,
                                 final HttpServletRequest httpServletRequest)
    {
        final AuthenticatedUser authenticatedUser = (AuthenticatedUser)authentication.getPrincipal();
        LOGGER.info("Install spigot version={} by {} from {}", version, authenticatedUser.getUsername(), httpServletRequest.getLocalAddr());
        return String.valueOf(this.serverManager.installServer(new SpigotInstallationRequest(authenticatedUser.getUsername(), version)));
    }
}
