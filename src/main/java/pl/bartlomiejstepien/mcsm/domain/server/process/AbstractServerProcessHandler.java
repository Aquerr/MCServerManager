package pl.bartlomiejstepien.mcsm.domain.server.process;

import lombok.extern.slf4j.Slf4j;
import pl.bartlomiejstepien.mcsm.domain.dto.ServerDto;
import pl.bartlomiejstepien.mcsm.domain.model.ServerId;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class AbstractServerProcessHandler implements ServerProcessHandler
{
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

        try
        {
            //TODO: Wait 60 seconds for process to be destroyed. After that force kill.
            doProcessStop(processId);
            SERVER_PROCESSES.remove(ServerId.of(serverDto.getId()));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    protected abstract void doProcessStop(long processId) throws IOException;

    protected abstract boolean isPidAlive(long processId);

    @Override
    public boolean isRunning(ServerDto serverDto)
    {
        final Process serverProcess = SERVER_PROCESSES.get(ServerId.of(serverDto.getId()));
        if (serverProcess != null && serverProcess.isAlive())
            return true;

        final long serverProcessId = getServerProcessId(serverDto);
        if (serverProcessId == -1L)
            return false;

        if (!isPidAlive(serverProcessId))
        {
            return false;
        }
        return true;
    }
}
