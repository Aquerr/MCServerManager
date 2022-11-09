package pl.bartlomiejstepien.mcsm.domain.server.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.mcsm.domain.dto.ServerDto;
import pl.bartlomiejstepien.mcsm.domain.model.InstalledServer;
import pl.bartlomiejstepien.mcsm.util.IsUnixCondition;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Map;

@Component
@Conditional(IsUnixCondition.class)
public class UnixServerProcessHandler implements ServerProcessHandler
{
    private static final Logger LOGGER = LoggerFactory.getLogger(UnixServerProcessHandler.class);
    private static final String PID_FILE_NAME = "server_pid.txt";

    /**
     * Current implementation starts the process and saves its pid to the file inside server directory.
     */
    @Override
    public Process startServerProcess(InstalledServer installedServer)
    {
        final Path serverStartFile = installedServer.getStartFilePath();

        LOGGER.info("Starting process for server id=" + installedServer.getId());

        try
        {
            final String[] commandArray = new String[] {"sh", "-c", "nohup", serverStartFile.getFileName().toString(), "&"};
            final ProcessBuilder processBuilder = new ProcessBuilder(commandArray);
            processBuilder.directory(installedServer.getServerDir().toFile());
            Map<String, String> environment = processBuilder.environment();
            environment.put("PATH", installedServer.getJavaPath() + ":" + System.getenv("PATH"));
            LOGGER.info("Starting server process with java {} in {} with commands: {}", installedServer.getJavaPath(), installedServer.getServerDir().toAbsolutePath(), Arrays.toString(commandArray));
            final Process process = processBuilder.start();
            //            environment.put("JAVA_HOME", "/usr/lib/jvm/java-1.8.0-openjdk-amd64");
            //            processBuilder.redirectErrorStream(true);
//            new Thread(() -> readInputStream(process.getInputStream())).start();

            final long pid = process.pid();

            LOGGER.info("Server process id = " + pid);
            Files.write(installedServer.getServerDir().resolve(PID_FILE_NAME),
                    String.valueOf(pid).getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);

            return process;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

//    private void readInputStream(InputStream inputStream)
//    {
//        BufferedReader subProcessInputReader =
//                new BufferedReader(new InputStreamReader(inputStream));
//
//        String line = null;
//        while (true)
//        {
//            try
//            {
//                if ((line = subProcessInputReader.readLine()) == null) break;
//            }
//            catch (IOException exception)
//            {
//                exception.printStackTrace();
//            }
//            System.out.println(line);
//        }
//    }

    @Override
    public void stopServerProcess(ServerDto serverDto)
    {
        try
        {
            final Path pidFilePath = getPidFilePath(serverDto);
            final String pid = Files.readString(pidFilePath, StandardCharsets.UTF_8);
            LOGGER.info("Killing process with id = " + pid);
            Runtime.getRuntime().exec("kill " + pid);
            Files.delete(pidFilePath);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public long getServerProcessId(ServerDto serverDto)
    {
        try
        {
            Path pidFilePath = Paths.get(serverDto.getServerDir()).resolve(PID_FILE_NAME);
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

    private Path getPidFilePath(ServerDto serverDto)
    {
        return Paths.get(serverDto.getServerDir()).resolve(PID_FILE_NAME);
    }
}
