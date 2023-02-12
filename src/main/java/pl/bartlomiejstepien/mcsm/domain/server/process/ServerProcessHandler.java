package pl.bartlomiejstepien.mcsm.domain.server.process;

import pl.bartlomiejstepien.mcsm.domain.dto.ServerDto;
import pl.bartlomiejstepien.mcsm.domain.model.InstalledServer;

import java.io.IOException;

public interface ServerProcessHandler
{
    Process startServerProcess(final InstalledServer installedServer);

    void stopServerProcess(final ServerDto serverDto) throws IOException;


    /**
     * Implementations are expected to return -1 if the server is not running.
     */
    long getServerProcessId(final ServerDto serverDto);

    boolean isRunning(ServerDto serverDto);
}
