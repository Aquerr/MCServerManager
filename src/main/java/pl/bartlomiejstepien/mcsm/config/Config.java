package pl.bartlomiejstepien.mcsm.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;
import pl.bartlomiejstepien.mcsm.domain.platform.Platform;
import pl.bartlomiejstepien.mcsm.domain.server.AbstractServerInstallationStrategy;
import pl.bartlomiejstepien.mcsm.domain.server.ServerInstallationRequest;
import pl.bartlomiejstepien.mcsm.domain.server.forge.ForgeServerInstallationStrategy;
import pl.bartlomiejstepien.mcsm.domain.server.spigot.SpigotServerInstallationStrategy;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Configuration
@PropertySource("classpath:application.yml")
public class Config
{
    @Value("${downloads-dir}")
    private String downloadsDir;

    @Value("${servers-dir}")
    private String serversDir;

    @Value("${CURSEFORGE_API_KEY:}")
    private String curseForgeApiKey;

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

    public String getCurseForgeApiKey() {
        return curseForgeApiKey;
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

    @Bean
    public RestTemplate curseForgeRestTemplate()
    {
        return new RestTemplateBuilder()
                .defaultHeader("x-api-key", this.curseForgeApiKey)
                .build();
    }
}
