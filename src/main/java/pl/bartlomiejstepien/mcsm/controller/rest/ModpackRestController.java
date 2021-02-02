package pl.bartlomiejstepien.mcsm.controller.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.bartlomiejstepien.mcsm.auth.AuthenticatedUser;
import pl.bartlomiejstepien.mcsm.controller.HomeController;
import pl.bartlomiejstepien.mcsm.model.ModPack;
import pl.bartlomiejstepien.mcsm.service.CurseForgeAPIService;
import pl.bartlomiejstepien.mcsm.service.ServerService;
import pl.bartlomiejstepien.mcsm.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
        final AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        return this.serverService.installServer(authenticatedUser, id);
    }

    @GetMapping("/search")
    public List<ModPack> searchModpacksForCategory(@RequestParam(name = "categoryId", defaultValue = "0") Integer categoryId,
                                                   @RequestParam(name = "size", defaultValue = "24") Integer size,
                                                   @RequestParam(name = "version", defaultValue = "") String version)
    {
        LOGGER.info("Get " + size + " modpacks for version=" + version + " and category=" + categoryId);
        return this.curseForgeAPIService.getModpacks(categoryId, version, size);
    }
}
