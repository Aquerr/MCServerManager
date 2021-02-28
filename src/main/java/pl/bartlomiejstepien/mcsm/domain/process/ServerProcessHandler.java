package pl.bartlomiejstepien.mcsm.domain.process;

import pl.bartlomiejstepien.mcsm.domain.dto.ServerDto;
import pl.bartlomiejstepien.mcsm.domain.model.InstalledServer;

import java.io.IOException;

public interface ServerProcessHandler
{
    Process startServerProcess(final InstalledServer installedServer);

    void stopServerProcess(final ServerDto serverDto) throws IOException;

    long getServerProcessId(final ServerDto serverDto);
}
