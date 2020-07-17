package pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.controller.rest;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model.InstallationStatus;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model.Server;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model.User;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.service.ServerService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @PostMapping("/{id}/command")
    public void postCommand(final @PathVariable("id") int serverId, @RequestBody String command, final Authentication authentication)
    {
        final User user = (User)authentication.getPrincipal();

        if (user.getServers().stream().noneMatch(serverDto -> serverDto.getId() == serverId))
            throw new RuntimeException("Access denied!");

        this.serverService.postCommand(serverId, command);
    }

    @PostMapping("/{id}/settings")
    public void saveSettings(final @PathVariable("id") int serverId, @RequestBody Map<String, String> properties, final Authentication authentication)
    {
        final User user = (User)authentication.getPrincipal();

        final Optional<Server> optionalServer = user.getServerById(serverId);
        if (!optionalServer.isPresent())
            throw new RuntimeException("Access denied!");

        final Server server = optionalServer.get();
        server.saveProperties(properties);
    }
}
