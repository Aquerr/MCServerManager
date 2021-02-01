package pl.bartlomiejstepien.mcsm.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.*;

public class ServerProperties
{
    public static final String PROPERTY_NAME_LEVEL_NAME = "level-name";
    public static final String PROPERTY_NAME_ONLINE_MODE = "online-mode";
    public static final String PROPERTY_NAME_SERVER_PORT = "server-port";
    public static final String PROPERTY_NAME_PVP = "pvp";
    public static final String PROPERTY_NAME_RCON_PORT = "rcon.port";
    public static final String PROPERTY_NAME_RCON_PASSWORD = "rcon.password";

    @NotNull
    @NotBlank
    @JsonProperty(value = "level_name")
    private String levelName;

    @JsonProperty(value = "online_mode")
    private boolean onlineMode;

    @JsonProperty(value = "server_port")
    private int port;

    @JsonProperty(value = "server_pvp")
    private boolean pvp;

    @JsonProperty(value = "rcon_port")
    private int rconPort;

    @NotNull
    @NotBlank
    @JsonProperty(value = "rcon_password")
    private String rconPassword;

    public ServerProperties()
    {

    }

    public ServerProperties(String levelName, boolean onlineMode, int port, boolean pvp)
    {
        this.levelName = levelName;
        this.onlineMode = onlineMode;
        this.port = port;
        this.pvp = pvp;
    }

    public String getLevelName()
    {
        return levelName;
    }

    public void setLevelName(String levelName)
    {
        this.levelName = levelName;
    }

    public boolean isOnlineMode()
    {
        return onlineMode;
    }

    public void setOnlineMode(boolean onlineMode)
    {
        this.onlineMode = onlineMode;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public boolean isPvp()
    {
        return pvp;
    }

    public void setPvp(boolean pvp)
    {
        this.pvp = pvp;
    }

    public int getRconPort()
    {
        return rconPort;
    }

    public void setRconPort(int rconPort)
    {
        this.rconPort = rconPort;
    }

    public String getRconPassword()
    {
        return rconPassword;
    }

    public void setRconPassword(String rconPassword)
    {
        this.rconPassword = rconPassword;
    }

    public Map<String, String> toMap()
    {
        final Map<String, String> properties = new HashMap<>();
        properties.put(PROPERTY_NAME_LEVEL_NAME, this.levelName);
        properties.put(PROPERTY_NAME_ONLINE_MODE, String.valueOf(this.onlineMode));
        properties.put(PROPERTY_NAME_SERVER_PORT, String.valueOf(this.port));
        properties.put(PROPERTY_NAME_PVP, String.valueOf(this.pvp));
        properties.put(PROPERTY_NAME_RCON_PORT, String.valueOf(this.rconPort));
        properties.put(PROPERTY_NAME_RCON_PASSWORD, String.valueOf(this.rconPassword));

        return properties;
    }

    @Override
    public String toString()
    {
        return "ServerProperties{" +
                "levelName='" + levelName + '\'' +
                ", onlineMode=" + onlineMode +
                ", port=" + port +
                ", pvp=" + pvp +
                ", rconPort=" + rconPort +
                ", rconPassword='" + rconPassword + '\'' +
                '}';
    }
}
