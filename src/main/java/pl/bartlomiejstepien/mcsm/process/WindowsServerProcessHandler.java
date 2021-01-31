package pl.bartlomiejstepien.mcsm.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.mcsm.dto.ServerDto;
import pl.bartlomiejstepien.mcsm.util.IsWindowsCondition;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@Conditional(IsWindowsCondition.class)
public class WindowsServerProcessHandler implements ServerProcessHandler
{
    private static final Logger LOGGER = LoggerFactory.getLogger(WindowsServerProcessHandler.class);
    private static final Pattern PATTERN = Pattern.compile("^[0-9]+.*$");

    @Override
    public Process startServerProcess(ServerDto serverDto)
    {
        try
        {
            final String serverDir = Paths.get(serverDto.getServerDir()).toAbsolutePath().toString();
            return Runtime.getRuntime().exec("cmd /c start " + serverDto.getStartFilePath().getFileName().toString() + " title " + serverDto.getName(), null, new File(serverDir));
        }
        catch (IOException e)
        {
            LOGGER.error("Could not start server with id=" + serverDto.getId(), e);
        }
        return null;
    }

    @Override
    public void stopServerProcess(ServerDto serverDto) throws IOException
    {
        final long processId = getServerProcessId(serverDto);
        Runtime.getRuntime().exec("taskkill /F /T /PID " + processId);
    }

    @Override
    public long getServerProcessId(ServerDto serverDto)
    {
        try
        {
            final Process process = Runtime.getRuntime().exec(new String[]{"powershell.exe", "Get-Process | Where-Object {$_.MainWindowTitle -Like \"'*" + serverDto.getName() + "*'\"}"});
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                line = line.trim();

                if (line.equals(""))
                    continue;

                if (!PATTERN.matcher(line).matches())
                    continue;

                final String[] splitLine = line.split(" ");
                final List<String> processLineArgs = Arrays.stream(splitLine).filter(x-> !x.isEmpty() && !x.trim().equals("")).collect(Collectors.toList());
                return Long.parseLong(processLineArgs.get(5));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return -1;
    }
}
