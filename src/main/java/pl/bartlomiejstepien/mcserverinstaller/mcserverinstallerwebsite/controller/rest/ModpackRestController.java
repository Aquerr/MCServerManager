package pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.service.CurseForgeAPIService;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.service.ServerService;

import java.security.Principal;

@RestController
@RequestMapping("/api/modpacks")
public class ModpackRestController
{
    private final CurseForgeAPIService curseForgeAPIService;

    private final ServerService serverService;

    @Autowired
    public ModpackRestController(final CurseForgeAPIService curseForgeAPIService, final ServerService serverService)
    {
        this.curseForgeAPIService = curseForgeAPIService;
        this.serverService = serverService;
    }

    @GetMapping("/{id}/description")
    public String getModpackDescription(@PathVariable("id") final int id)
    {
        return this.curseForgeAPIService.getModpackDescription(id);
    }

    @PostMapping("/{id}/install")
    public void installModpack(@PathVariable("id") final int id, final Principal principal)
    {
        this.serverService.installServer(principal.getName(), id);
    }
}
