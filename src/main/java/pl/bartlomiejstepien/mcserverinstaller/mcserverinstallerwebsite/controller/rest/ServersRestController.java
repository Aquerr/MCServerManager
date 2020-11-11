package pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.controller.rest;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(ServersRestController.class);

    private final ServerService serverService;

    public ServersRestController(final ServerService serverService)
    {
        this.serverService = serverService;
    }

    @GetMapping("/installation-status/{modpackId}")
    public InstallationStatus getInstallationStatus(@PathVariable final int modpackId)
    {
        LOGGER.info("Getting installation status for id = " + modpackId);
        return this.serverService.getInstallationStatus(modpackId).orElse(new InstallationStatus(0, ""));
    }

    @GetMapping("/{id}/latest-log")
    public List<String> getLatestServerLog(final @PathVariable("id") int serverId)
    {
        LOGGER.debug("Getting latest server log for server id = " + serverId);
        return this.serverService.getServerLatestLog(serverId);
    }

    @PostMapping("/{id}/command")
    public void postCommand(final @PathVariable("id") int serverId, @RequestBody String command, final Authentication authentication)
    {
        LOGGER.info("Posting command {" + command + "} to server id = " + serverId);

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
        LOGGER.info("Toggling server. Server Id = " + serverId);

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
        LOGGER.info("Saving server settings " + serverProperties + " for server id " + serverId);

        final User user = (User)authentication.getPrincipal();

        final Optional<Server> optionalServer = user.getServerById(serverId);
        if (!optionalServer.isPresent())
            throw new RuntimeException("Access denied!");

        final Server server = optionalServer.get();
        server.saveProperties(serverProperties);

        LOGGER.debug("Saved server settings for server id = " + serverId);
    }

    @PostMapping(value = "/import-server", consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
    public void importServer(final @RequestBody ObjectNode json, final Authentication authentication)
    {
        String serverName = json.get("server-name").textValue();
        String path = json.get("path").textValue();
        LOGGER.info("Importing server. Server name: " + serverName + ", server path: " + path);
        serverService.importServer((User)authentication.getPrincipal(), serverName, path);
        LOGGER.debug("Server has been imported.");
    }
}
