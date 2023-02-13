package pl.bartlomiejstepien.mcsm.domain.server.spigot;

import liquibase.pro.packaged.R;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.mcsm.config.Config;
import pl.bartlomiejstepien.mcsm.domain.exception.CouldNotDownloadServerFilesException;
import pl.bartlomiejstepien.mcsm.domain.exception.CouldNotInstallServerException;
import pl.bartlomiejstepien.mcsm.domain.model.InstalledServer;
import pl.bartlomiejstepien.mcsm.domain.server.AbstractServerInstallationStrategy;
import pl.bartlomiejstepien.mcsm.domain.server.EulaAcceptor;
import pl.bartlomiejstepien.mcsm.domain.server.ServerFileService;
import pl.bartlomiejstepien.mcsm.integration.getbukkit.GetBukkitClient;
import pl.bartlomiejstepien.mcsm.util.SystemUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Component
public class SpigotServerInstallationStrategy extends AbstractServerInstallationStrategy<SpigotInstallationRequest>
{
    private final GetBukkitClient getBukkitClient;
    private final ServerFileService serverFileService;
    private final Config config;

    public SpigotServerInstallationStrategy(final GetBukkitClient getBukkitClient,
                                            final EulaAcceptor eulaAcceptor,
                                            final ServerFileService serverFileService,
                                            final Config config)
    {
        super(eulaAcceptor);
        this.getBukkitClient = getBukkitClient;
        this.serverFileService = serverFileService;
        this.config = config;
    }

    @Override
    public InstalledServer install(int serverId, SpigotInstallationRequest serverInstallRequest)
    {
        final String username = serverInstallRequest.getUsername();
        final String version = serverInstallRequest.getVersion();

        Path serverPath = prepareServerPath(username, version);
        if (isServerPathOccupied(serverPath)) //TODO: Allow servers with same spigot version
            throw new RuntimeException("Server for this spigot version already exists!");

        try
        {
            // Download spigot
            Path downloadedFilePath = this.getBukkitClient.downloadServer(serverId, version);
            Files.createDirectories(serverPath);

            // Copy spigot
            Path destSpigotPath = serverPath.resolve(downloadedFilePath.getFileName());
            Files.copy(downloadedFilePath, destSpigotPath);

            // Create start file
            createStartFile(serverPath, destSpigotPath);

            return new InstalledServer(serverId, serverPath.getFileName().toString(), serverPath, destSpigotPath);
        }
        catch (CouldNotDownloadServerFilesException | IOException exception)
        {
            try
            {
                rollbackInstallation(serverPath);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            exception.printStackTrace();
            throw new CouldNotInstallServerException(exception.getMessage());
        }
    }

    private void createStartFile(Path serverPath, Path destSpigotPath) throws IOException
    {
        if (SystemUtil.isWindows())
        {
            Path startFilePath = serverPath.resolve("start.bat");
            if(Files.notExists(startFilePath))
            {
                Files.createFile(startFilePath);
                Files.writeString(startFilePath, "java -jar -Xmx1G " + destSpigotPath.toAbsolutePath() + " nogui\npause", StandardOpenOption.WRITE);
            }
        }
        else
        {
            Path startFilePath = serverPath.resolve("start.sh");
            if (Files.notExists(startFilePath))
            {
                Files.createFile(startFilePath);
                Files.writeString(startFilePath, "#!/bin/sh\njava -jar -Xmx1G " + destSpigotPath.toAbsolutePath() + " nogui\npause", StandardOpenOption.WRITE);
            }
        }
    }

    private Path prepareServerPath(String username, String version)
    {
        String serverDirName = this.serverFileService.prepareFilePath("spigot-" + version);
        return Paths.get(config.getServersDir()).resolve(username).resolve(serverDirName);
    }

    private boolean isServerPathOccupied(Path serverPath)
    {
        return Files.exists(serverPath);
    }
}
