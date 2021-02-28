package pl.bartlomiejstepien.mcsm.domain.process;

import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.mcsm.domain.dto.ServerDto;
import pl.bartlomiejstepien.mcsm.domain.model.InstalledServer;
import pl.bartlomiejstepien.mcsm.util.IsUnixCondition;

@Component
@Conditional(IsUnixCondition.class)
public class UnixServerProcessHandler implements ServerProcessHandler
{
    @Override
    public Process startServerProcess(InstalledServer installedServer)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void stopServerProcess(ServerDto serverDto)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getServerProcessId(ServerDto serverDto)
    {
        throw new UnsupportedOperationException();
    }
}
