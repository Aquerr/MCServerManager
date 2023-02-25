package pl.bartlomiejstepien.mcsm.domain.server.process;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.mcsm.domain.dto.ServerDto;
import pl.bartlomiejstepien.mcsm.domain.model.InstalledServer;
import pl.bartlomiejstepien.mcsm.domain.model.ServerId;
import pl.bartlomiejstepien.mcsm.util.IsUnixCondition;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Map;

@Component
@Conditional(IsUnixCondition.class)
@Slf4j
public class UnixServerProcessHandler extends AbstractServerProcessHandler
{
    /**
     * Current implementation starts the process and saves its pid to the file inside server directory.
     */
    @Override
    public Process startServerProcess(InstalledServer installedServer)
    {
        final Path serverStartFile = installedServer.getStartFilePath();

        log.info("Starting process for server id=" + installedServer.getId());

        try
        {
            final String[] commandArray = new String[] {"bash", serverStartFile.getFileName().toString(), "&"};
            final ProcessBuilder processBuilder = new ProcessBuilder(commandArray);
            processBuilder.directory(installedServer.getServerDir().toFile());
            Map<String, String> environment = processBuilder.environment();
            environment.put("PATH", installedServer.getJavaPath() + ":" + System.getenv("PATH"));
            log.info("Starting server process with java {} in {} with commands: {}", installedServer.getJavaPath(), installedServer.getServerDir().toAbsolutePath(), Arrays.toString(commandArray));
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
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void stopProcess(long processId) throws IOException
    {
        log.info("Stopping process with id = " + processId);
        Runtime.getRuntime().exec("kill " + processId);
    }

    @Override
    protected void forceStopProcess(long processId) throws IOException
    {
        log.info("Force killing process with id = " + processId);
        Runtime.getRuntime().exec("kill -9 " + processId);
    }

    @Override
    public long getServerProcessId(ServerDto serverDto)
    {
        try
        {
            Path pidFilePath = getPidFilePath(serverDto.getServerDir());
            if (Files.notExists(pidFilePath))
                return -1;

            final String pid = Files.readString(pidFilePath, StandardCharsets.UTF_8);
            return Long.parseLong(pid);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    protected boolean isPidAlive(long processId)
    {
        ProcessBuilder processBuilder = new ProcessBuilder("ps -p " + processId);

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

    private Path getPidFilePath(String serverDir)
    {
        return Paths.get(serverDir).resolve(PID_FILE_NAME);
    }
}
