package pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model.ModPack;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.service.CurseForgeAPIService;

import java.util.List;

@Controller
@RequestMapping("/servers")
public class ServersController
{
    private final CurseForgeAPIService curseForgeAPIService;

    @Autowired
    public ServersController(final CurseForgeAPIService curseForgeAPIService)
    {
        this.curseForgeAPIService = curseForgeAPIService;
    }

    @GetMapping("/add-server")
    public String addServer(final Model model)
    {
        final List<ModPack> modPacks = this.curseForgeAPIService.getModpacks();
        model.addAttribute("modpacks", modPacks);
        return "servers/add-server";
    }
}
