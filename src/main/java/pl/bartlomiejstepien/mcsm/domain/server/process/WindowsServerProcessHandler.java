package pl.bartlomiejstepien.mcsm.domain.server.process;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.mcsm.domain.dto.ServerDto;
import pl.bartlomiejstepien.mcsm.domain.model.InstalledServer;
import pl.bartlomiejstepien.mcsm.domain.model.ServerId;
import pl.bartlomiejstepien.mcsm.util.IsWindowsCondition;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@Conditional(IsWindowsCondition.class)
@Slf4j
public class WindowsServerProcessHandler extends AbstractServerProcessHandler
{
    private static final String PID_FILE_NAME = "server.pid";

    private static final Pattern PATTERN = Pattern.compile("^\\d+.*$");

    @Override
    public Process startServerProcess(InstalledServer installedServer)
    {
        try
        {
            final String[] commandArray = new String[] {"cmd", "/c", "start", installedServer.getStartFilePath().getFileName().toString(), "title", installedServer.getName()};
            log.info("Starting server process with java {} in {} with commands: {}", installedServer.getJavaPath(), installedServer.getServerDir().toAbsolutePath(), Arrays.toString(commandArray));
            final ProcessBuilder processBuilder = new ProcessBuilder(commandArray);
            processBuilder.directory(installedServer.getServerDir().toFile());
            Map<String, String> environment = processBuilder.environment();
            environment.put("Path", installedServer.getJavaPath() + ";" + System.getenv("Path"));
            final Process process = processBuilder.start();

            final long pid = process.pid();

            log.info("Server process id = " + pid);
            Files.write(getPidFilePath(installedServer.getServerDir().toAbsolutePath().toString()),
                    String.valueOf(pid).getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);

            SERVER_PROCESSES.put(ServerId.of(installedServer.getId()), process);

            return process;
        }
        catch (IOException e)
        {
            log.error("Could not start server with id=" + installedServer.getId(), e);
        }
        return null;
    }

    @Override
    protected void doProcessStop(long processId) throws IOException
    {
        Runtime.getRuntime().exec("taskkill /T /PID " + processId);
    }

    @Override
    protected boolean isPidAlive(long processId)
    {
        ProcessBuilder processBuilder = new ProcessBuilder("tasklist.exe", "/FI", "\"PID eq {pid}\"".replace("{pid}", String.valueOf(processId)));

        try
        {
            Process process = processBuilder.start();
            InputStreamReader isReader = new InputStreamReader(process.getInputStream());
            BufferedReader bReader = new BufferedReader(isReader);
            String strLine = null;
            boolean isPidRunning = false;
            while ((strLine= bReader.readLine()) != null) {
                if (strLine.contains(" " + processId + " ")) {
                    isPidRunning = true;
                }
            }

            process.destroy();

            return isPidRunning;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public long getServerProcessId(ServerDto serverDto)
    {
        try
        {
            Path pidFilePath = getPidFilePath(serverDto.getServerDir());
            if (Files.notExists(pidFilePath))
                return -1;

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

    private Path getPidFilePath(String serverDir)
    {
        return Paths.get(serverDir).resolve(PID_FILE_NAME);
    }
}
