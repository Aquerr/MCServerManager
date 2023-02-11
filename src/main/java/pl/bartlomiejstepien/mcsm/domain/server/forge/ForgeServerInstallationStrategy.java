package pl.bartlomiejstepien.mcsm.domain.server.forge;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.progress.ProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;
import pl.bartlomiejstepien.mcsm.config.Config;
import pl.bartlomiejstepien.mcsm.domain.exception.CouldNotDownloadServerFilesException;
import pl.bartlomiejstepien.mcsm.domain.exception.CouldNotInstallServerException;
import pl.bartlomiejstepien.mcsm.domain.model.InstallationStatus;
import pl.bartlomiejstepien.mcsm.domain.model.InstalledServer;
import pl.bartlomiejstepien.mcsm.domain.model.ModPack;
import pl.bartlomiejstepien.mcsm.domain.server.*;
import pl.bartlomiejstepien.mcsm.integration.curseforge.CurseForgeClient;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.lang.String.format;

@Component
public class ForgeServerInstallationStrategy extends AbstractServerInstallationStrategy<ForgeModpackInstallationRequest>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ForgeServerInstallationStrategy.class);

    private final Config config;
    private final ServerInstallationStatusMonitor serverInstallationStatusMonitor;
    private final CurseForgeClient curseForgeClient;
    private final ServerDirNameCorrector serverDirNameCorrector;
    private final ServerStartFileFinder serverStartFileFinder;

    @Autowired
    public ForgeServerInstallationStrategy(final Config config,
                                           final ServerInstallationStatusMonitor serverInstallationStatusMonitor,
                                           final EulaAcceptor eulaAcceptor,
                                           final CurseForgeClient curseForgeClient,
                                           final ServerDirNameCorrector serverDirNameCorrector,
                                           final ServerStartFileFinder serverStartFileFinder)
    {
        super(eulaAcceptor);
        this.config = config;
        this.serverInstallationStatusMonitor = serverInstallationStatusMonitor;
        this.curseForgeClient = curseForgeClient;
        this.serverDirNameCorrector = serverDirNameCorrector;
        this.serverStartFileFinder = serverStartFileFinder;
    }

    @Override
    public InstalledServer install(int serverId, ForgeModpackInstallationRequest serverInstallationRequest)
    {
        int serverPackId = serverInstallationRequest.getServerPackId();
        Long modpackId = serverInstallationRequest.getModpackId();
        String username = serverInstallationRequest.getUsername();

        final ModPack modPack = this.curseForgeClient.getModpack(modpackId);
        Path serverPath = prepareServerPathForNewModpack(username, modPack);

        Path downloadPath = getDownloadPath(modPack);
        if (!isModPackAlreadyDownloaded(downloadPath))
        {
            try
            {
                //TODO: Add download status...
                downloadPath = downloadServerFilesForModpack(serverId, modPack, serverPackId, downloadPath);
            }
            catch (CouldNotDownloadServerFilesException e)
            {
                e.printStackTrace();
                throw new CouldNotInstallServerException(e.getMessage());
            }
        }

        try
        {
            LOGGER.info("Unziping modpack id={} to path={}", modPack.getId(), serverPath.toAbsolutePath());
            unzipModpack(serverId, downloadPath, serverPath);

            final Path startFilePath = findServerStartFilePath(serverPath);
            LOGGER.info("Server's start file: {}", startFilePath.toAbsolutePath());

            // TODO: Determine free server port

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
            throw new CouldNotInstallServerException(format("Could not install modpack '%s'", modPack.getName()), exception);
        }
    }

    private Path getDownloadPath(ModPack modPack)
    {
        return this.config.getDownloadsDirPath().resolve(modPack.getName().replace(" ", "-") + "_" + modPack.getVersion() + ".zip");
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

    private void unzipModpack(int serverId, Path filePath, Path serverPath) throws IOException
    {
        Files.createDirectories(serverPath);

        this.serverInstallationStatusMonitor.setInstallationStatus(serverId, new InstallationStatus(2, 0, "Unpacking files..."));
        final ZipFile zipFile = new ZipFile(filePath.toAbsolutePath().toString());
        zipFile.setRunInThread(true);
        ProgressMonitor progressMonitor = zipFile.getProgressMonitor();

        try
        {
            zipFile.extractAll(serverPath.toString());
            while (!progressMonitor.getState().equals(ProgressMonitor.State.READY)) {
                this.serverInstallationStatusMonitor.setInstallationStatus(serverId, new InstallationStatus(2, progressMonitor.getPercentDone(), "Unpacking files..."));

                Thread.sleep(100);
            }
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        this.serverInstallationStatusMonitor.setInstallationStatus(serverId, new InstallationStatus(2, 100, "Unpacking completed!"));
    }

    private Path downloadServerFilesForModpack(int serverId, final ModPack modPack, int serverPackId, Path downloadPath) throws CouldNotDownloadServerFilesException
    {
        String serverDownloadUrl = this.curseForgeClient.getServerDownloadUrl(modPack.getId(), serverPackId);
        //TODO: Fix url. Parenthesis "(" and ")" still not work
        serverDownloadUrl = serverDownloadUrl.replaceAll(" ", "%20");
        return this.curseForgeClient.downloadServerFile(serverId, modPack, serverDownloadUrl, downloadPath);
    }

    private Path prepareServerPathForNewModpack(String username, ModPack modPack) {
        String modpackName = this.serverDirNameCorrector.convert(modPack.getName());
        Path path = Paths.get(config.getServersDir()).resolve(username).resolve(modpackName.replace(" ", "-"));

        Path resultPath = path;
        int number = 1;
        while (isServerPathOccupied(resultPath))
        {
            resultPath = Paths.get(path.toAbsolutePath() + "-" + number);
            number++;
        }
        return resultPath;
    }

    private boolean isServerPathOccupied(Path serverPath)
    {
        return Files.exists(serverPath);
    }

    private boolean isModPackAlreadyDownloaded(final Path downloadPath)
    {
        return Files.exists(downloadPath);
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
}
