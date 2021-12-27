package pl.bartlomiejstepien.mcsm.domain.server;

import pl.bartlomiejstepien.mcsm.domain.model.InstalledServer;

public abstract class AbstractServerInstallationStrategy<T extends ServerInstallationRequest> implements ServerInstallationStrategy<T>
{
    public InstalledServer installInternal(ServerInstallationRequest serverInstallationRequest)
    {
        return install((T)serverInstallationRequest);
    }
}
