package pl.bartlomiejstepien.mcsm.domain.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import org.springframework.lang.Nullable;

import java.util.*;

public class ServerProperties
{
    public static final String PROPERTY_NAME_LEVEL_NAME = "level-name";
    public static final String PROPERTY_NAME_ONLINE_MODE = "online-mode";
    public static final String PROPERTY_NAME_SERVER_PORT = "server-port";
    public static final String PROPERTY_NAME_PVP = "pvp";
    public static final String PROPERTY_NAME_RCON_PORT = "rcon.port";
    public static final String PROPERTY_NAME_RCON_PASSWORD = "rcon.password";
    public static final String PROPERTY_NAME_SPAWN_PROTECTION = "spawn-protection";
    public static final String PROPERTY_NAME_MOTD = "motd";

    private final Properties properties = new Properties();

    public ServerProperties()
    {

    }

//    public String getLevelName()
//    {
//        return this.getProperty(PROPERTY_NAME_LEVEL_NAME);
//    }
//
//    public boolean isOnlineMode()
//    {
//        Optional.ofNullable(this.properties.getProperty(PROPERTY_NAME_ONLINE_MODE, null))
//                .map()
//
//        return Boolean.parseBoolean(this.properties.getProperty(PROPERTY_NAME_ONLINE_MODE));
//    }
//
//    public int getPort()
//    {
//        return Integer.parseInt(this.properties.getProperty(PROPERTY_NAME_SERVER_PORT));
//    }
//
//    public boolean isPvp()
//    {
//        return Boolean.parseBoolean(this.properties.getProperty(PROPERTY_NAME_PVP));
//    }
//
//    public int getRconPort()
//    {
//        return Integer.parseInt(this.properties.getProperty(PROPERTY_NAME_RCON_PORT));
//    }
//
//    public String getRconPassword()
//    {
//        return this.getProperty(PROPERTY_NAME_RCON_PASSWORD);
//    }
//
//    public int getSpawnProtection()
//    {
//        return this.getProperty(PROPERTY_NAME_SPAWN_PROTECTION);
//    }
//
//    public String getMotd()
//    {
//        return this.getProperty(PROPERTY_NAME_MOTD);
//    }
//
    public Map<String, String> getAsMap()
    {
        final Map<String, String> map = new HashMap<>();
        for (final String propertyName : this.properties.stringPropertyNames())
        {
            map.put(propertyName, this.properties.getProperty(propertyName));
        }
        return map;
    }

    @Override
    public String toString()
    {
        return "ServerProperties{" + getAsMap().toString() +'}';
    }

    @JsonAnySetter
    public void setProperty(final String propertyName, String value)
    {
        this.properties.setProperty(propertyName, value);
    }

    @Nullable
    public Integer getIntProperty(final String propertyName)
    {
        try
        {
            return Integer.parseInt(this.properties.getProperty(propertyName));
        }
        catch (NumberFormatException exception)
        {
            return null;
        }
    }

    @JsonAnyGetter
    public <T> T getProperty(final String propertyName)
    {
        return (T) this.properties.getProperty(propertyName);
    }

    public Properties getProperties()
    {
        return this.properties;
    }
}
