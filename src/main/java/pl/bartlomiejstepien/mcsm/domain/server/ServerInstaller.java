package pl.bartlomiejstepien.mcsm.domain.server;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.progress.ProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.mcsm.auth.AuthenticatedUser;
import pl.bartlomiejstepien.mcsm.config.Config;
import pl.bartlomiejstepien.mcsm.domain.model.InstallationStatus;
import pl.bartlomiejstepien.mcsm.domain.model.ModPack;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

@Component
public class ServerInstaller
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerInstaller.class);

    private final Config config;
    private final ServerInstallationStatusMonitor serverInstallationStatusMonitor;

    @Autowired
    public ServerInstaller(final Config config, final ServerInstallationStatusMonitor serverInstallationStatusMonitor)
    {
        this.config = config;
        this.serverInstallationStatusMonitor = serverInstallationStatusMonitor;
    }

    // Modpack id ==> InstallationStatus

    public boolean installServerForModpack(AuthenticatedUser authenticatedUser, ModPack modPack, Path serverPath)
    {
        LOGGER.info("Installing server for modpack id= " + modPack.getId() + " by user " + authenticatedUser.getUsername());
        try
        {
            Files.createDirectories(serverPath);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        this.serverInstallationStatusMonitor.setInstallationStatus(modPack.getId(), new InstallationStatus(0, "Unpacking files..."));
        final ZipFile zipFile = new ZipFile(this.config.getDownloadsDir() + File.separator + modPack.getName() + "_" + modPack.getVersion());
        zipFile.setRunInThread(true);
        ProgressMonitor progressMonitor = zipFile.getProgressMonitor();

        try
        {
            zipFile.extractAll(serverPath.toString());
            while (!progressMonitor.getState().equals(ProgressMonitor.State.READY)) {
                this.serverInstallationStatusMonitor.setInstallationStatus(modPack.getId(), new InstallationStatus(progressMonitor.getPercentDone(), "Unpacking files..."));

                Thread.sleep(100);
            }
        }
        catch (ZipException | InterruptedException e)
        {
            e.printStackTrace();
        }

        this.serverInstallationStatusMonitor.setInstallationStatus(modPack.getId(), new InstallationStatus(100, "Unpacking completed!"));
        return true;
    }
}
