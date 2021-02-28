package pl.bartlomiejstepien.mcsm.web.controller.rest;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import pl.bartlomiejstepien.mcsm.Routes;
import pl.bartlomiejstepien.mcsm.auth.AuthenticatedUser;
import pl.bartlomiejstepien.mcsm.domain.exception.ServerNotOwnedException;
import pl.bartlomiejstepien.mcsm.domain.exception.ServerNotRunningException;
import pl.bartlomiejstepien.mcsm.domain.model.InstallationStatus;
import pl.bartlomiejstepien.mcsm.domain.dto.ServerDto;
import pl.bartlomiejstepien.mcsm.domain.model.ServerProperties;
import pl.bartlomiejstepien.mcsm.domain.dto.UserDto;
import pl.bartlomiejstepien.mcsm.domain.server.ServerManager;
import pl.bartlomiejstepien.mcsm.service.ServerService;
import pl.bartlomiejstepien.mcsm.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(Routes.API_SERVERS)
public class ServersRestController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ServersRestController.class);

    private final ServerService serverService;
    private final UserService userService;
    private final ServerManager serverManager;

    public ServersRestController(final ServerService serverService, final UserService userService, final ServerManager serverManager)
    {
        this.serverService = serverService;
        this.userService = userService;
        this.serverManager = serverManager;
    }

    @GetMapping(value = "/installation-status/{modpackId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public InstallationStatus getInstallationStatus(@PathVariable final int modpackId)
    {
        LOGGER.info("Getting installation status for id = " + modpackId);
        return this.serverService.getInstallationStatus(modpackId).orElse(new InstallationStatus(0, ""));
    }

    @GetMapping(value = "/{id}/latest-log/{lines}", produces = MediaType.TEXT_PLAIN_VALUE)
    public List<String> getLatestServerLog(final @PathVariable("id") int serverId, @PathVariable("lines") final int numberOfLines)
    {
        LOGGER.debug("Getting latest server log for server id =" + serverId + ", numberOfLines =" + numberOfLines);
        return this.serverService.getServerLatestLog(serverId, numberOfLines);
    }

    @PostMapping(value = "/{id}/command", consumes = MediaType.TEXT_PLAIN_VALUE)
    public void postCommand(final @PathVariable("id") int serverId, @RequestBody String command, final Authentication authentication)
    {
        LOGGER.info("Posting command {" + command + "} to server id = " + serverId);

        final AuthenticatedUser authenticatedUser = (AuthenticatedUser)authentication.getPrincipal();

        final ServerDto serverDto = this.serverService.getServersForUser(authenticatedUser.getId()).stream()
                .filter(s -> s.getId() == serverId)
                .findFirst()
                .orElseThrow(() -> new ServerNotOwnedException("Access denied!"));

        try
        {
            this.serverManager.sendCommand(serverDto, command);
        }
        catch (final ServerNotRunningException exception)
        {

        }
    }

    @PostMapping("/{id}/toggle")
    public void toggleServer(final @PathVariable("id") int serverId, final Authentication authentication)
    {
        LOGGER.info("Toggling server. Server Id = " + serverId);

        final AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        final UserDto userDto = this.userService.find(authenticatedUser.getId());

        final Optional<ServerDto> optionalServer = userDto.getServerById(serverId);
        if (!optionalServer.isPresent())
            throw new RuntimeException("Access denied!");

        final ServerDto serverDto = optionalServer.get();
        if (this.serverManager.isRunning(serverDto))
        {
            this.serverManager.stopServer(serverDto);
        }
        else
        {
            this.serverManager.startServer(serverDto);
        }
    }

    @PostMapping(value = "/{id}/settings", consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
    public void saveSettings(final @PathVariable("id") int serverId, @RequestBody @Valid ServerProperties serverProperties, final Authentication authentication)
    {
        LOGGER.info("Saving server settings " + serverProperties + " for server id " + serverId);

        final AuthenticatedUser authenticatedUser = (AuthenticatedUser)authentication.getPrincipal();

        final ServerDto serverDto = this.serverService.getServersForUser(authenticatedUser.getId()).stream()
                .filter(s -> s.getId() == serverId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Access denied!"));

        this.serverManager.saveProperties(serverDto, serverProperties);
        LOGGER.debug("Saved server settings for server id = " + serverId);
    }

    @PostMapping(value = "/import-server", consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
    public void importServer(final @RequestBody ObjectNode json, final Authentication authentication)
    {
        LOGGER.info("importServer request: " + json.toString());

        String serverName = json.get("server-name").textValue();
        String path = json.get("path").textValue();
        LOGGER.info("Importing server. Server name: " + serverName + ", server path: " + path);
        final AuthenticatedUser authenticatedUser = (AuthenticatedUser)authentication.getPrincipal();

        serverService.importServer(authenticatedUser.getId(), serverName, path);
        LOGGER.debug("Server has been imported.");
    }

    @GetMapping(value = "/status/{id}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String checkServerStatus(@PathVariable final int id)
    {
        LOGGER.info("Checking status for server id = " + id);

        final boolean isRunning = this.serverManager.isRunning(this.serverService.getServer(id));
        return isRunning ? "Running" : "Not Running";
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String deleteServer(@PathVariable final int id, @AuthenticationPrincipal AuthenticatedUser authenticatedUser)
    {
        LOGGER.info("Deleting server for id: " + id);
        if (this.serverService.getServersForUser(authenticatedUser.getId()).stream().anyMatch(serverDto -> serverDto.getId() == id))
        {
            serverService.deleteServer(id);
            return "Server has been deleted";
        }
        else
        {
            throw new ServerNotOwnedException("You don't have access to do this");
        }
    }
}
