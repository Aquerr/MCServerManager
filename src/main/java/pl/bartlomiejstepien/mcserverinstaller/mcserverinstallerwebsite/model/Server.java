package pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model;

import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.repository.dto.ServerDto;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Server
{
    private int id;
    private String name;
    private String path;

    private User user;

    private List<String> players;



    // Properties
    private String levelName;
    private boolean onlineMode;

    public static Server fromDAO(final ServerDto serverDto)
    {
        //TODO: Load server information from server.properties.
        final String serverPath = serverDto.getPath();

        final Server server = new Server(serverDto.getId(), "TEST", serverDto.getPath(), serverDto.getUser());

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
        }

        return server;
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
}
