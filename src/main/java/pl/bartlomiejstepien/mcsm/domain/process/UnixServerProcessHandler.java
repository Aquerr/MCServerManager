package pl.bartlomiejstepien.mcsm.domain.process;

import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.mcsm.domain.dto.ServerDto;
import pl.bartlomiejstepien.mcsm.domain.process.util.IsUnixCondition;

@Component
@Conditional(IsUnixCondition.class)
public class UnixServerProcessHandler implements ServerProcessHandler
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
