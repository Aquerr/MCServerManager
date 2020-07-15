package pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.controller.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model.InstallationStatus;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.service.ServerService;

@RestController
@RequestMapping("/api/servers")
public class ServersRestController
{
    @GetMapping("/installation-status/{modpackId}")
    public String getInstallationStatus(@PathVariable final int modpackId)
    {
        return ServerService.MODPACKS_INSTALLATION_STATUSES.getOrDefault(modpackId, new InstallationStatus(0, "")).getMessage();
    }
}
