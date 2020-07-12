package pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.service.CurseForgeAPIService;

@RestController
@RequestMapping("/api/modpacks")
public class ModpackRestController
{
    private final CurseForgeAPIService curseForgeAPIService;

    @Autowired
    public ModpackRestController(final CurseForgeAPIService curseForgeAPIService)
    {
        this.curseForgeAPIService = curseForgeAPIService;
    }

    @GetMapping("/{id}/description")
    public String getModpackDescription(@PathVariable("id") final int id)
    {
        return this.curseForgeAPIService.getModpackDescription(id);
    }
}
