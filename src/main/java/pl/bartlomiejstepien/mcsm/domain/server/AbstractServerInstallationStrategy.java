package pl.bartlomiejstepien.mcsm.domain.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.bartlomiejstepien.mcsm.domain.exception.CouldNotInstallServerException;
import pl.bartlomiejstepien.mcsm.domain.model.InstalledServer;

import java.io.IOException;

public abstract class AbstractServerInstallationStrategy<T extends ServerInstallationRequest> implements ServerInstallationStrategy<T>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractServerInstallationStrategy.class);

    private final EulaAcceptor eulaAcceptor;

    protected AbstractServerInstallationStrategy(EulaAcceptor eulaAcceptor)
    {
        this.eulaAcceptor = eulaAcceptor;
    }

    public InstalledServer installInternal(int serverId, ServerInstallationRequest serverInstallationRequest)
    {
        try
        {
            //TODO: Remove determineJavaVersionForModpack and set java to first found java. User should properly configure the server after the installation.
            InstalledServer installedServer = install(serverId, (T)serverInstallationRequest);
            this.eulaAcceptor.acceptEula(installedServer.getServerDir());

            LOGGER.info("Accepted EULA");

            return installedServer;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new CouldNotInstallServerException(e);
        }
    }
}
