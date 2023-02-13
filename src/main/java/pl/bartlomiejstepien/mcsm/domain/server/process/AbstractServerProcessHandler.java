package pl.bartlomiejstepien.mcsm.domain.server.process;

import lombok.extern.slf4j.Slf4j;
import pl.bartlomiejstepien.mcsm.domain.dto.ServerDto;
import pl.bartlomiejstepien.mcsm.domain.model.ServerId;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class AbstractServerProcessHandler implements ServerProcessHandler
{
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    protected static final String PID_FILE_NAME = "server.pid";

    protected static final Map<ServerId, Process> SERVER_PROCESSES = new HashMap<>();

    @Override
    public void stopServerProcess(ServerDto serverDto) throws IOException
    {
        log.info("Killing process for server id=" + serverDto.getId());
        final Process process = SERVER_PROCESSES.get(ServerId.of(serverDto.getId()));

        if (process != null)
        {
            if (process.isAlive())
            {
                process.destroy();
            }
        }

        long processId = getServerProcessId(serverDto);
        if (processId == -1L)
            return;

        stopProcess(processId);
        forceStopProcessDelayed(processId, 20);
        SERVER_PROCESSES.remove(ServerId.of(serverDto.getId()));
    }

    @Override
    public boolean isRunning(ServerDto serverDto)
    {
        final Process serverProcess = SERVER_PROCESSES.get(ServerId.of(serverDto.getId()));
        if (serverProcess != null && serverProcess.isAlive())
            return true;

        final long serverProcessId = getServerProcessId(serverDto);
        if (serverProcessId == -1L)
            return false;

        return isPidAlive(serverProcessId);
    }

    private void forceStopProcessDelayed(long processId, long delay)
    {
        ScheduledFuture<?> scheduledFuture = SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(new Runnable()
        {
            private int seconds = 0;

            @Override
            public void run()
            {
                seconds++;

                try
                {

                    if (!isPidAlive(processId))
                        return;

                    if (seconds >= delay)
                    {
                        forceStopProcess(processId);
                    }
                }
                catch (Exception exception)
                {
                    exception.printStackTrace();
                }
            }
        }, 0, 1, TimeUnit.SECONDS);

        SCHEDULED_EXECUTOR_SERVICE.schedule(() ->
        {
            scheduledFuture.cancel(true);
        }, delay + 20L, TimeUnit.SECONDS);
    }

    protected abstract void stopProcess(long processId) throws IOException;

    protected abstract void forceStopProcess(long processId) throws IOException;

    protected abstract boolean isPidAlive(long processId);
}
