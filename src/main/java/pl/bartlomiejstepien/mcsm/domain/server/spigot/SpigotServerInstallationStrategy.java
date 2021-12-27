package pl.bartlomiejstepien.mcsm.domain.server.spigot;

import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.mcsm.config.Config;
import pl.bartlomiejstepien.mcsm.domain.dto.JavaDto;
import pl.bartlomiejstepien.mcsm.domain.dto.ServerDto;
import pl.bartlomiejstepien.mcsm.domain.exception.CouldNotDownloadServerFilesException;
import pl.bartlomiejstepien.mcsm.domain.exception.CouldNotInstallServerException;
import pl.bartlomiejstepien.mcsm.domain.exception.MissingJavaConfigurationException;
import pl.bartlomiejstepien.mcsm.domain.model.InstalledServer;
import pl.bartlomiejstepien.mcsm.domain.platform.Platform;
import pl.bartlomiejstepien.mcsm.domain.server.AbstractServerInstallationStrategy;
import pl.bartlomiejstepien.mcsm.domain.server.EulaAcceptor;
import pl.bartlomiejstepien.mcsm.domain.server.ServerDirNameCorrector;
import pl.bartlomiejstepien.mcsm.integration.getbukkit.GetBukkitClient;
import pl.bartlomiejstepien.mcsm.service.JavaService;
import pl.bartlomiejstepien.mcsm.service.ServerService;
import pl.bartlomiejstepien.mcsm.service.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Component
public class SpigotServerInstallationStrategy extends AbstractServerInstallationStrategy<SpigotInstallationRequest>
{
    private final GetBukkitClient getBukkitClient;
    private final EulaAcceptor eulaAcceptor;
    private final ServerDirNameCorrector serverDirNameCorrector;
    private final Config config;
    private final JavaService javaService;
    private final ServerService serverService;
    private final UserService userService;

    public SpigotServerInstallationStrategy(final GetBukkitClient getBukkitClient,
                                            final EulaAcceptor eulaAcceptor,
                                            final ServerDirNameCorrector serverDirNameCorrector,
                                            final Config config,
                                            final JavaService javaService,
                                            final ServerService serverService,
                                            final UserService userService)
    {
        this.getBukkitClient = getBukkitClient;
        this.eulaAcceptor = eulaAcceptor;
        this.serverDirNameCorrector = serverDirNameCorrector;
        this.config = config;
        this.javaService = javaService;
        this.serverService = serverService;
        this.userService = userService;
    }

    @Override
    public InstalledServer install(SpigotInstallationRequest serverInstallRequest)
    {
        final String username = serverInstallRequest.getUsername();
        final String version = serverInstallRequest.getVersion();

        //TODO: Preform spigot server installation

        // Check if username already owns server for such version

        try
        {
            // Download spigot
            Path downloadedFilePath = this.getBukkitClient.downloadServer(version);

            // TODO: Determine free server port

            Path serverPath = prepareServerPath(username, version);
            Files.createDirectories(serverPath);

            // Copy spigot
            Path destSpigotPath = serverPath.resolve(downloadedFilePath.getFileName());
            Files.copy(downloadedFilePath, destSpigotPath);

            this.eulaAcceptor.acceptEula(serverPath);

            //TODO: Remove determineJavaVersionForModpack and set java to first found java. User should properly configure the server after the installation.
            JavaDto javaDto = Optional.ofNullable(this.javaService.findFirst())
                    .orElseThrow(() -> new MissingJavaConfigurationException("No available java found!"));

            final int serverId = saveServerToDB(username, serverPath.getFileName().toString(), serverPath, javaDto.getId());

            return new InstalledServer(serverId, serverPath.getFileName().toString(), serverPath, destSpigotPath);
        }
        catch (CouldNotDownloadServerFilesException | IOException e)
        {
            e.printStackTrace();
            throw new CouldNotInstallServerException(e.getMessage());
        }
    }

    private Path prepareServerPath(String username, String version) {
        String serverDirName = this.serverDirNameCorrector.convert("spigot-" + version);
        return Paths.get(config.getServersDir()).resolve(username).resolve(serverDirName);
    }

    private int saveServerToDB(String username, String serverName, Path serverPath, int javaId)
    {
        ServerDto serverDto = new ServerDto(0, serverName, serverPath.toString());
        serverDto.setJavaId(javaId);
        serverDto.setPlatform(Platform.FORGE.getName());
        this.serverService.addServer(userService.findByUsername(username).getId(), serverDto);
        final int serverId = this.serverService.getServerByPath(serverPath.toString())
                .map(ServerDto::getId)
                .orElse(-1);
        return serverId;
    }
}
