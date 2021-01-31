package pl.bartlomiejstepien.mcsm.controller.rest;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import pl.bartlomiejstepien.mcsm.auth.AuthenticatedUser;
import pl.bartlomiejstepien.mcsm.exception.ServerNotRunningException;
import pl.bartlomiejstepien.mcsm.model.InstallationStatus;
import pl.bartlomiejstepien.mcsm.dto.ServerDto;
import pl.bartlomiejstepien.mcsm.model.ServerProperties;
import pl.bartlomiejstepien.mcsm.dto.UserDto;
import pl.bartlomiejstepien.mcsm.process.ServerManager;
import pl.bartlomiejstepien.mcsm.service.ServerService;
import pl.bartlomiejstepien.mcsm.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/servers")
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

    @GetMapping("/installation-status/{modpackId}")
    public InstallationStatus getInstallationStatus(@PathVariable final int modpackId)
    {
        LOGGER.info("Getting installation status for id = " + modpackId);
        return this.serverService.getInstallationStatus(modpackId).orElse(new InstallationStatus(0, ""));
    }

    @GetMapping("/{id}/latest-log")
    public List<String> getLatestServerLog(final @PathVariable("id") int serverId, final int numberOfLines)
    {
        LOGGER.debug("Getting latest server log for server id =" + serverId + ", numberOfLines =" + numberOfLines);
        return this.serverService.getServerLatestLog(serverId, numberOfLines);
    }

    @PostMapping("/{id}/command")
    public void postCommand(final @PathVariable("id") int serverId, @RequestBody String command, final Authentication authentication)
    {
        LOGGER.info("Posting command {" + command + "} to server id = " + serverId);

        final UserDto userDto = (UserDto)authentication.getPrincipal();

        final Optional<ServerDto> optionalServer = userDto.getServerById(serverId);
        if (!optionalServer.isPresent())
            throw new RuntimeException("Access denied!");

        final ServerDto serverDto = optionalServer.get();

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

        final UserDto userDto = (UserDto)authentication.getPrincipal();

        final Optional<ServerDto> optionalServer = userDto.getServerById(serverId);
        if (!optionalServer.isPresent())
            throw new RuntimeException("Access denied!");

        final ServerDto serverDto = optionalServer.get();
        serverDto.saveProperties(serverProperties);

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
}
