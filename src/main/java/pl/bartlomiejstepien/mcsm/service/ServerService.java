package pl.bartlomiejstepien.mcsm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.bartlomiejstepien.mcsm.auth.AuthenticatedUser;
import pl.bartlomiejstepien.mcsm.config.Config;
import pl.bartlomiejstepien.mcsm.domain.model.InstalledServer;
import pl.bartlomiejstepien.mcsm.domain.server.ServerInstaller;
import pl.bartlomiejstepien.mcsm.domain.server.ServerManager;
import pl.bartlomiejstepien.mcsm.repository.converter.ServerConverter;
import pl.bartlomiejstepien.mcsm.domain.exception.ServerAlreadyOwnedException;
import pl.bartlomiejstepien.mcsm.domain.exception.CouldNotDownloadServerFilesException;
import pl.bartlomiejstepien.mcsm.domain.model.InstallationStatus;
import pl.bartlomiejstepien.mcsm.domain.model.ModPack;
import pl.bartlomiejstepien.mcsm.domain.dto.ServerDto;
import pl.bartlomiejstepien.mcsm.domain.dto.UserDto;
import pl.bartlomiejstepien.mcsm.repository.ServerRepository;
import pl.bartlomiejstepien.mcsm.repository.ds.Server;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ServerService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerService.class);

    private final Config config;

    private final CurseForgeAPIService curseForgeAPIService;
    private final ServerRepository serverRepository;
    private final UserService userService;
    private final ServerInstaller serverInstaller;
    private final ServerConverter serverConverter;
    private final ServerManager serverManager;

    @Autowired
    public ServerService(final Config config,
                         final CurseForgeAPIService curseForgeAPIService,
                         final UserService userService,
                         final ServerRepository serverRepository,
                         final ServerInstaller serverInstaller,
                         final ServerConverter serverConverter,
                         final ServerManager serverManager)
    {
        this.config = config;
        this.curseForgeAPIService = curseForgeAPIService;
        this.userService = userService;
        this.serverRepository = serverRepository;
        this.serverInstaller = serverInstaller;
        this.serverConverter = serverConverter;
        this.serverManager = serverManager;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startup()
    {
        LOGGER.info("Preparing file structure...");

        final Path serversDirectoryPath = this.config.getServersDirPath();
        if (Files.notExists(serversDirectoryPath))
        {
            try
            {
                Files.createDirectory(serversDirectoryPath);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        if (Files.notExists(this.config.getDownloadsDirPath()))
        {
            try
            {
                Files.createDirectory(this.config.getDownloadsDirPath());
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        LOGGER.info("Structure generated!");
    }

    /**
     * Installs the server with the given modpack.
     *
     * @param authenticatedUser the username
     * @param modpackId the modpack id
     * @return newly generated server id
     */
    @Transactional
    public int installServerForModpack(final AuthenticatedUser authenticatedUser, final int modpackId)
    {
        final ModPack modPack = this.curseForgeAPIService.getModpack(modpackId);
        Path serverPath = prepareServerPathForNewModpack(authenticatedUser, modPack);
        if (isServerPathOccupied(serverPath))
            throw new RuntimeException("Server for this modpack already exists!");

        if (!isModPackAlreadyDownloaded(modPack))
        {
            try
            {
                //TODO: Add download status...
                downloadServerFilesForModpack(modPack);
            }
            catch (CouldNotDownloadServerFilesException e)
            {
                e.printStackTrace();
                return -1;
            }
        }

        final InstalledServer installedServer = this.serverManager.installServerForModPack(authenticatedUser, modPack, serverPath);
        final ServerDto serverDto = new ServerDto(installedServer.getId(), installedServer.getName(), installedServer.getServerDir().toString());
        final UserDto userDto = this.userService.find(authenticatedUser.getId());
        serverDto.addUser(userDto);
        userDto.addServer(serverDto);
        int serverId = addServer(serverDto);
        serverDto.setId(serverId);
        return serverId;
    }

    private Path prepareServerPathForNewModpack(AuthenticatedUser authenticatedUser, ModPack modPack) {
        return Paths.get(config.getServersDir()).resolve(authenticatedUser.getUsername()).resolve(modPack.getName());
    }

    private boolean isServerPathOccupied(Path serverPath)
    {
        return Files.exists(serverPath);
    }

    @Transactional
    public int addServer(final ServerDto serverDto)
    {
        final Server server = this.serverConverter.convertToServer(serverDto);
        return this.serverRepository.save(server);
    }

    @Transactional
    public void addServer(final Server server)
    {
        this.serverRepository.save(server);
    }

    @Transactional
    public List<ServerDto> getServers()
    {
        return this.serverRepository.findAll().stream().map(serverConverter::convertToDto).collect(Collectors.toList());
    }

    @Transactional
    public void deleteServer(final int id)
    {
        ServerDto serverDto = getServer(id);
        this.serverManager.deleteServer(serverDto);
        this.serverRepository.delete(id);
    }

    @Transactional
    public ServerDto getServer(final int id)
    {
        final Server server = this.serverRepository.find(id);
        return this.serverConverter.convertToDto(server);
    }

    @Transactional
    public List<ServerDto> getServersForUser(final int userId)
    {
        return this.serverRepository.findByUserId(userId).stream()
                .map(this.serverConverter::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<String> getServerLatestLog(int serverId, int numberOfLines)
    {
        final ServerDto serverDto = getServer(serverId);
        return this.serverManager.getLatestServerLog(serverDto, numberOfLines);
    }

    @Transactional
    public void importServer(final int userId, final String serverName, final String path)
    {
        final UserDto userDto = this.userService.find(userId);
        final ServerDto serverDto = getServerByPath(path).orElse(new ServerDto(0, serverName, path));
        if (serverDto.getId() != 0 && userDto.getServers().stream().anyMatch(x -> path.equals(x.getServerDir())))
            throw new ServerAlreadyOwnedException(userDto, serverDto);
        userDto.addServer(serverDto);
        serverDto.addUser(userDto);
        this.userService.save(userDto);
    }

    @Transactional
    public Optional<ServerDto> getServerByPath(final String path)
    {
        final Server server = this.serverRepository.findByPath(path);
        return Optional.ofNullable(server).map(this.serverConverter::convertToDto);
    }

    public Optional<InstallationStatus> getInstallationStatus(int serverId)
    {
        return this.serverInstaller.getInstallationStatus(serverId);
    }

    public void downloadServerFilesForModpack(final ModPack modPack) throws CouldNotDownloadServerFilesException
    {
        final int latestServerFileId = modPack.getLatestFiles().get(0).getServerPackFileId();
        String serverDownloadUrl = this.curseForgeAPIService.getLatestServerDownloadUrl(modPack.getId(), latestServerFileId);
        //TODO: Fix url. Parenthesis "(" and ")" still not work
        serverDownloadUrl = serverDownloadUrl.replaceAll(" ", "+");
        this.curseForgeAPIService.downloadServerFile(modPack, serverDownloadUrl);
    }

    private boolean isModPackAlreadyDownloaded(final ModPack modPack)
    {
        return Files.exists(this.config.getDownloadsDirPath().resolve(Paths.get(modPack.getName() + "_" + modPack.getVersion())));
    }
}
