package pl.bartlomiejstepien.mcsm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.bartlomiejstepien.mcsm.auth.AuthenticatedUser;
import pl.bartlomiejstepien.mcsm.curseforge.api.Category;
import pl.bartlomiejstepien.mcsm.curseforge.api.Versions;
import pl.bartlomiejstepien.mcsm.model.ModPack;
import pl.bartlomiejstepien.mcsm.dto.ServerDto;
import pl.bartlomiejstepien.mcsm.process.ServerManager;
import pl.bartlomiejstepien.mcsm.service.CurseForgeAPIService;
import pl.bartlomiejstepien.mcsm.service.ServerService;

import java.util.List;

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
    public String addServer(final Model model)
    {
        final List<ModPack> modPacks = this.curseForgeAPIService.getModpacks(0, "", 24);
        model.addAttribute("modpacks", modPacks);
        model.addAttribute("categories", Category.values());
        model.addAttribute("versions", Versions.VERSIONS);
        return "servers/add-server";
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
}
