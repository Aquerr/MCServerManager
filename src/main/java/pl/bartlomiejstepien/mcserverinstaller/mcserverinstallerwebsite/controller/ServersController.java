package pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model.ModPack;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model.Server;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.service.CurseForgeAPIService;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.service.ServerService;

import java.util.List;

@Controller
@RequestMapping("/servers")
public class ServersController
{
    private final CurseForgeAPIService curseForgeAPIService;
    private final ServerService serverService;

    @Autowired
    public ServersController(final CurseForgeAPIService curseForgeAPIService, final ServerService serverService)
    {
        this.curseForgeAPIService = curseForgeAPIService;
        this.serverService = serverService;
    }

    @GetMapping("/add-server")
    public String addServer(final Model model)
    {
        final List<ModPack> modPacks = this.curseForgeAPIService.getModpacks();
        model.addAttribute("modpacks", modPacks);
        return "servers/add-server";
    }

    @GetMapping("/{id}")
    public String showServer(final @PathVariable("id") int id, final Model model)
    {
        final Server server = this.serverService.getServer(id);

        if (server == null)
            throw new RuntimeException("Server with the given id does not exist!");

        model.addAttribute("server", server);
        return "servers/server-panel";
    }
}
