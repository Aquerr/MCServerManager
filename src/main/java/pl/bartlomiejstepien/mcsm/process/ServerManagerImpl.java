package pl.bartlomiejstepien.mcsm.process;

import com.github.t9t.minecraftrconclient.RconClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;
import pl.bartlomiejstepien.mcsm.dto.ServerDto;
import pl.bartlomiejstepien.mcsm.exception.ServerNotRunningException;
import pl.bartlomiejstepien.mcsm.model.ServerProperties;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;

@Component
public class ServerManagerImpl implements ServerManager
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerManagerImpl.class);
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();
    private static final Map<Integer, Process> SERVER_PROCESSES = new HashMap<>();
    private static final String SERVER_PROPERTIES_FILE_NAME = "server.properties";

    private final ServerProcessHandler serverProcessHandler;

    @Autowired
    public ServerManagerImpl(ServerProcessHandler serverProcessHandler)
    {
        this.serverProcessHandler = serverProcessHandler;
    }

    @Override
    public void startServer(ServerDto serverDto)
    {
        final Process process = serverProcessHandler.startServerProcess(serverDto);
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

//            if (deleteStatus)
//            {
//                stopServer(serverDto);
//                Path path = Paths.get(serverDto.getServerDir());
//                try
//                {
//                    if (!isRunning(serverDto))
//                    {
//                        FileSystemUtils.deleteRecursively(path);
//                        Files.delete(path);
//                    }
//                }
//                catch (IOException e)
//                {
//                    e.printStackTrace();
//                }
//            }
            return true;
        }, 20, TimeUnit.SECONDS);
        return scheduledFuture;
    }

    @Override
    public List<String> getLatestServerLog(final ServerDto serverDto, final int numberOfLines)
    {
        final String serverPath = serverDto.getServerDir();
        final Path latestLogPath = Paths.get(serverPath + File.separator + "logs" + File.separator + "latest.log");

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

        try(final RconClient rconClient = RconClient.open("localhost", serverDto.getServerProperties().getRconPort(), serverDto.getServerProperties().getRconPassword()))
        {
            rconClient.sendCommand(command);
        }
        catch(final Exception exception)
        {
            LOGGER.error("Could not send command to the server. Server id=" + serverDto.getId() + " | Server name=" + serverDto.getName());
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
    }
}
