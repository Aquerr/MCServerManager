package pl.bartlomiejstepien.mcsm.domain.server;

import pl.bartlomiejstepien.mcsm.domain.platform.Platform;

public interface ServerInstallationRequest
{
    Platform getPlatform();

    String getUsername();
}
