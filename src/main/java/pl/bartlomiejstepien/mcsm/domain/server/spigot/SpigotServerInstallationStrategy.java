package pl.bartlomiejstepien.mcsm.domain.server.spigot;

import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.mcsm.domain.model.InstalledServer;
import pl.bartlomiejstepien.mcsm.domain.server.AbstractServerInstallationStrategy;

@Component
public class SpigotServerInstallationStrategy extends AbstractServerInstallationStrategy<SpigotInstallationRequest>
{
    @Override
    public InstalledServer install(SpigotInstallationRequest serverInstallRequest)
    {
        final String username = serverInstallRequest.getUsername();
        final String version = serverInstallRequest.getVersion();

        //TODO: Preform spigot server installation

        // Check if username already owns server for such version

        // Download spigot

        // Determine free server port

        // Create directory (and necessary files like server.properties)

        // Create and accept eula

        //

        return null;
    }
}
