package pl.bartlomiejstepien.mcsm.domain.server;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Component
public class EulaAcceptor
{
    public void acceptEula(Path serverPath) throws IOException
    {
        final Path eulaFilePath = serverPath.resolve("eula.txt");
        if (Files.notExists(eulaFilePath))
        {
            Files.createFile(eulaFilePath);
        }
        Files.write(eulaFilePath, "eula=true".getBytes(StandardCharsets.UTF_8), StandardOpenOption.WRITE);
    }
}
