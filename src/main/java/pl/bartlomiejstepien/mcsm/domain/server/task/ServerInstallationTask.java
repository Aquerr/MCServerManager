package pl.bartlomiejstepien.mcsm.domain.server.task;

import pl.bartlomiejstepien.mcsm.domain.server.ServerInstallationRequest;

public class ServerInstallationTask
{
    private final int serverId;
    private final ServerInstallationRequest serverInstallationRequest;

    public ServerInstallationTask(int serverId, ServerInstallationRequest serverInstallationRequest)
    {
        this.serverId = serverId;
        this.serverInstallationRequest = serverInstallationRequest;
    }

    public int getServerId()
    {
        return serverId;
    }

    public ServerInstallationRequest getServerInstallationRequest()
    {
        return serverInstallationRequest;
    }
}
