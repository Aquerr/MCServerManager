package pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.controller.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model.InstallationStatus;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.service.ServerService;

import java.util.List;

@RestController
@RequestMapping("/api/servers")
public class ServersRestController
{
    private final ServerService serverService;

    public ServersRestController(final ServerService serverService)
    {
        this.serverService = serverService;
    }

    @GetMapping("/installation-status/{modpackId}")
    public String getInstallationStatus(@PathVariable final int modpackId)
    {
        return ServerService.MODPACKS_INSTALLATION_STATUSES.getOrDefault(modpackId, new InstallationStatus(0, "")).getMessage();
    }

    @GetMapping("/{id}/latest-log")
    public List<String> getLatestServerLog(final @PathVariable("id") int serverId)
    {
        final List<String> logs = this.serverService.getServerLatestLog(serverId);
        return logs;
    }
}
