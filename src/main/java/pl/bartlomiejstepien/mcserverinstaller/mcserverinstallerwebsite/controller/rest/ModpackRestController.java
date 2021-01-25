package pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.controller.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.controller.HomeController;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model.UserDto;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.service.CurseForgeAPIService;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.service.ServerService;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.service.UserService;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/modpacks")
public class ModpackRestController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

    private final CurseForgeAPIService curseForgeAPIService;

    private final ServerService serverService;
    private final UserService userService;

    @Autowired
    public ModpackRestController(final CurseForgeAPIService curseForgeAPIService, final ServerService serverService, final UserService userService)
    {
        this.curseForgeAPIService = curseForgeAPIService;
        this.serverService = serverService;
        this.userService = userService;
    }

    @GetMapping("/{id}/description")
    public String getModpackDescription(@PathVariable("id") final int id, final HttpServletRequest httpServletRequest)
    {
        LOGGER.info("Getting modpack description for id=" +id + " from " + httpServletRequest.getRemoteAddr());
        return this.curseForgeAPIService.getModpackDescription(id);
    }

    @PostMapping("/{id}/install")
    public int installModpack(@PathVariable("id") final int id, final Authentication authentication, final HttpServletRequest httpServletRequest)
    {
        LOGGER.info("Install modpack id=" + id + " by " + authentication.getName() + " " + httpServletRequest.getRemoteAddr());
        final UserDto userDto = (UserDto) authentication.getPrincipal();
        return this.serverService.installServer(userDto, id);
    }
}
