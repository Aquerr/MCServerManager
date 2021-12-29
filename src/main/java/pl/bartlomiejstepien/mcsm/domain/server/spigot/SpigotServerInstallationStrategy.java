package pl.bartlomiejstepien.mcsm.domain.server.spigot;

import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;
import pl.bartlomiejstepien.mcsm.config.Config;
import pl.bartlomiejstepien.mcsm.domain.dto.JavaDto;
import pl.bartlomiejstepien.mcsm.domain.dto.ServerDto;
import pl.bartlomiejstepien.mcsm.domain.exception.CouldNotDownloadServerFilesException;
import pl.bartlomiejstepien.mcsm.domain.exception.CouldNotInstallServerException;
import pl.bartlomiejstepien.mcsm.domain.exception.MissingJavaConfigurationException;
import pl.bartlomiejstepien.mcsm.domain.model.InstalledServer;
import pl.bartlomiejstepien.mcsm.domain.server.AbstractServerInstallationStrategy;
import pl.bartlomiejstepien.mcsm.domain.server.EulaAcceptor;
import pl.bartlomiejstepien.mcsm.domain.server.ServerDirNameCorrector;
import pl.bartlomiejstepien.mcsm.integration.getbukkit.GetBukkitClient;
import pl.bartlomiejstepien.mcsm.service.JavaService;
import pl.bartlomiejstepien.mcsm.service.ServerService;
import pl.bartlomiejstepien.mcsm.service.UserService;
import pl.bartlomiejstepien.mcsm.util.SystemUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

@Component
public class SpigotServerInstallationStrategy extends AbstractServerInstallationStrategy<SpigotInstallationRequest>
{
    private final GetBukkitClient getBukkitClient;
    private final ServerDirNameCorrector serverDirNameCorrector;
    private final Config config;

    public SpigotServerInstallationStrategy(final GetBukkitClient getBukkitClient,
                                            final EulaAcceptor eulaAcceptor,
                                            final ServerDirNameCorrector serverDirNameCorrector,
                                            final Config config)
    {
        super(eulaAcceptor);
        this.getBukkitClient = getBukkitClient;
        this.serverDirNameCorrector = serverDirNameCorrector;
        this.config = config;
    }

    @Override
    public InstalledServer install(SpigotInstallationRequest serverInstallRequest)
    {
        final String username = serverInstallRequest.getUsername();
        final String version = serverInstallRequest.getVersion();

        Path serverPath = prepareServerPath(username, version);
        if (isServerPathOccupied(serverPath)) //TODO: Allow servers with same spigot version
            throw new RuntimeException("Server for this spigot version already exists!");

        try
        {
            // Download spigot
            Path downloadedFilePath = this.getBukkitClient.downloadServer(version);
            Files.createDirectories(serverPath);

            // Copy spigot
            Path destSpigotPath = serverPath.resolve(downloadedFilePath.getFileName());
            Files.copy(downloadedFilePath, destSpigotPath);

            // Create start file
            createStartFile(serverPath, destSpigotPath);

            return new InstalledServer(0, serverPath.getFileName().toString(), serverPath, destSpigotPath);
        }
        catch (CouldNotDownloadServerFilesException | IOException exception)
        {
            try
            {
                FileSystemUtils.deleteRecursively(serverPath);
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
        String serverDirName = this.serverDirNameCorrector.convert("spigot-" + version);
        return Paths.get(config.getServersDir()).resolve(username).resolve(serverDirName);
    }

    private boolean isServerPathOccupied(Path serverPath)
    {
        return Files.exists(serverPath);
    }
}
