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
import pl.bartlomiejstepien.mcsm.converter.ServerConverter;
import pl.bartlomiejstepien.mcsm.exception.ServerAlreadyOwnedException;
import pl.bartlomiejstepien.mcsm.exception.CouldNotDownloadServerFilesException;
import pl.bartlomiejstepien.mcsm.model.InstallationStatus;
import pl.bartlomiejstepien.mcsm.model.ModPack;
import pl.bartlomiejstepien.mcsm.model.ServerDto;
import pl.bartlomiejstepien.mcsm.model.UserDto;
import pl.bartlomiejstepien.mcsm.repository.ServerRepository;
import pl.bartlomiejstepien.mcsm.repository.ds.Server;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Autowired
    public ServerService(final Config config,
                         final CurseForgeAPIService curseForgeAPIService,
                         final UserService userService,
                         final ServerRepository serverRepository,
                         final ServerInstaller serverInstaller,
                         final ServerConverter serverConverter)
    {
        this.config = config;
        this.curseForgeAPIService = curseForgeAPIService;
        this.userService = userService;
        this.serverRepository = serverRepository;
        this.serverInstaller = serverInstaller;
        this.serverConverter = serverConverter;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startup()
    {
        LOGGER.info("Preparing file structure...");

        final Path serversDirectoryPath = Paths.get(".").resolve(config.getServersDir());
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
        if (Files.notExists(Paths.get(".").resolve("downloads")))
        {
            try
            {
                Files.createDirectory(Paths.get(".").resolve("downloads"));
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
    public int installServer(final AuthenticatedUser authenticatedUser, final int modpackId)
    {
        final ModPack modPack = this.curseForgeAPIService.getModpack(modpackId);
        Path serverPath = Paths.get(config.getServersDir()).resolve(authenticatedUser.getUsername()).resolve(modPack.getName());
        if (Files.exists(serverPath))
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

        this.serverInstaller.installServerForModpack(authenticatedUser, modPack, serverPath);

        try
        {
            final Stream<Path> walkStream = Files.walk(serverPath, FileVisitOption.FOLLOW_LINKS);
            final Path newServerPath = walkStream.filter(path -> path.getFileName().toString().contains("minecraft_server"))
                    .findFirst()
                    .orElse(null);
            if (newServerPath != null)
            {
                serverPath = serverPath.resolve(newServerPath.getName(newServerPath.getNameCount() - 2));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        final ServerDto serverDto = new ServerDto(0, modPack.getName(), serverPath.toString());
        final UserDto userDto = this.userService.find(authenticatedUser.getId());
        serverDto.addUser(userDto);
        userDto.addServer(serverDto);

        int serverId = addServer(serverDto);
        serverDto.setId(serverId);
        return serverId;
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
        return this.serverRepository.findAll().stream().map(ServerDto::fromServer).collect(Collectors.toList());
    }

    @Transactional
    public void deleteServer(final int id)
    {
        this.serverRepository.delete(id);
    }

    @Transactional
    public ServerDto getServer(final int id)
    {
        return this.serverRepository.find(id).toServer();
    }

    @Transactional
    public List<ServerDto> getServersForUser(final int userId)
    {
        return this.serverRepository.findByUserId(userId).stream()
                .map(Server::toServer)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<String> getServerLatestLog(int serverId)
    {
        final ServerDto serverDto = getServer(serverId);
        final String serverPath = serverDto.getServerDir();
        final Path latestLogPath = Paths.get(serverPath + File.separator + "logs" + File.separator + "latest.log");

        if (Files.notExists(latestLogPath))
            return Collections.emptyList();

        try
        {
//            RandomAccessFile randomAccessFile = new RandomAccessFile(latestLogPath.toFile(), RandomAccessFileMode.READ.getValue());
//            randomAccessFile.seek();
            final List<String> lines = Files.readAllLines(latestLogPath);
            return lines;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            try
            {
                Files.delete(latestLogPath);
                Files.createFile(latestLogPath);
            }
            catch (IOException ioException)
            {
                ioException.printStackTrace();
            }
        }
        return Collections.emptyList();
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
        return Optional.ofNullable(server).map(Server::toServer);
    }

    public Optional<InstallationStatus> getInstallationStatus(int serverId)
    {
        return this.serverInstaller.getInstallationStatus(serverId);
    }

    public void downloadServerFilesForModpack(final ModPack modPack) throws CouldNotDownloadServerFilesException
    {
        final int latestServerFileId = modPack.getLatestFiles().get(0).getServerPackFileId();
        String serverDownloadUrl = this.curseForgeAPIService.getLatestServerDownloadUrl(modPack.getId(), latestServerFileId);
        //TODO: Fix url. ( ) still not work
        serverDownloadUrl = serverDownloadUrl.replaceAll(" ", "+");
        this.curseForgeAPIService.downloadServerFile(modPack, serverDownloadUrl);
    }

    private boolean isModPackAlreadyDownloaded(final ModPack modPack)
    {
        if (Files.exists(Paths.get("downloads" + File.separator + modPack.getName() + "_" + modPack.getVersion())))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
