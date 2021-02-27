package pl.bartlomiejstepien.mcsm.domain.process;

import pl.bartlomiejstepien.mcsm.domain.dto.ServerDto;

import java.io.IOException;

public interface ServerProcessHandler
{
    Process startServerProcess(final ServerDto serverDto);

    void stopServerProcess(final ServerDto serverDto) throws IOException;

    long getServerProcessId(final ServerDto serverDto);
}
