package pl.bartlomiejstepien.mcsm.web.controller.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.bartlomiejstepien.mcsm.Routes;
import pl.bartlomiejstepien.mcsm.auth.AuthenticatedUser;
import pl.bartlomiejstepien.mcsm.auth.AuthenticationFacade;
import pl.bartlomiejstepien.mcsm.domain.model.ServerPack;
import pl.bartlomiejstepien.mcsm.domain.server.ServerManager;
import pl.bartlomiejstepien.mcsm.web.controller.HomeController;
import pl.bartlomiejstepien.mcsm.domain.model.ModPack;
import pl.bartlomiejstepien.mcsm.service.CurseForgeAPIService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(Routes.API_MODPACKS)
public class ModpackRestController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

    private final AuthenticationFacade authenticationFacade;
    private final CurseForgeAPIService curseForgeAPIService;
    private final ServerManager serverManager;

    @Autowired
    public ModpackRestController(final AuthenticationFacade authenticationFacade,
                                 final CurseForgeAPIService curseForgeAPIService,
                                 final ServerManager serverManager)
    {
        this.authenticationFacade = authenticationFacade;
        this.curseForgeAPIService = curseForgeAPIService;
        this.serverManager = serverManager;
    }

    @GetMapping(value = "/{id}/description", produces = MediaType.TEXT_HTML_VALUE)
    public String getModpackDescription(@PathVariable("id") final int id, final HttpServletRequest httpServletRequest)
    {
        LOGGER.info("Getting modpack description for id=" +id + " from " + httpServletRequest.getLocalAddr());
        return this.curseForgeAPIService.getModpackDescription(id);
    }

    @GetMapping(value = "/{modpackId}/serverpacks", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ServerPack> getServerPacksForModPack(@PathVariable("modpackId") final int modpackId)
    {
        final AuthenticatedUser authenticatedUser = authenticationFacade.getCurrentUser();
        LOGGER.info("Get server packs for modpack id=" + modpackId + " by " + authenticatedUser.getUsername() + " " + authenticatedUser.getLocalIpAddress());
        return this.curseForgeAPIService.getServerPacks(modpackId);
    }

    @PostMapping(value = "/{modpackId}/serverpacks/{serverPackId}/install", produces = MediaType.TEXT_PLAIN_VALUE)
    public String installModpack(@PathVariable("modpackId") final int modpackId,
                                 @PathVariable("serverPackId") final int serverPackId,
                                 Authentication authentication,
                                 final HttpServletRequest httpServletRequest)
    {
        final AuthenticatedUser authenticatedUser = (AuthenticatedUser)authentication.getPrincipal();
        LOGGER.info("Install modpack id=" + modpackId + " by " + authenticatedUser.getUsername() + " " + httpServletRequest.getLocalAddr());
        return String.valueOf(this.serverManager.installServerForModpack(authenticatedUser, modpackId, serverPackId));
    }

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ModPack> searchModpacksForCategory(@RequestParam(name = "categoryId", defaultValue = "0") Integer categoryId,
                                                   @RequestParam(name = "size", defaultValue = "24") Integer size,
                                                   @RequestParam(name = "version", defaultValue = "") String version,
                                                   @RequestParam(name = "index", defaultValue = "0") Integer index,
                                                   @RequestParam(name = "modpackName", defaultValue = "") String modpackName)
    {
        LOGGER.info("Get " + size + " modpacks named=" + modpackName + " for version=" + version + " and category=" + categoryId + " starting at index=" + index);
        return this.curseForgeAPIService.getModpacks(categoryId, modpackName, version, size, index);
    }
}
