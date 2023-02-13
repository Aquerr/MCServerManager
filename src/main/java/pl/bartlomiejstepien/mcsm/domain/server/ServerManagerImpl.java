package pl.bartlomiejstepien.mcsm.domain.server;

import com.github.t9t.minecraftrconclient.RconClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;
import pl.bartlomiejstepien.mcsm.config.Config;
import pl.bartlomiejstepien.mcsm.domain.dto.JavaDto;
import pl.bartlomiejstepien.mcsm.domain.dto.ServerDto;
import pl.bartlomiejstepien.mcsm.domain.dto.UserDto;
import pl.bartlomiejstepien.mcsm.domain.exception.CouldNotStartServerException;
import pl.bartlomiejstepien.mcsm.domain.exception.MissingJavaConfigurationException;
import pl.bartlomiejstepien.mcsm.domain.exception.ServerNotRunningException;
import pl.bartlomiejstepien.mcsm.domain.model.InstallationStatus;
import pl.bartlomiejstepien.mcsm.domain.model.InstalledServer;
import pl.bartlomiejstepien.mcsm.domain.model.ServerProperties;
import pl.bartlomiejstepien.mcsm.domain.platform.Platform;
import pl.bartlomiejstepien.mcsm.domain.server.process.ServerProcessHandler;
import pl.bartlomiejstepien.mcsm.domain.server.task.ServerInstallationTask;
import pl.bartlomiejstepien.mcsm.service.JavaService;
import pl.bartlomiejstepien.mcsm.service.ServerService;
import pl.bartlomiejstepien.mcsm.service.UserService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class ServerManagerImpl implements ServerManager
{
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();
    private static final ExecutorService SERVER_INSTALLATION_EXECUTOR_SERVICE = Executors.newCachedThreadPool();
    private static final String SERVER_PROPERTIES_FILE_NAME = "server.properties";

    private final Config config;
    private final ServerService serverService;
    private final ServerProcessHandler serverProcessHandler;
    private final ServerStartFileFinder serverStartFileFinder;
    private final JavaService javaService;
    private final UserService userService;

    private final Map<Platform, AbstractServerInstallationStrategy<? extends ServerInstallationRequest>> installationStrategyMap;

    private final Set<Integer> serverIdsUnderInstallation = new HashSet<>();
    private final ServerInstallationStatusMonitor serverInstallationStatusMonitor;

    @Autowired
    public ServerManagerImpl(final Config config,
                             @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") final ServerProcessHandler serverProcessHandler,
                             final ServerService serverService,
                             final ServerStartFileFinder serverStartFileFinder,
                             final JavaService javaService,
                             final UserService userService,
                             final ServerInstallationStatusMonitor serverInstallationStatusMonitor,
                             final Map<Platform, AbstractServerInstallationStrategy<? extends ServerInstallationRequest>> installationStrategyMap)
    {
        this.config = config;
        this.serverProcessHandler = serverProcessHandler;
        this.serverService = serverService;
        this.serverStartFileFinder = serverStartFileFinder;
        this.javaService = javaService;
        this.userService = userService;
        this.serverInstallationStatusMonitor = serverInstallationStatusMonitor;
        this.installationStrategyMap = installationStrategyMap;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startup()
    {
        log.info("Preparing file structure...");

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

        log.info("Structure generated!");
    }


    @Override
    public void startServer(ServerDto serverDto)
    {
        if (isRunning(serverDto))
            throw new CouldNotStartServerException("Server is already running!");

        final InstalledServer installedServer = convertToInstalledServer(serverDto);
        final Process process = serverProcessHandler.startServerProcess(installedServer);
        if (process == null)
            throw new CouldNotStartServerException("Could not start server with id= " + serverDto.getId());
    }

    @Override
    public boolean stopServer(ServerDto serverDto)
    {
        try
        {
            log.info("Sending stop command to server id={}", serverDto.getId());
            sendCommand(serverDto, "stop");
        }
        catch (ServerNotRunningException exception)
        {
            log.warn("Server is not running");
            return true;
        }

        log.info("Scheduling kill server process task for server id = {}", serverDto.getId());
        log.info("Server process will be killed in 20 seconds");
        try
        {
            serverProcessHandler.stopServerProcess(serverDto);
            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
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
                log.warn("Something went wrong with reading the log file. It will be removed and created again...");
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

        try(final RconClient rconClient = RconClient.open("localhost",
                serverDto.getServerProperties().getIntProperty(ServerProperties.PROPERTY_NAME_RCON_PORT),
                serverDto.getServerProperties().getProperty(ServerProperties.PROPERTY_NAME_RCON_PASSWORD)))
        {
            final String response = rconClient.sendCommand(command);

            // Put the response in the log file so that it is visible in the browser.
            Files.write(serverDto.getLatestLogFilePath(), response.getBytes(), StandardOpenOption.APPEND);
        }
        catch(final Exception exception)
        {
            log.error("Could not send command to server id={}, name={}. Reason: {}", serverDto.getId(), serverDto.getName(), exception.getMessage());
        }
    }

    @Override
    public boolean isRunning(ServerDto serverDto)
    {
        return serverProcessHandler.isRunning(serverDto);
    }

    @Override
    public void loadProperties(ServerDto serverDto)
    {
        log.debug("Getting properties from {} file for server id={}", SERVER_PROPERTIES_FILE_NAME, serverDto.getId());

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
            props.putAll(properties);
        }
        else
        {
            log.warn("{} file does not exist for server id={}", SERVER_PROPERTIES_FILE_NAME, serverDto.getId());
        }
    }

    @Override
    public void saveProperties(ServerDto serverDto, ServerProperties serverProperties)
    {
        log.info("Saving server properties for server id={}", serverDto.getId());
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
            properties.putAll(serverProperties.getProperties());

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
        log.info("Saved server properties for server id={}", serverDto.getId());
    }

    @Override
    public void deleteServer(ServerDto serverDto)
    {
        try
        {
            boolean status = stopServer(serverDto);
            log.info("Is server stopped={}", status);
            if(status)
            {
                log.info("Deleting server files");
                Path path = Paths.get(serverDto.getServerDir());
                FileSystemUtils.deleteRecursively(path);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        this.serverService.deleteServer(serverDto.getId());
    }

    @Override
    public int queueServerInstallation(ServerInstallationRequest serverInstallationRequest)
    {
        Integer serverId = requestNewServerId();
        this.serverIdsUnderInstallation.add(serverId);
        SERVER_INSTALLATION_EXECUTOR_SERVICE.submit(() -> installServer(new ServerInstallationTask(serverId, serverInstallationRequest)));
        return serverId;
    }

    private void installServer(ServerInstallationTask task)
    {
        log.info("Starting server installation for {}, ", task);
        int serverId = task.getServerId();
        ServerInstallationRequest serverInstallationRequest = task.getServerInstallationRequest();

        try
        {
            JavaDto javaDto = Optional.ofNullable(this.javaService.findFirst())
                    .orElseThrow(() -> new MissingJavaConfigurationException("No available java found!"));

            Platform platform = serverInstallationRequest.getPlatform();
            AbstractServerInstallationStrategy<? extends ServerInstallationRequest> serverInstallationStrategy = this.installationStrategyMap.get(platform);
            InstalledServer installedServer = serverInstallationStrategy.installInternal(serverId, serverInstallationRequest);
            serverId = saveServerToDB(serverId, serverInstallationRequest.getUsername(), installedServer.getName(), serverInstallationRequest.getPlatform(), installedServer.getServerDir(), javaDto.getId());
            log.info("Server id={} is ready!", serverId);
            serverInstallationStatusMonitor.setInstallationStatus(serverId, new InstallationStatus(5, 100, "Server installed!"));
            SCHEDULED_EXECUTOR_SERVICE.schedule(() -> serverInstallationStatusMonitor.clearStatus(task.getServerId()), 1, TimeUnit.HOURS);
        }
        catch (Exception exception)
        {
            this.serverInstallationStatusMonitor.setInstallationStatus(serverId, new InstallationStatus(-1, 0, exception.getMessage()));
            log.error(exception.getMessage(), exception);
            this.serverIdsUnderInstallation.remove(serverId);
        }
        finally
        {
            this.serverIdsUnderInstallation.remove(serverId);
        }
    }

    private Integer requestNewServerId()
    {
        Integer serverId = this.serverService.getLastFreeServerId();
        while (this.serverIdsUnderInstallation.contains(serverId))
            serverId++;
        return serverId;
    }

    private InstalledServer convertToInstalledServer(final ServerDto serverDto)
    {
        log.info("Looking for server start file...");
        Path serverPath = Paths.get(serverDto.getServerDir());
        Path serverRootDirectory = findServerRootDirectory(serverPath);
        Path serverStartFilePath = serverStartFileFinder.findServerStartFile(serverRootDirectory);
        String javaPath = this.serverService.getJavaForServer(serverDto.getId()).getPath();
        if (serverStartFilePath == null)
            throw new RuntimeException("Could not find server start file!");

        InstalledServer installedServer = new InstalledServer(serverDto.getId(), serverDto.getName(), serverPath, serverStartFilePath);
        installedServer.setJavaPath(javaPath);
        return installedServer;
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

    private int saveServerToDB(Integer serverId, String username, String serverName, Platform platform, Path serverPath, int javaId)
    {
        UserDto user = userService.findByUsername(username);

        ServerDto serverDto = new ServerDto(serverId, serverName, serverPath.toString());
        serverDto.setJavaId(javaId);
        serverDto.setPlatform(platform.getName());
        serverDto.setUsersIds(Arrays.asList(user.getId()));
        this.serverService.saveNewServer(serverDto);

//        this.serverService.addServerToUser(userService.findByUsername(username).getId(), serverDto);
        return this.serverService.getServerByPath(serverPath.toString())
                .map(ServerDto::getId)
                .orElse(-1);
    }
}
