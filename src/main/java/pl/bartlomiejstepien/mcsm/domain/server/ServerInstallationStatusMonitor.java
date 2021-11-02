package pl.bartlomiejstepien.mcsm.domain.server;

import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.mcsm.domain.model.InstallationStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class ServerInstallationStatusMonitor
{
    private final Map<Integer, InstallationStatus> modpackInstallationStatus = new HashMap<>();

    public void setInstallationStatus(final int serverId, final InstallationStatus installationStatus)
    {
        this.modpackInstallationStatus.put(serverId, installationStatus);
    }

    public Optional<InstallationStatus> getInstallationStatus(final int serverId)
    {
        return Optional.ofNullable(this.modpackInstallationStatus.get(serverId));
    }
}
