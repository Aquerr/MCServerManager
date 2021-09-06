package pl.bartlomiejstepien.mcsm.web.controller.rest;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import pl.bartlomiejstepien.mcsm.Routes;
import pl.bartlomiejstepien.mcsm.auth.AuthenticatedUser;
import pl.bartlomiejstepien.mcsm.auth.AuthenticationFacade;
import pl.bartlomiejstepien.mcsm.domain.exception.ServerNotOwnedException;
import pl.bartlomiejstepien.mcsm.domain.exception.ServerNotRunningException;
import pl.bartlomiejstepien.mcsm.domain.model.FancyTreeNode;
import pl.bartlomiejstepien.mcsm.domain.model.InstallationStatus;
import pl.bartlomiejstepien.mcsm.domain.dto.ServerDto;
import pl.bartlomiejstepien.mcsm.domain.model.ServerProperties;
import pl.bartlomiejstepien.mcsm.domain.platform.Platform;
import pl.bartlomiejstepien.mcsm.domain.server.ServerManager;
import pl.bartlomiejstepien.mcsm.service.ServerFilesService;
import pl.bartlomiejstepien.mcsm.service.ServerService;
import pl.bartlomiejstepien.mcsm.service.UserService;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping(Routes.API_SERVERS)
public class ServersRestController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ServersRestController.class);

    private final AuthenticationFacade authenticationFacade;
    private final ServerService serverService;
    private final UserService userService;
    private final ServerManager serverManager;
    private final ServerFilesService serverFilesService;

    public ServersRestController(final AuthenticationFacade authenticationFacade,
                                 final ServerService serverService,
                                 final UserService userService,
                                 final ServerManager serverManager,
                                 final ServerFilesService serverFilesService)
    {
        this.authenticationFacade = authenticationFacade;
        this.serverService = serverService;
        this.userService = userService;
        this.serverManager = serverManager;
        this.serverFilesService = serverFilesService;
    }

    @GetMapping()
    public List<ServerDto> getServers()
    {
        final AuthenticatedUser authenticatedUser = authenticationFacade.getCurrentUser();
        LOGGER.info("Accessing url / by user " + authenticatedUser.getUsername() + " " + authenticatedUser.getRemoteIpAddress());
        List<ServerDto> serverDtos = new ArrayList<>(this.serverService.getServersForUser(authenticatedUser.getId()));
        return serverDtos;
    }

    @GetMapping(value = "/installation-status/{modpackId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public InstallationStatus getInstallationStatus(@PathVariable final int modpackId)
    {
        LOGGER.info("Getting installation status for id = " + modpackId);
        return this.serverService.getInstallationStatus(modpackId).orElse(new InstallationStatus(0, ""));
    }

    @GetMapping(value = "/{id}/latest-log/{lines}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getLatestServerLog(final @PathVariable("id") int serverId, @PathVariable("lines") final int numberOfLines)
    {
        LOGGER.debug("Getting latest server log for server id =" + serverId + ", numberOfLines =" + numberOfLines);
        return this.serverManager.getLatestServerLog(serverId, numberOfLines);
    }

    @PostMapping(value = "/{id}/command", consumes = MediaType.TEXT_PLAIN_VALUE)
    public void postCommand(final @PathVariable("id") int serverId, @RequestBody String command)
    {
        LOGGER.info("Posting command {" + command + "} to server id = " + serverId);

        final AuthenticatedUser authenticatedUser = authenticationFacade.getCurrentUser();
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
            LOGGER.warn("Server is not running!", exception);
        }
    }

    @PostMapping("/{id}/toggle")
    public void toggleServer(final @PathVariable("id") int serverId)
    {
        LOGGER.info("Toggling server. Server Id = " + serverId);

        AuthenticatedUser authenticatedUser = authenticationFacade.getCurrentUser();
        final ServerDto serverDto = this.serverService.getServerForUser(serverId, authenticatedUser.getId())
                .orElseThrow(() -> new RuntimeException("Access denied!"));

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
    public void saveSettings(final @PathVariable("id") int serverId, @RequestBody @Valid ServerProperties serverProperties)
    {
        LOGGER.info("Saving server settings " + serverProperties + " for server id " + serverId);

        AuthenticatedUser authenticatedUser = authenticationFacade.getCurrentUser();
        final ServerDto serverDto = this.serverService.getServersForUser(authenticatedUser.getId()).stream()
                .filter(s -> s.getId() == serverId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Access denied!"));

        this.serverManager.saveProperties(serverDto, serverProperties);
        LOGGER.debug("Saved server settings for server id = " + serverId);
    }

    @PostMapping(value = "/import-server", consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
    public void importServer(final @RequestBody ObjectNode json)
    {
        LOGGER.info("/importServer: " + json.toString());

        String serverName = json.get("server-name").textValue();
        String path = json.get("path").textValue();
        String platform = json.get("platform").textValue();

        AuthenticatedUser authenticatedUser = authenticationFacade.getCurrentUser();

        serverService.importServer(authenticatedUser.getId(), serverName, path, Platform.valueOf(platform.toUpperCase()));
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
    public String deleteServer(@PathVariable final int id)
    {
        LOGGER.info("Deleting server for id: " + id);
        AuthenticatedUser authenticatedUser = authenticationFacade.getCurrentUser();
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

    @GetMapping("/{id}/file-structure")
    public ResponseEntity<?> fileStructure(final @PathVariable("id") int serverId)
    {
        LOGGER.info("/{}/file-structure", serverId);
        final AuthenticatedUser authenticatedUser = this.authenticationFacade.getCurrentUser();
        if (!hasAccessToServer(authenticatedUser, serverId))
        {
            throw new ServerNotOwnedException("You don't have access to do this");
        }

        List<FancyTreeNode> nodes = this.serverFilesService.getServerFileStructure(this.serverService.getServer(serverId));
        LOGGER.info("Returning file tree = " + Arrays.toString(nodes.toArray()));
        return ResponseEntity.ok(nodes);
    }

    @GetMapping("/{id}/file-content/{fileName}")
    public ResponseEntity<?> getFileContent(final @PathVariable("id") int serverId, final @PathVariable("fileName") String fileName)
    {
        final AuthenticatedUser authenticatedUser = this.authenticationFacade.getCurrentUser();
        if (!hasAccessToServer(authenticatedUser, serverId))
        {
            throw new ServerNotOwnedException("You don't have access to do this");
        }

        return ResponseEntity.ok(this.serverFilesService.getFileContent(fileName, this.serverService.getServer(serverId)));
    }

    @PostMapping(value = "/{id}/file-content/{fileName}", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> saveFileContent(final @PathVariable("id") int serverId, final @PathVariable("fileName") String fileName, @RequestBody String content)
    {
        final AuthenticatedUser authenticatedUser = this.authenticationFacade.getCurrentUser();
        if (!hasAccessToServer(authenticatedUser, serverId))
        {
            throw new ServerNotOwnedException("You don't have access to do this");
        }

        return ResponseEntity.ok(this.serverFilesService.saveFileContent(fileName, content, this.serverService.getServer(serverId)));
    }

    private boolean hasAccessToServer(final AuthenticatedUser authenticatedUser, final int serverId)
    {
        return this.serverService.getServersForUser(authenticatedUser.getId()).stream().anyMatch(serverDto -> serverDto.getId() == serverId);
    }
}
