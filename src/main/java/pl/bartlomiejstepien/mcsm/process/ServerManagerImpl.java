package pl.bartlomiejstepien.mcsm.process;

import com.github.t9t.minecraftrconclient.RconClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.mcsm.dto.ServerDto;
import pl.bartlomiejstepien.mcsm.exception.ServerNotRunningException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class ServerManagerImpl implements ServerManager
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerManagerImpl.class);
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();
    private static final Map<Integer, Process> SERVER_PROCESSES = new HashMap<>();

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
    public void stopServer(ServerDto serverDto)
    {
        try
        {
            LOGGER.info("Sending stop command to server id=" + serverDto.getId());
            sendCommand(serverDto, "stop");
        }
        catch (ServerNotRunningException exception)
        {
            LOGGER.warn("Server is not running.");
            return;
        }

        LOGGER.info("Scheduling kill server process task for server id = " + serverDto.getId());
        LOGGER.info("Server process will be killed in 20 seconds");
        SCHEDULED_EXECUTOR_SERVICE.schedule(() -> {
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
        }, 20, TimeUnit.SECONDS);
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
            final List<String> lines = Files.readAllLines(latestLogPath);
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

        try(final RconClient rconClient = RconClient.open("localhost", serverDto.getServerProperties().getRconPort(), serverDto.getServerProperties().getRconPassword()))
        {
            rconClient.sendCommand(command);
        }
        catch(final Exception exception)
        {
            LOGGER.error("Could not send a command to the server. Server id = " + serverDto.getId() + " | Server name = " + serverDto.getName());
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
}
