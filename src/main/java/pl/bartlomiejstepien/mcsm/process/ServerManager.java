package pl.bartlomiejstepien.mcsm.process;

import pl.bartlomiejstepien.mcsm.dto.ServerDto;
import pl.bartlomiejstepien.mcsm.exception.ServerNotRunningException;
import pl.bartlomiejstepien.mcsm.model.ServerProperties;

import java.util.List;
import java.util.concurrent.Future;

public interface ServerManager
{
//    void sendCommand(String name)

    void startServer(final ServerDto serverDto);

    Future<Boolean> stopServer(final ServerDto serverDto);

    List<String> getLatestServerLog(ServerDto serverDto, int numberOfLines);

    void sendCommand(final ServerDto serverDto, final String command) throws ServerNotRunningException;

    boolean isRunning(ServerDto serverDto);

    void loadProperties(final ServerDto serverDto);

    void saveProperties(ServerDto serverDto, ServerProperties serverProperties);

    void deleteServer(final ServerDto serverDto);
}
