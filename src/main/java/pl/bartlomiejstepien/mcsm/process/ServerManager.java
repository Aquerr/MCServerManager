package pl.bartlomiejstepien.mcsm.process;

import pl.bartlomiejstepien.mcsm.dto.ServerDto;
import pl.bartlomiejstepien.mcsm.exception.ServerNotRunningException;

import java.util.List;

public interface ServerManager
{
//    void sendCommand(String name)

    void startServer(final ServerDto serverDto);

    void stopServer(final ServerDto serverDto);

    List<String> getLatestServerLog(ServerDto serverDto, int numberOfLines);

    void sendCommand(final ServerDto serverDto, final String command) throws ServerNotRunningException;

    boolean isRunning(ServerDto serverDto);
}
