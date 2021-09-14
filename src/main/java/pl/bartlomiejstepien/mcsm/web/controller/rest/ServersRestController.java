package pl.bartlomiejstepien.mcsm.web.controller.rest;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import pl.bartlomiejstepien.mcsm.Routes;
import pl.bartlomiejstepien.mcsm.auth.AuthenticatedUser;
import pl.bartlomiejstepien.mcsm.auth.AuthenticationFacade;
import pl.bartlomiejstepien.mcsm.domain.dto.JavaDto;
import pl.bartlomiejstepien.mcsm.domain.exception.ServerNotOwnedException;
import pl.bartlomiejstepien.mcsm.domain.exception.ServerNotRunningException;
import pl.bartlomiejstepien.mcsm.domain.model.FancyTreeNode;
import pl.bartlomiejstepien.mcsm.domain.model.InstallationStatus;
import pl.bartlomiejstepien.mcsm.domain.dto.ServerDto;
import pl.bartlomiejstepien.mcsm.domain.model.ServerProperties;
import pl.bartlomiejstepien.mcsm.domain.dto.UserDto;
import pl.bartlomiejstepien.mcsm.domain.platform.Platform;
import pl.bartlomiejstepien.mcsm.domain.server.ServerManager;
import pl.bartlomiejstepien.mcsm.domain.server.ServerFileService;
import pl.bartlomiejstepien.mcsm.service.ServerService;
import pl.bartlomiejstepien.mcsm.service.ServerServiceImpl;
import pl.bartlomiejstepien.mcsm.service.UserService;

import javax.validation.Valid;
import java.io.File;
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
    private final ServerFileService serverFileService;

    public ServersRestController(final AuthenticationFacade authenticationFacade,
                                 final ServerService serverService,
                                 final UserService userService,
                                 final ServerManager serverManager,
                                 final ServerFileService serverFileService)
    {
        this.authenticationFacade = authenticationFacade;
        this.serverService = serverService;
        this.userService = userService;
        this.serverManager = serverManager;
        this.serverFileService = serverFileService;
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
        final UserDto userDto = this.userService.find(authenticatedUser.getId());

        if(userDto.getServerIds().stream().noneMatch(id -> id == serverId))
            throw new RuntimeException("Access denied!");

        final ServerDto serverDto = this.serverService.getServer(serverId);
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
        int javaId = Integer.parseInt(json.get("java_id").textValue());

        if (StringUtils.isEmpty(serverName) || StringUtils.isEmpty(path) || StringUtils.isEmpty(platform) || javaId <= 0)
            throw new IllegalArgumentException("Wrong parameters");

        AuthenticatedUser authenticatedUser = authenticationFacade.getCurrentUser();

        serverService.importServer(authenticatedUser.getId(), serverName, path, Platform.valueOf(platform.toUpperCase()), javaId);
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

    @GetMapping("/{id}/folder-content")
    public ResponseEntity<?> getFolderContent(final @PathVariable("id") int serverId, @RequestParam(value = "path", required = false) String path)
    {
        LOGGER.info("/{}/folder-content/path={}", serverId, path);
        final AuthenticatedUser authenticatedUser = this.authenticationFacade.getCurrentUser();
        if (!hasAccessToServer(authenticatedUser, serverId))
        {
            throw new ServerNotOwnedException("You don't have access to do this");
        }

        String serverDir = this.serverService.getServer(serverId).getServerDir();
        if (StringUtils.isEmpty(path))
            path = serverDir;
        else
            path = serverDir + (path.startsWith("/") ? path : File.separator + path);

        List<FancyTreeNode> nodes = this.serverFileService.getFolderContent(path);
        LOGGER.info("Returning file tree = " + Arrays.deepToString(nodes.toArray()));
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

        return ResponseEntity.ok(this.serverFileService.getFileContent(fileName, this.serverService.getServer(serverId)));
    }

    @PostMapping(value = "/{id}/file-content/{fileName}", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> saveFileContent(final @PathVariable("id") int serverId, final @PathVariable("fileName") String fileName, @RequestBody String content)
    {
        final AuthenticatedUser authenticatedUser = this.authenticationFacade.getCurrentUser();
        if (!hasAccessToServer(authenticatedUser, serverId))
        {
            throw new ServerNotOwnedException("You don't have access to do this");
        }

        return ResponseEntity.ok(this.serverFileService.saveFileContent(fileName, content, this.serverService.getServer(serverId)));
    }

    @GetMapping(value = "/{id}/java")
    public JavaDto getJavaForServer(final @PathVariable("id") int serverId)
    {
        final AuthenticatedUser authenticatedUser = this.authenticationFacade.getCurrentUser();
        if (!hasAccessToServer(authenticatedUser, serverId))
        {
            throw new ServerNotOwnedException("You don't have access to do this");
        }

        return this.serverService.getJavaForServer(serverId);
    }

    @PutMapping(value = "{id}/java/{javaId}")
    public String saveJavaForServer(final @PathVariable("id") Integer serverId, final @PathVariable("javaId") Integer javaId)
    {
        final AuthenticatedUser authenticatedUser = this.authenticationFacade.getCurrentUser();
        if (!hasAccessToServer(authenticatedUser, serverId))
        {
            throw new ServerNotOwnedException("You don't have access to do this");
        }

        boolean success = this.serverService.addJavaToServer(serverId, javaId);
        if (success)
            return "Success";
        else
            return "Failure";
    }

    private boolean hasAccessToServer(final AuthenticatedUser authenticatedUser, final int serverId)
    {
        return this.serverService.getServersForUser(authenticatedUser.getId()).stream().anyMatch(serverDto -> serverDto.getId() == serverId);
    }
}
