package pl.bartlomiejstepien.mcsm.domain.server;

import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.mcsm.util.SystemUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

@Component
public class ServerStartFileFinder
{
    public Path findServerStartFile(final Path serverDir)
    {
        try
        {
            if (SystemUtil.isWindows())
                return findStartFileWindows(serverDir);
            else return findStartFileUnix(serverDir);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private Path findStartFileUnix(Path serverDir) throws IOException
    {
        return Files.list(serverDir)
                .map(Path::getFileName)
                .filter(this::isStartFile)
                .filter(fileName -> fileName.toString().endsWith(".sh"))
                .findFirst()
                .orElse(null);
    }

    private Path findStartFileWindows(Path serverDir) throws IOException
    {
        return Files.list(serverDir)
                .map(Path::getFileName)
                .filter(this::isStartFile)
                .filter(fileName -> fileName.toString().endsWith(".bat"))
                .findFirst()
                .orElse(null);
    }

    private boolean isStartFile(Path fileName)
    {
        return Arrays.stream(getPossibleStartFileNames())
                .anyMatch(possibleStartFileName -> fileName.toString().toUpperCase().contains(possibleStartFileName.toUpperCase()));
    }

    private String[] getPossibleStartFileNames()
    {
        return new String[]{"start", "launch", "run", "setup_server"};
    }
}
