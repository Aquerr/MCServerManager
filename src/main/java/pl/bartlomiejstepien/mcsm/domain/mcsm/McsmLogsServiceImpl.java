package pl.bartlomiejstepien.mcsm.domain.mcsm;

import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

@Service
public class McsmLogsServiceImpl implements McsmLogsService
{
    private static final String MCSM_LOGS_FILE_PATH = "logs/mcsm-logs.log";

    @Override
    public List<String> getMcsmLogs()
    {
        Path logFilePath = Paths.get(".").resolve(MCSM_LOGS_FILE_PATH);

        try
        {
            return Files.readAllLines(logFilePath);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
