package pl.bartlomiejstepien.mcsm.domain.server;

import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.mcsm.domain.model.InstallationStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class ServerInstallationStatusMonitor
{
    private final Map<Long, InstallationStatus> serverInstallationStatus = new HashMap<>();

    public void setInstallationStatus(final long serverId, final InstallationStatus installationStatus)
    {
        this.serverInstallationStatus.put(serverId, installationStatus);
    }

    public Optional<InstallationStatus> getInstallationStatus(final long serverId)
    {
        return Optional.ofNullable(this.serverInstallationStatus.get(serverId));
    }

    public void clearStatus(final int serverId)
    {
        this.serverInstallationStatus.remove(serverId);
    }
}
