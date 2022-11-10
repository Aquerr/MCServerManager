package pl.bartlomiejstepien.mcsm.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import pl.bartlomiejstepien.mcsm.auth.AuthenticatedUser;
import pl.bartlomiejstepien.mcsm.domain.model.Role;
import pl.bartlomiejstepien.mcsm.domain.platform.Platform;
import pl.bartlomiejstepien.mcsm.integration.curseforge.Category;
import pl.bartlomiejstepien.mcsm.integration.curseforge.CurseForgeClient;
import pl.bartlomiejstepien.mcsm.integration.curseforge.Versions;
import pl.bartlomiejstepien.mcsm.domain.model.ModPack;
import pl.bartlomiejstepien.mcsm.domain.dto.ServerDto;
import pl.bartlomiejstepien.mcsm.domain.server.ServerManager;
import pl.bartlomiejstepien.mcsm.service.ConfigService;
import pl.bartlomiejstepien.mcsm.service.ServerServiceImpl;

import java.util.List;

@Controller
@RequestMapping("/servers")
public class ServersController
{
    private final CurseForgeClient curseForgeClient;
    private final ServerServiceImpl serverService;
    private final ServerManager serverManager;
    private final ConfigService configService;

    @Autowired
    public ServersController(final CurseForgeClient curseForgeClient, final ServerServiceImpl serverService, final ServerManager serverManager
        , final ConfigService configService)
    {
        this.curseForgeClient = curseForgeClient;
        this.serverService = serverService;
        this.serverManager = serverManager;
        this.configService = configService;
    }

    @GetMapping("/add-server")
    public ModelAndView addServer(final ModelAndView modelAndView, @RequestParam("platform") String platformName)
    {
        Platform platform = Platform.valueOf(platformName.toUpperCase());

        switch (platform)
        {
            case BUKKIT:
            case SPIGOT:
                return prepareSpigotModelAndView(modelAndView);
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

        ServerDto serverDto;

        if (authenticatedUser.getRole().hasMorePrivilegesThan(Role.USER))
        {
            serverDto = this.serverService.getServer(id);
        }
        else
        {
            serverDto = this.serverService.getServersForUser(authenticatedUser.getId()).stream()
                    .filter(s -> s.getId() == id)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Access denied!"));
        }

        this.serverManager.loadProperties(serverDto);

        model.addAttribute("server", serverDto);
        model.addAttribute("allJavaVersions", this.configService.getAllJavaVersions());
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
        final List<ModPack> modPacks = this.curseForgeClient.getModpacks(0, "", "", 24, 0);
        modelAndView.addObject("modpacks", modPacks);
        modelAndView.addObject("categories", Category.values());
        modelAndView.addObject("versions", Versions.VERSIONS);
        modelAndView.setViewName("servers/forge/select-modpack");
        return modelAndView;
    }

    private ModelAndView prepareSpigotModelAndView(ModelAndView modelAndView)
    {
        modelAndView.addObject("versions", Versions.VERSIONS);
        modelAndView.addObject("spigotIconPath", Platform.SPIGOT.getImagePath());
        modelAndView.setViewName("servers/spigot/select-spigot");
        return modelAndView;
    }
}
