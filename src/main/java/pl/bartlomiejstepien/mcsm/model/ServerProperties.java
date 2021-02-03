package pl.bartlomiejstepien.mcsm.model;

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

//    @NotNull
//    @NotBlank
//    @JsonProperty(value = PROPERTY_NAME_LEVEL_NAME)
//    private String levelName;
//
//    @JsonProperty(value = PROPERTY_NAME_ONLINE_MODE)
//    private boolean onlineMode;
//
//    @JsonProperty(value = PROPERTY_NAME_SERVER_PORT)
//    private int port;
//
//    @JsonProperty(value = PROPERTY_NAME_PVP)
//    private boolean pvp;
//
//    @JsonProperty(value = PROPERTY_NAME_RCON_PORT)
//    private int rconPort;
//
//    @JsonProperty(value = PROPERTY_NAME_SPAWN_PROTECTION)
//    private int spawnProtection;
//
//    @NotNull
//    @JsonProperty(value = PROPERTY_NAME_MOTD)
//    private String motd;
//
//    @NotNull
//    @JsonProperty(value = PROPERTY_NAME_RCON_PASSWORD)
//    private String rconPassword;

    private final Properties properties = new Properties();

    public ServerProperties()
    {

    }

    public String getLevelName()
    {
        return this.getProperty(PROPERTY_NAME_LEVEL_NAME);
    }

    public boolean isOnlineMode()
    {
        return this.getProperty(PROPERTY_NAME_ONLINE_MODE);
    }

    public int getPort()
    {
        return this.getProperty(PROPERTY_NAME_SERVER_PORT);
    }

    public boolean isPvp()
    {
        return this.getProperty(PROPERTY_NAME_PVP);
    }

    public int getRconPort()
    {
        return this.getProperty(PROPERTY_NAME_RCON_PORT);
    }

    public String getRconPassword()
    {
        return this.getProperty(PROPERTY_NAME_RCON_PASSWORD);
    }

    public int getSpawnProtection()
    {
        return this.getProperty(PROPERTY_NAME_SPAWN_PROTECTION);
    }

    public String getMotd()
    {
        return this.getProperty(PROPERTY_NAME_MOTD);
    }

//    public void setLevelName(String levelName)
//    {
//        this.levelName = levelName;
//    }
//
//    public boolean isOnlineMode()
//    {
//        return onlineMode;
//    }
//
//    public void setOnlineMode(boolean onlineMode)
//    {
//        this.onlineMode = onlineMode;
//    }
//
//    public int getPort()
//    {
//        return port;
//    }
//
//    public void setPort(int port)
//    {
//        this.port = port;
//    }
//
//    public boolean isPvp()
//    {
//        return pvp;
//    }
//
//    public void setPvp(boolean pvp)
//    {
//        this.pvp = pvp;
//    }
//
//    public int getRconPort()
//    {
//        return rconPort;
//    }
//
//    public void setRconPort(int rconPort)
//    {
//        this.rconPort = rconPort;
//    }
//
//    public String getRconPassword()
//    {
//        return rconPassword;
//    }
//
//    public void setRconPassword(String rconPassword)
//    {
//        this.rconPassword = rconPassword;
//    }
//
//    public void setSpawnProtection(int spawnProtection)
//    {
//        this.spawnProtection = spawnProtection;
//    }
//
//    public int getSpawnProtection()
//    {
//        return this.spawnProtection;
//    }
//
//    public void setMotd(String motd)
//    {
//        this.motd = motd;
//    }
//
//    public String getMotd()
//    {
//        return this.motd;
//    }

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

    public void setProperty(final String propertyName, String value)
    {
        this.properties.setProperty(propertyName, value);
    }

    public <T> T getProperty(final String propertyName)
    {
        return (T) this.properties.getProperty(propertyName);
    }

    public Properties getProperties()
    {
        return this.properties;
    }
}
