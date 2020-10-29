package pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.controller.rest;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.util.MimeTypeUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.exception.ServerNotRunningException;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model.InstallationStatus;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model.Server;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model.ServerProperties;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model.User;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.service.ServerService;

import javax.validation.Valid;
import java.util.HashMap;
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
    public InstallationStatus getInstallationStatus(@PathVariable final int modpackId)
    {
        return this.serverService.getInstallationStatus(modpackId).orElse(new InstallationStatus(0, ""));
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

        final Optional<Server> optionalServer = user.getServerById(serverId);
        if (!optionalServer.isPresent())
            throw new RuntimeException("Access denied!");

        final Server server = optionalServer.get();

        try
        {
            server.postCommand(command);
        }
        catch (final ServerNotRunningException exception)
        {

        }
    }

    @PostMapping("/{id}/toggle")
    public void toggleServer(final @PathVariable("id") int serverId, final Authentication authentication)
    {
        final User user = (User) authentication.getPrincipal();

        final Optional<Server> optionalServer = user.getServerById(serverId);
        if (!optionalServer.isPresent())
            throw new RuntimeException("Access denied!");

        final Server server = optionalServer.get();
        if (server.isRunning())
        {
            server.stop();
        }
        else server.start();
    }

    @PostMapping(value = "/{id}/settings", consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
    public void saveSettings(final @PathVariable("id") int serverId, @RequestBody @Valid ServerProperties serverProperties, final Authentication authentication)
    {
        final User user = (User)authentication.getPrincipal();

        final Optional<Server> optionalServer = user.getServerById(serverId);
        if (!optionalServer.isPresent())
            throw new RuntimeException("Access denied!");

        final Server server = optionalServer.get();
        server.saveProperties(serverProperties);
    }

    @PostMapping(value = "/import-server", consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
    public void importServer(final @RequestBody ObjectNode json, final Authentication authentication)
    {
        String serverName = json.get("server-name").textValue();
        String path = json.get("path").textValue();
        System.out.println("server-name =" + serverName);
        System.out.println("path =" + path);
        serverService.importServer((User)authentication.getPrincipal(), serverName, path);
    }
}
