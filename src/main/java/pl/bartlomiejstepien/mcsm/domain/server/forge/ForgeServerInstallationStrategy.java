package pl.bartlomiejstepien.mcsm.domain.server.forge;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.progress.ProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;
import pl.bartlomiejstepien.mcsm.auth.AuthenticatedUser;
import pl.bartlomiejstepien.mcsm.config.Config;
import pl.bartlomiejstepien.mcsm.domain.dto.JavaDto;
import pl.bartlomiejstepien.mcsm.domain.dto.ServerDto;
import pl.bartlomiejstepien.mcsm.domain.exception.CouldNotDownloadServerFilesException;
import pl.bartlomiejstepien.mcsm.domain.exception.CouldNotInstallServerException;
import pl.bartlomiejstepien.mcsm.domain.exception.MissingJavaConfigurationException;
import pl.bartlomiejstepien.mcsm.domain.model.InstallationStatus;
import pl.bartlomiejstepien.mcsm.domain.model.InstalledServer;
import pl.bartlomiejstepien.mcsm.domain.model.ModPack;
import pl.bartlomiejstepien.mcsm.domain.platform.Platform;
import pl.bartlomiejstepien.mcsm.domain.server.*;
import pl.bartlomiejstepien.mcsm.integration.curseforge.CurseForgeClient;
import pl.bartlomiejstepien.mcsm.service.JavaService;
import pl.bartlomiejstepien.mcsm.service.ServerService;
import pl.bartlomiejstepien.mcsm.service.UserService;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Optional;

@Component
public class ForgeServerInstallationStrategy extends AbstractServerInstallationStrategy<ForgeModpackInstallationRequest>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ForgeServerInstallationStrategy.class);

    private final Config config;
    private final ServerInstallationStatusMonitor serverInstallationStatusMonitor;
    private final EulaAcceptor eulaAcceptor;
    private final CurseForgeClient curseForgeClient;
    private final ServerDirNameCorrector serverDirNameCorrector;
    private final JavaService javaService;
    private final ServerStartFileFinder serverStartFileFinder;
    private final ServerService serverService;
    private final UserService userService;

    @Autowired
    public ForgeServerInstallationStrategy(final Config config,
                                           final ServerInstallationStatusMonitor serverInstallationStatusMonitor,
                                           final EulaAcceptor eulaAcceptor,
                                           final CurseForgeClient curseForgeClient,
                                           final ServerDirNameCorrector serverDirNameCorrector,
                                           final JavaService javaService,
                                           final ServerStartFileFinder serverStartFileFinder,
                                           final ServerService serverService,
                                           final UserService userService)
    {
        this.config = config;
        this.serverInstallationStatusMonitor = serverInstallationStatusMonitor;
        this.eulaAcceptor = eulaAcceptor;
        this.curseForgeClient = curseForgeClient;
        this.serverDirNameCorrector = serverDirNameCorrector;
        this.javaService = javaService;
        this.serverStartFileFinder = serverStartFileFinder;
        this.serverService = serverService;
        this.userService = userService;
    }

    // Modpack id ==> InstallationStatus

    @Override
    public InstalledServer install(ForgeModpackInstallationRequest serverInstallationRequest)
    {
        int serverPackId = serverInstallationRequest.getServerPackId();
        int modpackId = serverInstallationRequest.getModpackId();
        String username = serverInstallationRequest.getUsername();

        final ModPack modPack = this.curseForgeClient.getModpack(modpackId);
        Path serverPath = prepareServerPathForNewModpack(username, modPack);
        if (isServerPathOccupied(serverPath)) //TODO: Allow servers with same modpack
            throw new RuntimeException("Server for this modpack already exists!");

        if (!isModPackAlreadyDownloaded(modPack))
        {
            try
            {
                //TODO: Add download status...
                downloadServerFilesForModpack(modPack, serverPackId);
            }
            catch (CouldNotDownloadServerFilesException e)
            {
                e.printStackTrace();
                throw new CouldNotInstallServerException(e.getMessage());
            }
        }

        //TODO: Remove determineJavaVersionForModpack and set java to first found java. User should properly configure the server after the installation.
        JavaDto javaDto = determineJavaVersionForModpack(modPack)
                .orElseThrow(() -> new MissingJavaConfigurationException("No available java found!"));

        try
        {
            LOGGER.info("Unziping modpack id={} to path={}", modPack.getId(), serverPath.toAbsolutePath());
            unzipModpack(modPack, serverPath);

            final Path startFilePath = findServerStartFilePath(serverPath);
            LOGGER.info("Server's start file: {}", startFilePath.toAbsolutePath());

            this.eulaAcceptor.acceptEula(serverPath);
            LOGGER.info("Accepted EULA");

            LOGGER.info("Saving information about the server to the database");
            final int serverId = saveServerToDB(username, modPack.getName(), serverPath, javaDto.getId());

            LOGGER.info("Server id={} is ready!", serverId);
            return new InstalledServer(serverId, modPack.getName(), serverPath, startFilePath);
        }
        catch (Exception exception)
        {
            try
            {
                FileSystemUtils.deleteRecursively(serverPath);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            throw new CouldNotInstallServerException(exception.getMessage());
        }
    }

    private Path findServerStartFilePath(Path serverPath)
    {
        LOGGER.info("Looking for server start file...");
        Path serverRootDirectory = findServerRootDirectory(serverPath);
        Path serverStartFilePath = serverStartFileFinder.findServerStartFile(serverRootDirectory);
        if (serverStartFilePath == null)
            throw new RuntimeException("Could not find server start file!");

        return serverStartFilePath;
    }

    private void unzipModpack(ModPack modPack, Path serverPath) throws IOException
    {
        Files.createDirectories(serverPath);

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
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        this.serverInstallationStatusMonitor.setInstallationStatus(modPack.getId(), new InstallationStatus(100, "Unpacking completed!"));
    }

    private void downloadServerFilesForModpack(final ModPack modPack, int serverPackId) throws CouldNotDownloadServerFilesException
    {
        String serverDownloadUrl = this.curseForgeClient.getServerDownloadUrl(modPack.getId(), serverPackId);
        //TODO: Fix url. Parenthesis "(" and ")" still not work
        serverDownloadUrl = serverDownloadUrl.replaceAll(" ", "%20");
        this.curseForgeClient.downloadServerFile(modPack, serverDownloadUrl);
    }

    private Path prepareServerPathForNewModpack(String username, ModPack modPack) {
        String modpackName = this.serverDirNameCorrector.convert(modPack.getName());
        return Paths.get(config.getServersDir()).resolve(username).resolve(modpackName);
    }

    private boolean isServerPathOccupied(Path serverPath)
    {
        return Files.exists(serverPath);
    }

    private boolean isModPackAlreadyDownloaded(final ModPack modPack)
    {
        return Files.exists(this.config.getDownloadsDirPath().resolve(Paths.get(modPack.getName() + "_" + modPack.getVersion())));
    }

    private Optional<JavaDto> determineJavaVersionForModpack(ModPack modPack)
    {
        if (modPack.getVersion().equals("1.17") || modPack.getVersion().equals("1.17.1") || modPack.getVersion().equals("1.17.2"))
        {
            return Optional.ofNullable(this.javaService.findFirst());
        }
        return Optional.ofNullable(this.javaService.findFirst());
    }

    private Path findServerRootDirectory(Path serverPath)
    {
        try
        {
            final Path newServerPath = Files.walk(serverPath, FileVisitOption.FOLLOW_LINKS)
                    .filter(path -> path.getFileName().toString().contains("minecraft_server"))
                    .findFirst()
                    .orElse(null);
            if (newServerPath != null)
            {
                serverPath = newServerPath.getParent();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return serverPath;
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
