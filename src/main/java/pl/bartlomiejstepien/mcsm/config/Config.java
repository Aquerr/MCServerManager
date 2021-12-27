package pl.bartlomiejstepien.mcsm.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import pl.bartlomiejstepien.mcsm.domain.platform.Platform;
import pl.bartlomiejstepien.mcsm.domain.server.AbstractServerInstallationStrategy;
import pl.bartlomiejstepien.mcsm.domain.server.ServerInstallationRequest;
import pl.bartlomiejstepien.mcsm.domain.server.ServerInstallationStrategy;
import pl.bartlomiejstepien.mcsm.domain.server.forge.ForgeServerInstallationStrategy;
import pl.bartlomiejstepien.mcsm.domain.server.spigot.SpigotServerInstallationStrategy;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

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

    @Bean
    public Map<Platform, AbstractServerInstallationStrategy<? extends ServerInstallationRequest>> installationStrategyMap(
            ForgeServerInstallationStrategy forgeServerInstallationStrategy,
            SpigotServerInstallationStrategy spigotServerInstallationStrategy)
    {
        final Map<Platform, AbstractServerInstallationStrategy<? extends ServerInstallationRequest>> serverInstallationStrategyMap = new HashMap<>();
        serverInstallationStrategyMap.put(Platform.FORGE, forgeServerInstallationStrategy);
        serverInstallationStrategyMap.put(Platform.SPIGOT, spigotServerInstallationStrategy);
        return serverInstallationStrategyMap;
    }
}
