package pl.bartlomiejstepien.mcsm.domain.server;

import com.github.t9t.minecraftrconclient.RconClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;
import pl.bartlomiejstepien.mcsm.auth.AuthenticatedUser;
import pl.bartlomiejstepien.mcsm.config.Config;
import pl.bartlomiejstepien.mcsm.domain.dto.ServerDto;
import pl.bartlomiejstepien.mcsm.domain.exception.CouldNotDownloadServerFilesException;
import pl.bartlomiejstepien.mcsm.domain.exception.ServerNotRunningException;
import pl.bartlomiejstepien.mcsm.domain.model.InstalledServer;
import pl.bartlomiejstepien.mcsm.domain.model.ModPack;
import pl.bartlomiejstepien.mcsm.domain.model.ServerProperties;
import pl.bartlomiejstepien.mcsm.domain.platform.Platform;
import pl.bartlomiejstepien.mcsm.domain.process.ServerProcessHandler;
import pl.bartlomiejstepien.mcsm.service.CurseForgeAPIService;
import pl.bartlomiejstepien.mcsm.service.ServerService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

@Component
public class ServerManagerImpl implements ServerManager
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerManagerImpl.class);
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();
    private static final Map<Integer, Process> SERVER_PROCESSES = new HashMap<>();
    private static final Map<Integer, InstalledServer> RUNNING_SERVERS = new HashMap<>();
    private static final String SERVER_PROPERTIES_FILE_NAME = "server.properties";

    private final Config config;
    private final ServerService serverService;
    private final ServerProcessHandler serverProcessHandler;
    private final ServerInstaller serverInstaller;
    private final ServerStartFileFinder serverStartFileFinder;
    private final CurseForgeAPIService curseForgeAPIService;

    @Autowired
    public ServerManagerImpl(final Config config,
                             @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") final ServerProcessHandler serverProcessHandler,
                             final ServerService serverService,
                             final ServerInstaller serverInstaller,
                             final ServerStartFileFinder serverStartFileFinder,
                             final CurseForgeAPIService curseForgeAPIService)
    {
        this.config = config;
        this.serverProcessHandler = serverProcessHandler;
        this.serverService = serverService;
        this.serverInstaller = serverInstaller;
        this.serverStartFileFinder = serverStartFileFinder;
        this.curseForgeAPIService = curseForgeAPIService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startup()
    {
        LOGGER.info("Preparing file structure...");

        final Path serversDirectoryPath = this.config.getServersDirPath();
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
        if (Files.notExists(this.config.getDownloadsDirPath()))
        {
            try
            {
                Files.createDirectory(this.config.getDownloadsDirPath());
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        LOGGER.info("Structure generated!");
    }


    @Override
    public void startServer(ServerDto serverDto)
    {
        if (RUNNING_SERVERS.containsKey(serverDto.getId()))
            throw new RuntimeException("Server is already running!");

        final InstalledServer installedServer = prepareServer(serverDto);
        final Process process = serverProcessHandler.startServerProcess(installedServer);
        if (process == null)
            throw new RuntimeException("Could not start server with id= " + serverDto.getId());

        RUNNING_SERVERS.put(installedServer.getId(), installedServer);
        SERVER_PROCESSES.put(serverDto.getId(), process);
    }

    @Override
    public Future<Boolean> stopServer(ServerDto serverDto)
    {
        try
        {
            LOGGER.info("Sending stop command to server id=" + serverDto.getId());
            sendCommand(serverDto, "stop");
        }
        catch (ServerNotRunningException exception)
        {
            LOGGER.warn("Server is not running");
            return new FutureTask<>(()->true);
        }

        LOGGER.info("Scheduling kill server process task for server id = " + serverDto.getId());
        LOGGER.info("Server process will be killed in 20 seconds");
        ScheduledFuture<Boolean> scheduledFuture = SCHEDULED_EXECUTOR_SERVICE.schedule(() -> {
            LOGGER.info("Killing process for server id=" + serverDto.getId());
            final Process process = SERVER_PROCESSES.get(serverDto.getId());

            if (process != null)
            {
                if (process.isAlive())
                {
                    process.destroy();
                }
            }

            try
            {
                this.serverProcessHandler.stopServerProcess(serverDto);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            SERVER_PROCESSES.remove(serverDto.getId());
            RUNNING_SERVERS.remove(serverDto.getId());

            return true;
        }, 20, TimeUnit.SECONDS);
        return scheduledFuture;
    }

    @Override
    public List<String> getLatestServerLog(final int serverId, final int numberOfLines)
    {
        final ServerDto serverDto = this.serverService.getServer(serverId);
        final Path latestLogPath = serverDto.getLatestLogFilePath();

        if (Files.notExists(latestLogPath))
            return Collections.emptyList();

        try
        {
//            RandomAccessFile randomAccessFile = new RandomAccessFile(latestLogPath.toFile(), RandomAccessFileMode.READ.getValue());
//            randomAccessFile.seek(randomAccessFile.length() - );
//            randomAccessFile.readLine();
            final List<String> lines = Files.readAllLines(latestLogPath, StandardCharsets.ISO_8859_1);
            if (lines.size() < numberOfLines)
                return lines;

            return lines.subList(lines.size() - 1 - numberOfLines, lines.size());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            try
            {
                LOGGER.warn("Something went wrong with reading the log file. It will be removed and created again...");
                Files.delete(latestLogPath);
                Files.createFile(latestLogPath);
            }
            catch (IOException exception)
            {
                exception.printStackTrace();
            }
        }
        return Collections.emptyList();
    }

    @Override
    public void sendCommand(ServerDto serverDto, String command) throws ServerNotRunningException
    {
        if (!isRunning(serverDto))
            throw new ServerNotRunningException();
        loadProperties(serverDto);

        try(final RconClient rconClient = RconClient.open("localhost", Integer.parseInt(serverDto.getServerProperties().getProperty(ServerProperties.PROPERTY_NAME_RCON_PORT)), serverDto.getServerProperties().getProperty(ServerProperties.PROPERTY_NAME_RCON_PASSWORD)))
        {
            final String response = rconClient.sendCommand(command);

            // Put the response in the log file so that it is visible in the browser.
            Files.write(serverDto.getLatestLogFilePath(), response.getBytes(), StandardOpenOption.APPEND);
        }
        catch(final Exception exception)
        {
            LOGGER.error("Could not send command to the server. Server id=" + serverDto.getId() + " | Server name=" + serverDto.getName(), exception);
        }
    }

    @Override
    public boolean isRunning(ServerDto serverDto)
    {
        final Process serverProcess = SERVER_PROCESSES.get(serverDto.getId());
        if (serverProcess != null && serverProcess.isAlive())
            return true;

        final long serverProcessId = this.serverProcessHandler.getServerProcessId(serverDto);
        return serverProcessId != -1;
    }

    @Override
    public void loadProperties(ServerDto serverDto)
    {
        LOGGER.debug("Getting properties from " + SERVER_PROPERTIES_FILE_NAME + " file for server id=" + serverDto.getId());

        final Path serverPropertiesFilePath = Paths.get(serverDto.getServerDir()).resolve(SERVER_PROPERTIES_FILE_NAME);
        if (Files.exists(serverPropertiesFilePath))
        {
            final Properties properties = new Properties();
            try(final InputStream inputStream = Files.newInputStream(serverPropertiesFilePath))
            {
                properties.load(inputStream);
            }
            catch (IOException exception)
            {
                exception.printStackTrace();
            }

            final ServerProperties serverProperties = serverDto.getServerProperties();
            final Properties props = serverProperties.getProperties();
            for (final Map.Entry<Object, Object> entry : properties.entrySet())
            {
                props.put(entry.getKey(), entry.getValue());
            }
        }
        else
        {
            LOGGER.warn(SERVER_PROPERTIES_FILE_NAME + " file does not exist for server id=" + serverDto.getId());
        }
    }

    @Override
    public void saveProperties(ServerDto serverDto, ServerProperties serverProperties)
    {
        LOGGER.info("Saving server properties for server id=" + serverDto.getId());
        final Path propertiesFilePath = Paths.get(serverDto.getServerDir()).resolve(SERVER_PROPERTIES_FILE_NAME);

        try
        {
            final Properties properties = new Properties();

            //Load
            try(final InputStream inputStream = Files.newInputStream(propertiesFilePath))
            {
                properties.load(inputStream);
            }

            //Add new values
            for (final Map.Entry<Object, Object> entry : serverProperties.getProperties().entrySet())
            {
                properties.put(entry.getKey(), entry.getValue());
            }

            //Save
            try(final OutputStream outputStream = Files.newOutputStream(propertiesFilePath))
            {
                properties.store(outputStream, "");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        LOGGER.info("Saved server properties for server id=" + serverDto.getId());
    }

    @Override
    public void deleteServer(ServerDto serverDto)
    {
        CompletableFuture.runAsync(()->{
            try
            {
                boolean status = stopServer(serverDto).get();
                if(status)
                {
                    LOGGER.info("Is server stopped=" + status);
                    LOGGER.info("Deleting server files");
                    Path path = Paths.get(serverDto.getServerDir());
                    FileSystemUtils.deleteRecursively(path);
                    Files.delete(path);
                }
            }
            catch (InterruptedException | ExecutionException | IOException e)
            {
                e.printStackTrace();
            }
        });
        this.serverService.deleteServer(serverDto.getId());
    }

    @Override
    public InstalledServer installServerForModPack(AuthenticatedUser authenticatedUser, ModPack modPack, Path serverPath)
    {
        this.serverInstaller.installServerForModpack(authenticatedUser, modPack, serverPath);

        LOGGER.info("Accepting EULA...");
        acceptEula(serverPath);

        return prepareServer(new ServerDto(0, modPack.getName(), serverPath.toString()));
    }

    @Override
    public int installServerForModpack(AuthenticatedUser authenticatedUser, int modpackId, int serverPackId)
    {
        final ModPack modPack = this.curseForgeAPIService.getModpack(modpackId);
        Path serverPath = prepareServerPathForNewModpack(authenticatedUser, modPack);
        if (isServerPathOccupied(serverPath))
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
                return -1;
            }
        }

        final InstalledServer installedServer = installServerForModPack(authenticatedUser, modPack, serverPath);
        ServerDto serverDto = new ServerDto(installedServer.getId(), installedServer.getName(), installedServer.getServerDir().toString());
        serverDto.setPlatform(Platform.FORGE.getName());
        this.serverService.addServer(authenticatedUser.getId(), serverDto);
        final int serverId = this.serverService.getServerByPath(installedServer.getServerDir().toString())
                .map(ServerDto::getId)
                .orElse(-1);

//        final User user = this.userRepository.find(authenticatedUser.getId());
//        final Server server = new Server(0, installedServer.getName(), installedServer.getServerDir().toString());
//        user.addServer(server);
//        this.serverRepository.save(server);

        LOGGER.info("Server id=" + serverId + " is ready!");
        return serverId;
    }

    private InstalledServer prepareServer(final ServerDto serverDto)
    {
        LOGGER.info("Looking for server start file...");
        Path serverPath = Paths.get(serverDto.getServerDir());
        Path serverRootDirectory = findServerRootDirectory(serverPath);
        Path serverStartFilePath = serverStartFileFinder.findServerStartFile(serverRootDirectory);
        if (serverStartFilePath == null)
            throw new RuntimeException("Could not find server start file!");

        return new InstalledServer(0, serverDto.getName(), serverPath, serverStartFilePath);
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

    private void acceptEula(Path serverPath) {
        final Path eulaFilePath = serverPath.resolve("eula.txt");
        try {
            if (Files.notExists(eulaFilePath)) {
                Files.createFile(eulaFilePath);
            }
            Files.write(eulaFilePath, "eula=true".getBytes(StandardCharsets.UTF_8), StandardOpenOption.WRITE);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Path prepareServerPathForNewModpack(AuthenticatedUser authenticatedUser, ModPack modPack) {
        return Paths.get(config.getServersDir()).resolve(authenticatedUser.getUsername()).resolve(modPack.getName());
    }

    private boolean isServerPathOccupied(Path serverPath)
    {
        return Files.exists(serverPath);
    }

    private boolean isModPackAlreadyDownloaded(final ModPack modPack)
    {
        return Files.exists(this.config.getDownloadsDirPath().resolve(Paths.get(modPack.getName() + "_" + modPack.getVersion())));
    }

    private void downloadServerFilesForModpack(final ModPack modPack, int serverPackId) throws CouldNotDownloadServerFilesException
    {
        String serverDownloadUrl = this.curseForgeAPIService.getServerDownloadUrl(modPack.getId(), serverPackId);
        //TODO: Fix url. Parenthesis "(" and ")" still not work
        serverDownloadUrl = serverDownloadUrl.replaceAll(" ", "%20");
        this.curseForgeAPIService.downloadServerFile(modPack, serverDownloadUrl);
    }
}
