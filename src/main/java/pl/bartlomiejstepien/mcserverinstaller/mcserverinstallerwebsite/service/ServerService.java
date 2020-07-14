package pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.service;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.config.Config;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model.InstallationStatus;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model.ModPack;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
public class ServerService
{
    @Autowired
    private Config config;

    // Modpack id ==> InstallationStatus
    public static final Map<Integer, InstallationStatus> MODPACKS_INSTALLATION_STATUSES = new HashMap<>();

    private final CurseForgeAPIService curseForgeAPIService;

    @Autowired
    public ServerService(final CurseForgeAPIService curseForgeAPIService)
    {
        this.curseForgeAPIService = curseForgeAPIService;
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
     * @param username the username
     * @param modpackId the modpack id
     * @return newly generated server id
     */
    public String installServer(final String username, final int modpackId)
    {
        MODPACKS_INSTALLATION_STATUSES.put(modpackId, new InstallationStatus(0, "Checking server existence..."));

        final ModPack modPack = this.curseForgeAPIService.getModpack(modpackId);

        final Path serversUsernamePath = Paths.get(config.getServersDir()).resolve(username);
        final Path serverPath = serversUsernamePath.resolve(modPack.getName());

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
            zipFile.extractAll(config.getServersDir() + File.separator + username + File.separator + modPack.getName());
        }
        catch (ZipException e)
        {
            e.printStackTrace();
        }

        MODPACKS_INSTALLATION_STATUSES.put(modpackId, new InstallationStatus(75, "Last steps..."));

        //TODO: Attach server to given user

        //TODO: Set eula to true?

        MODPACKS_INSTALLATION_STATUSES.put(modpackId, new InstallationStatus(100, "Server is ready!"));
        return "";
    }
}
