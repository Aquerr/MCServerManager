package pl.bartlomiejstepien.mcsm.domain.server;

import com.github.t9t.minecraftrconclient.RconClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;
import pl.bartlomiejstepien.mcsm.config.Config;
import pl.bartlomiejstepien.mcsm.domain.dto.JavaDto;
import pl.bartlomiejstepien.mcsm.domain.dto.ServerDto;
import pl.bartlomiejstepien.mcsm.domain.exception.CouldNotInstallServerException;
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
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

@Component
public class ServerManagerImpl implements ServerManager
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerManagerImpl.class);
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();
    private static final ExecutorService SERVER_INSTALLATION_EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();
    private static final Map<Integer, Process> SERVER_PROCESSES = new HashMap<>();
    private static final Map<Integer, InstalledServer> RUNNING_SERVERS = new HashMap<>();
    private static final String SERVER_PROPERTIES_FILE_NAME = "server.properties";

    private final Config config;
    private final ServerService serverService;
    private final ServerProcessHandler serverProcessHandler;
    private final ServerStartFileFinder serverStartFileFinder;
    private final JavaService javaService;
    private final UserService userService;

    private final Map<Platform, AbstractServerInstallationStrategy<? extends ServerInstallationRequest>> installationStrategyMap;

    private final Set<Integer> serverIdsUnderInstallation = new HashSet<>();
    private final Queue<ServerInstallationTask> serverInstallationQueue = new ConcurrentLinkedQueue<>();

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
        SERVER_INSTALLATION_EXECUTOR_SERVICE.submit(this::installQueuedServers);
    }


    @Override
    public void startServer(ServerDto serverDto)
    {
        if (isRunning(serverDto))
            throw new RuntimeException("Server is already running!");

        final InstalledServer installedServer = convertToInstalledServer(serverDto);
        final Process process = serverProcessHandler.startServerProcess(installedServer);
        if (process == null)
            throw new RuntimeException("Could not start server with id= " + serverDto.getId());

        RUNNING_SERVERS.put(installedServer.getId(), installedServer);
        SERVER_PROCESSES.put(serverDto.getId(), process);
    }

    @Override
    public boolean stopServer(ServerDto serverDto)
    {
        try
        {
            LOGGER.info("Sending stop command to server id={}", serverDto.getId());
            sendCommand(serverDto, "stop");
        }
        catch (ServerNotRunningException exception)
        {
            LOGGER.warn("Server is not running");
            return true;
        }

        LOGGER.info("Scheduling kill server process task for server id = {}", serverDto.getId());
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
        try
        {
            return scheduledFuture.get();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        catch (ExecutionException e)
        {
            e.printStackTrace();
        }
        return false;
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
            LOGGER.error("Could not send command to server id={}, name={}. Reason: {}", serverDto.getId(), serverDto.getName(), exception.getMessage());
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
        LOGGER.debug("Getting properties from {} file for server id={}", SERVER_PROPERTIES_FILE_NAME, serverDto.getId());

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
            LOGGER.warn("{} file does not exist for server id={}", SERVER_PROPERTIES_FILE_NAME, serverDto.getId());
        }
    }

    @Override
    public void saveProperties(ServerDto serverDto, ServerProperties serverProperties)
    {
        LOGGER.info("Saving server properties for server id={}", serverDto.getId());
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
        LOGGER.info("Saved server properties for server id={}", serverDto.getId());
    }

    @Override
    public void deleteServer(ServerDto serverDto)
    {
        try
        {
            boolean status = stopServer(serverDto);
            LOGGER.info("Is server stopped={}", status);
            if(status)
            {
                LOGGER.info("Deleting server files");
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
        this.serverInstallationQueue.add(new ServerInstallationTask(serverId, serverInstallationRequest));
        return serverId;
    }

    private void installQueuedServers()
    {
        while (true)
        {
            if (this.serverInstallationQueue.isEmpty())
            {
                try
                {
                    Thread.sleep(4000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                installServer(this.serverInstallationQueue.poll());
            }
        }
    }

    private void installServer(ServerInstallationTask task)
    {
        LOGGER.info("Starting server installation for {}, ", task);
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
            LOGGER.info("Server id={} is ready!", serverId);
            serverInstallationStatusMonitor.setInstallationStatus(serverId, new InstallationStatus(5, 100, "Server installed!"));
            SCHEDULED_EXECUTOR_SERVICE.schedule(() -> serverInstallationStatusMonitor.clearStatus(task.getServerId()), 1, TimeUnit.HOURS);
        }
        catch (Exception exception)
        {
            this.serverInstallationStatusMonitor.setInstallationStatus(serverId, new InstallationStatus(-1, 0, exception.getMessage()));
            LOGGER.error(exception.getMessage(), exception);
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
        LOGGER.info("Looking for server start file...");
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
        ServerDto serverDto = new ServerDto(serverId, serverName, serverPath.toString());
        serverDto.setJavaId(javaId);
        serverDto.setPlatform(platform.getName());
        this.serverService.saveNewServer(serverDto);

        this.serverService.addServerToUser(userService.findByUsername(username).getId(), serverDto);
        return this.serverService.getServerByPath(serverPath.toString())
                .map(ServerDto::getId)
                .orElse(-1);
    }
}
