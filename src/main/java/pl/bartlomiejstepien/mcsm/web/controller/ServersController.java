package pl.bartlomiejstepien.mcsm.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import pl.bartlomiejstepien.mcsm.auth.AuthenticatedUser;
import pl.bartlomiejstepien.mcsm.domain.platform.Platform;
import pl.bartlomiejstepien.mcsm.integration.curseforge.Category;
import pl.bartlomiejstepien.mcsm.integration.curseforge.Versions;
import pl.bartlomiejstepien.mcsm.domain.model.ModPack;
import pl.bartlomiejstepien.mcsm.domain.dto.ServerDto;
import pl.bartlomiejstepien.mcsm.domain.server.ServerManager;
import pl.bartlomiejstepien.mcsm.service.CurseForgeAPIService;
import pl.bartlomiejstepien.mcsm.service.ServerService;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/servers")
public class ServersController
{
    private final CurseForgeAPIService curseForgeAPIService;
    private final ServerService serverService;
    private final ServerManager serverManager;

    @Autowired
    public ServersController(final CurseForgeAPIService curseForgeAPIService, final ServerService serverService, final ServerManager serverManager)
    {
        this.curseForgeAPIService = curseForgeAPIService;
        this.serverService = serverService;
        this.serverManager = serverManager;
    }

    @GetMapping("/add-server")
    public ModelAndView addServer(final ModelAndView modelAndView, @RequestParam("platform") String platformName)
    {
        Platform platform = Platform.valueOf(platformName.toUpperCase());

        switch (platform)
        {
            case BUKKIT:
            case SPIGOT:
            case FORGE:
                return prepareForgeModelAndView(modelAndView);
            case SPONGE:

            default:
                throw new IllegalArgumentException("No platform specified!");
        }
    }

    @GetMapping("/select-platform")
    public String selectPlatform()
    {
        return "servers/select-platform";
    }


    @GetMapping("/{id}")
    public String showServer(final @PathVariable("id") int id, final Model model, final Authentication authentication)
    {
        final AuthenticatedUser authenticatedUser = (AuthenticatedUser)authentication.getPrincipal();

        ServerDto serverDto = this.serverService.getServersForUser(authenticatedUser.getId()).stream()
                .filter(s -> s.getId() == id)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Access denied!"));

        this.serverManager.loadProperties(serverDto);

        model.addAttribute("server", serverDto);
        return "servers/server-panel";
    }

    @GetMapping("/{id}/files")
    public String files(Model model, @PathVariable("id") int serverId)
    {
        model.addAttribute("serverId", serverId);
        return "servers/server-files";
    }

    private ModelAndView prepareForgeModelAndView(ModelAndView modelAndView)
    {
        ModelMap model = modelAndView.getModelMap();
        final List<ModPack> modPacks = this.curseForgeAPIService.getModpacks(0, "", "", 24, 0);
        model.addAttribute("modpacks", modPacks);
        model.addAttribute("categories", Category.values());
        model.addAttribute("versions", Versions.VERSIONS);
        modelAndView.setViewName("servers/select-modpack");
        return modelAndView;
    }
}
