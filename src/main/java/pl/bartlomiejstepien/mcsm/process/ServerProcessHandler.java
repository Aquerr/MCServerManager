package pl.bartlomiejstepien.mcsm.process;

import pl.bartlomiejstepien.mcsm.dto.ServerDto;

import java.io.IOException;

public interface ServerProcessHandler
{
    Process startServerProcess(final ServerDto serverDto);

    void stopServerProcess(final ServerDto serverDto) throws IOException;

    long getServerProcessId(final ServerDto serverDto);
}
