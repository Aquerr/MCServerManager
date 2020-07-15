package pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model.User;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.service.CurseForgeAPIService;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.service.ServerService;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.service.UserService;

@RestController
@RequestMapping("/api/modpacks")
public class ModpackRestController
{
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
    public String getModpackDescription(@PathVariable("id") final int id)
    {
        return this.curseForgeAPIService.getModpackDescription(id);
    }

    @PostMapping("/{id}/install")
    public int installModpack(@PathVariable("id") final int id, final Authentication authentication)
    {
        final User user = (User) authentication.getPrincipal();
        return this.serverService.installServer(user, id);
    }
}
