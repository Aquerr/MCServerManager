package pl.bartlomiejstepien.mcsm.domain.server;

import pl.bartlomiejstepien.mcsm.domain.dto.ServerDto;
import pl.bartlomiejstepien.mcsm.domain.exception.ServerNotRunningException;
import pl.bartlomiejstepien.mcsm.domain.model.ServerProperties;

import java.util.List;
import java.util.concurrent.Future;

public interface ServerManager
{
    void startServer(final ServerDto serverDto);

    Future<Boolean> stopServer(final ServerDto serverDto);

    List<String> getLatestServerLog(ServerDto serverDto, int numberOfLines);

    void sendCommand(final ServerDto serverDto, final String command) throws ServerNotRunningException;

    boolean isRunning(ServerDto serverDto);

    void loadProperties(final ServerDto serverDto);

    void saveProperties(ServerDto serverDto, ServerProperties serverProperties);

    void deleteServer(final ServerDto serverDto);
}
