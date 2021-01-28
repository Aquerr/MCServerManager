package pl.bartlomiejstepien.mcsm.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
public class Config
{
    @Value("${servers-dir}")
    public String serversDir;

    public String getServersDir()
    {
        return serversDir;
    }
}
