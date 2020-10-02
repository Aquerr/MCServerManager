package pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.service;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.config.Config;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model.InstallationStatus;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model.ModPack;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model.Server;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model.User;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.repository.ServerRepository;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.repository.dto.ServerDto;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    @Autowired
    public ServerService(final Config config,
                         final CurseForgeAPIService curseForgeAPIService,
                         final UserService userService,
                         final ServerRepository serverRepository,
                         final ServerInstaller serverInstaller)
    {
        this.config = config;
        this.curseForgeAPIService = curseForgeAPIService;
        this.userService = userService;
        this.serverRepository = serverRepository;
        this.serverInstaller = serverInstaller;
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
     * @param user the username
     * @param modpackId the modpack id
     * @return newly generated server id
     */
    @Transactional
    public int installServer(final User user, final int modpackId)
    {
        this.serverInstaller.setInstallationStatus(modpackId, new InstallationStatus(0, "Checking server existence..."));

        final ModPack modPack = this.curseForgeAPIService.getModpack(modpackId);

        final Path serversUsernamePath = Paths.get(config.getServersDir()).resolve(user.getUsername());
        Path serverPath = serversUsernamePath.resolve(modPack.getName());

        if (Files.exists(serverPath))
            throw new RuntimeException("Server for this modpack already exists!");

        try
        {
            Files.createDirectories(serverPath);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        // Download server files...
        this.serverInstaller.setInstallationStatus(modpackId, new InstallationStatus(25, "Downloading server files..."));

        final int latestServerFileId = modPack.getLatestFiles().get(0).getServerPackFileId();
        String serverDownloadUrl = this.curseForgeAPIService.getLatestServerDownloadUrl(modpackId, latestServerFileId);
        try
        {
            //TODO: Fix url. ( ) still not work
            serverDownloadUrl = serverDownloadUrl.replaceAll(" ", "+");
            this.curseForgeAPIService.downloadServerFile(modPack, serverDownloadUrl);
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
            return -1;
        }

        this.serverInstaller.setInstallationStatus(modpackId, new InstallationStatus(50, "Unpacking server files..."));

        //TODO: Unpack server files
        final ZipFile zipFile = new ZipFile("downloads" + File.separator + modPack.getName());
        try
        {
            zipFile.extractAll(serverPath.toString());
        }
        catch (ZipException e)
        {
            e.printStackTrace();
        }

        this.serverInstaller.setInstallationStatus(modpackId, new InstallationStatus(75, "Last steps..."));

        if (!Files.exists(serverPath.resolve("server.properties")))
        {
            try
            {
                Optional<Path> path = Files.list(serverPath).findFirst();
                if (path.isPresent())
                {
                    serverPath = path.get();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        final Server server = new Server(0, modPack.getName(), serverPath.toString());
        server.addUser(user);
        int serverId = addServer(server);

        //TODO: Set eula to true?

        this.serverInstaller.setInstallationStatus(modpackId, new InstallationStatus(100, "Server is ready!"));
        return serverId;
    }

    @Transactional
    public int addServer(final Server server)
    {
        final ServerDto serverDto = ServerDto.fromServer(server);
        return this.serverRepository.save(serverDto);
    }

    @Transactional
    public void addServer(final ServerDto serverDto)
    {
        this.serverRepository.save(serverDto);
    }

    @Transactional
    public List<Server> getServers()
    {
        return this.serverRepository.findAll().stream().map(Server::fromDto).collect(Collectors.toList());
    }

    @Transactional
    public void deleteServer(final int id)
    {
        this.serverRepository.delete(id);
    }

    @Transactional
    public Server getServer(final int id)
    {
        return this.serverRepository.find(id).toServer();
    }

    @Transactional
    public List<Server> getServersForUser(final int userId)
    {
        return this.serverRepository.findByUserId(userId).stream()
                .map(ServerDto::toServer)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<String> getServerLatestLog(int serverId)
    {
        final Server server = getServer(serverId);
        final String serverPath = server.getServerDir();
        final Path latestLogPath = Paths.get(serverPath + File.separator + "logs" + File.separator + "latest.log");

        if (Files.notExists(latestLogPath))
            return Collections.emptyList();

        try
        {
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
    public void importServer(final User user, final String serverName, final String path)
    {
        final Server server = getServerByPath(path).orElse(new Server(0, serverName, path));
        server.addUser(user);
        addServer(server);
    }

    @Transactional
    public Optional<Server> getServerByPath(final String path)
    {
        final ServerDto serverDto = this.serverRepository.findByPath(path);
        return Optional.ofNullable(serverDto).map(ServerDto::toServer);
    }

    public Optional<InstallationStatus> getInstallationStatus(int serverId)
    {
        return this.serverInstaller.getInstallationStatus(serverId);
    }
}
