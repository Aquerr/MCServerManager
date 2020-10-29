package pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.*;

public class ServerProperties
{
    @NotNull
    @NotBlank
    private String levelName;

    private boolean onlineMode;

    private int port;

    private boolean pvp;
    private int rconPort;
    @NotNull
    @NotBlank
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
        properties.put("level-name", this.levelName);
        properties.put("online-mode", String.valueOf(this.onlineMode));
        properties.put("server-port", String.valueOf(this.port));
        properties.put("pvp", String.valueOf(this.pvp));
        properties.put("rcon.port", String.valueOf(this.rconPort));
        properties.put("rcon.password", String.valueOf(this.rconPassword));

        return properties;
    }

    private void fillProperties()
    {

    }
}
