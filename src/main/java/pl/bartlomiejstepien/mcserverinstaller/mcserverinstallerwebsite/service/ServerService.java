package pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.service;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
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
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ServerService
{
    private Config config;

    // Modpack id ==> InstallationStatus
    public static final Map<Integer, InstallationStatus> MODPACKS_INSTALLATION_STATUSES = new HashMap<>();

    private final CurseForgeAPIService curseForgeAPIService;
    private final ServerRepository serverRepository;

    @Autowired
    public ServerService(final Config config, final CurseForgeAPIService curseForgeAPIService, final ServerRepository serverRepository)
    {
        this.config = config;
        this.curseForgeAPIService = curseForgeAPIService;
        this.serverRepository = serverRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startup()
    {
        // Prepare servers directory structure here...

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
        MODPACKS_INSTALLATION_STATUSES.put(modpackId, new InstallationStatus(0, "Checking server existence..."));

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
        MODPACKS_INSTALLATION_STATUSES.put(modpackId, new InstallationStatus(25, "Downloading server files..."));

        final int latestServerFileId = modPack.getLatestFiles().get(0).getServerPackFileId();
        final String serverDownloadUrl = this.curseForgeAPIService.getLatestServerDownloadUrl(modpackId, latestServerFileId);
        final String serverFilesZipPath = this.curseForgeAPIService.downloadServerFile(modpackId, serverDownloadUrl);

        MODPACKS_INSTALLATION_STATUSES.put(modpackId, new InstallationStatus(50, "Unpacking server files..."));

        //TODO: Unpack server files
        final ZipFile zipFile = new ZipFile("downloads" + File.separator + serverFilesZipPath);
        try
        {
            zipFile.extractAll(serverPath.toString());
        }
        catch (ZipException e)
        {
            e.printStackTrace();
        }

        MODPACKS_INSTALLATION_STATUSES.put(modpackId, new InstallationStatus(75, "Last steps..."));

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

        //TODO: Attach server to given user
        final ServerDto serverDto = new ServerDto(0, serverPath.toString(), user);
        user.addServer(serverDto);

        addServer(serverDto);

        //TODO: Set eula to true?

        MODPACKS_INSTALLATION_STATUSES.put(modpackId, new InstallationStatus(100, "Server is ready!"));
        return serverDto.getId();
    }

    @Transactional
    public void addServer(final Server server)
    {
        this.serverRepository.save(ServerDto.fromServer(server));
    }

    @Transactional
    public void addServer(final ServerDto serverDto)
    {
        this.serverRepository.save(serverDto);
    }

    @Transactional
    public List<Server> getServers()
    {
        return this.serverRepository.findAll().stream().map(Server::fromDAO).collect(Collectors.toList());
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

    public ArrayList<Server> getServersForUser(User principal)
    {
        return new ArrayList<>();
    }
}
