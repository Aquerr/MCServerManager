package pl.bartlomiejstepien.mcsm.service;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.mcsm.auth.AuthenticatedUser;
import pl.bartlomiejstepien.mcsm.model.InstallationStatus;
import pl.bartlomiejstepien.mcsm.model.ModPack;
import pl.bartlomiejstepien.mcsm.model.UserDto;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class ServerInstaller
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerInstaller.class);

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

    public void installServerForModpack(AuthenticatedUser authenticatedUser, ModPack modPack, Path serverPath)
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

        final ZipFile zipFile = new ZipFile("downloads" + File.separator + modPack.getName() + "_" + modPack.getVersion());
        try
        {
            zipFile.extractAll(serverPath.toString());
        }
        catch (ZipException e)
        {
            e.printStackTrace();
        }

        setInstallationStatus(modPack.getId(), new InstallationStatus(75, "Finishing up..."));

        //TODO: Set eula to true?

        setInstallationStatus(modPack.getId(), new InstallationStatus(100, "Server is ready!"));
    }
}
