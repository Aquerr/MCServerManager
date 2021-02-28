package pl.bartlomiejstepien.mcsm.domain.server;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.mcsm.auth.AuthenticatedUser;
import pl.bartlomiejstepien.mcsm.config.Config;
import pl.bartlomiejstepien.mcsm.domain.model.InstallationStatus;
import pl.bartlomiejstepien.mcsm.domain.model.InstalledServer;
import pl.bartlomiejstepien.mcsm.domain.model.ModPack;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Component
public class ServerInstaller
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerInstaller.class);

    private final Config config;

    @Autowired
    public ServerInstaller(final Config config)
    {
        this.config = config;
    }

    // Modpack id ==> InstallationStatus
    private final Map<Integer, InstallationStatus> modpackInstallationStatus = new HashMap<>();

    public void setInstallationStatus(final int serverId, final InstallationStatus installationStatus)
    {
        this.modpackInstallationStatus.put(serverId, installationStatus);
    }

    public Optional<InstallationStatus> getInstallationStatus(final int serverId)
    {
        return Optional.ofNullable(this.modpackInstallationStatus.get(serverId));
    }

    public boolean installServerForModpack(AuthenticatedUser authenticatedUser, ModPack modPack, Path serverPath)
    {
        try
        {
            Files.createDirectories(serverPath);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        LOGGER.info("Installing server for modpack id= " + modPack.getId() + " by user " + authenticatedUser.getUsername());
        setInstallationStatus(modPack.getId(), new InstallationStatus(0, "Checking server existence..."));

        final ZipFile zipFile = new ZipFile(this.config.getDownloadsDir() + File.separator + modPack.getName() + "_" + modPack.getVersion());
        try
        {
            zipFile.extractAll(serverPath.toString());
        }
        catch (ZipException e)
        {
            e.printStackTrace();
        }

        setInstallationStatus(modPack.getId(), new InstallationStatus(75, "Finishing up..."));
        setInstallationStatus(modPack.getId(), new InstallationStatus(100, "Server is ready!"));

        return true;
    }
}
