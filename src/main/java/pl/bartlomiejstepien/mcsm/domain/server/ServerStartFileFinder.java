package pl.bartlomiejstepien.mcsm.domain.server;

import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.mcsm.util.SystemUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Stream;

@Component
public class ServerStartFileFinder
{
    private static final String[] POSSIBLE_START_FILE_NAME_SEGMENTS = {"start", "launch", "run", "setup_server"};

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
        try(Stream<Path> walkStream = Files.walk(serverDir))
        {
            return walkStream.filter(this::canBeStartFile)
                    .filter(this::isShellFile)
                    .findFirst()
                    .orElse(null);
        }
    }

    private Path findStartFileWindows(Path serverDir) throws IOException
    {
        try(Stream<Path> walkStream = Files.walk(serverDir))
        {
            return walkStream.filter(this::canBeStartFile)
                    .filter(this::isBatFile)
                    .findFirst()
                    .orElse(null);
        }
    }

    private boolean canBeStartFile(Path fileName)
    {
        return Arrays.stream(POSSIBLE_START_FILE_NAME_SEGMENTS)
                .anyMatch(possibleStartFileName -> fileName.toString().toUpperCase().contains(possibleStartFileName.toUpperCase()));
    }

    private boolean isShellFile(Path path)
    {
        return path.toString().endsWith(".sh");
    }

    private boolean isBatFile(Path path)
    {
        return path.toString().endsWith(".bat");
    }
}
