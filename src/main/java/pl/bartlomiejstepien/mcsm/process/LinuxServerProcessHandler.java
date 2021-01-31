package pl.bartlomiejstepien.mcsm.process;

import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.mcsm.dto.ServerDto;
import pl.bartlomiejstepien.mcsm.util.IsUnixCondition;

@Component
@Conditional(IsUnixCondition.class)
public class LinuxServerProcessHandler implements ServerProcessHandler
{
    @Override
    public Process startServerProcess(ServerDto serverDto)
    {
        return null;
    }

    @Override
    public void stopServerProcess(ServerDto serverDto)
    {

    }

    @Override
    public long getServerProcessId(ServerDto serverDto)
    {
        return 0;
    }
}
