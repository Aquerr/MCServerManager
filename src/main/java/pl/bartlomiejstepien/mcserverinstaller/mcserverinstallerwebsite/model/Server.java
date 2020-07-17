package pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model;

import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.repository.dto.ServerDto;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Server
{
    private int id;
    private String name;
    private String path;

    private User user;


    private List<String> players;

    private String startFilePath;

    // Properties
    private String levelName;
    private boolean onlineMode;

    //Query

    //Rcon
    private int rconPort;
    private String rconPassword;

    public static Server fromDto(final ServerDto serverDto)
    {
        //TODO: Load server information from server.properties.
        final String serverPath = serverDto.getPath();
        final Server server = new Server(serverDto.getId(), "TEST", serverDto.getPath(), serverDto.getUser());

        // Find start file. BATCH or SHELL depending on operating system.
        Path startFilePath = null;

        final String osName = System.getProperty("os.name");
        if (osName.contains("win"))
        {
            try
            {
                startFilePath = Files.list(Paths.get(serverPath)).filter(file ->
                {
                    String fileName = file.getFileName().toString();
                    if(fileName.endsWith(".bat") && (fileName.contains("start") || fileName.contains("Start") || fileName.contains("launch") || fileName.contains("Launch") || fileName.contains("Run") || fileName.contains("run")))
                        return true;
                    else return false;
                }).findFirst().orElse(null);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            try
            {
                startFilePath = Files.list(Paths.get(serverPath)).filter(file ->
                {
                    String fileName = file.getFileName().toString();
                    if(fileName.endsWith(".sh") && (fileName.contains("start") || fileName.contains("launch") || fileName.contains("run")))
                        return true;
                    else return false;
                }).findFirst().orElse(null);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        if (startFilePath != null)
            server.setStartFilePath(startFilePath.toString());


        //Load server.properties
        if (Files.exists(Paths.get(serverPath).resolve("server.properties")))
        {
            //TODO: Read server.properties

            final Properties properties = new Properties();
            try
            {
                final InputStream inputStream = Files.newInputStream(Paths.get(serverPath).resolve("server.properties"));
                properties.load(inputStream);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            final String levelName = properties.getProperty("level-name");
            final boolean onlineMode = Boolean.parseBoolean(properties.getProperty("online-mode"));

            server.setLevelName(levelName);
            server.setOnlineMode(onlineMode);
            server.setRconPort(Integer.parseInt(properties.getProperty("rcon.port")));
            server.setRconPassword(properties.getProperty("rcon.password"));
        }

        return server;
    }

    public void setRconPort(int rconPort)
    {
        this.rconPort = rconPort;
    }

    public void setRconPassword(String rconPassword)
    {
        this.rconPassword = rconPassword;
    }

    public void setStartFilePath(String startFilePath)
    {
        this.startFilePath = startFilePath;
    }

    public Server()
    {

    }

    public Server(int id, String name, String path, User user)
    {
        this.id = id;
        this.name = name;
        this.path = path;
        this.user = user;
        this.players = new ArrayList<>();
    }

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public List<String> getPlayers()
    {
        return players;
    }

    public String getPath()
    {
        return this.path;
    }

    public User getUser()
    {
        return user;
    }

    public String getLevelName()
    {
        return this.levelName;
    }

    public boolean getOnlineMode()
    {
        return this.onlineMode;
    }

    public void setLevelName(String levelName)
    {
        this.levelName = levelName;
    }

    public void setOnlineMode(boolean onlineMode)
    {
        this.onlineMode = onlineMode;
    }

    public int getRconPort()
    {
        return rconPort;
    }

    public String getRconPassword()
    {
        return rconPassword;
    }

    public String getStartFilePath()
    {
        return startFilePath;
    }

    public void saveProperties(Map<String, String> settings)
    {
        final Path propertiesFilePath = Paths.get(this.path).resolve("server.properties");

        try
        {
            final Properties properties = new Properties();

            //Load
            try(final InputStream inputStream = Files.newInputStream(propertiesFilePath))
            {
                properties.load(inputStream);
            }

            //Add new values
            for (final Map.Entry<String, String> entry : settings.entrySet())
            {
                properties.setProperty(entry.getKey(), entry.getValue());
            }

            //Save
            try(final OutputStream outputStream = Files.newOutputStream(propertiesFilePath))
            {
                properties.store(outputStream, "");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
