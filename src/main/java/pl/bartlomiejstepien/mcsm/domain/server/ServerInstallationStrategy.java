package pl.bartlomiejstepien.mcsm.domain.server;

import pl.bartlomiejstepien.mcsm.domain.model.InstalledServer;

public interface ServerInstallationStrategy<T extends ServerInstallationRequest>
{
    InstalledServer install(T serverInstallationRequest);
}
