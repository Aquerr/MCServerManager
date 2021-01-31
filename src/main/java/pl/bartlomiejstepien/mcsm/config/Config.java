package pl.bartlomiejstepien.mcsm.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@PropertySource("classpath:application.properties")
public class Config
{
    @Value("${downloads-dir}")
    private String downloadsDir;

    @Value("${servers-dir}")
    private String serversDir;

    public String getServersDir()
    {
        return serversDir;
    }

    public String getDownloadsDir()
    {
        return downloadsDir;
    }

    public Path getServersDirPath()
    {
        return Paths.get(".").resolve(serversDir);
    }

    public Path getDownloadsDirPath()
    {
        return Paths.get(".").resolve(downloadsDir);
    }
}
