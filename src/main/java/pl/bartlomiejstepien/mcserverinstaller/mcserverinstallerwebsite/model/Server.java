package pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model;

import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.repository.dto.ServerDto;

import java.io.File;
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
    private int port;
    private boolean pvp;

    //Query

    //Rcon
    private int rconPort;
    private String rconPassword;


    private boolean isRunning;

    public static Server fromDto(final ServerDto serverDto)
    {
        //TODO: Load server information from server.properties.
        final String serverPath = serverDto.getPath();
//        final User user = serverDto.getUser().toUser();
        final Server server = new Server(serverDto.getId(), serverDto.getPath().substring(serverPath.lastIndexOf(File.separator)) + 1, serverPath, null);

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
            final int port = Integer.parseInt(properties.getProperty("server-port"));
            final boolean pvp = Boolean.parseBoolean(properties.getProperty("pvp"));

            server.setLevelName(levelName);
            server.setOnlineMode(onlineMode);
            server.setPort(port);
            server.setPvp(pvp);
            server.setRconPort(Integer.parseInt(properties.getProperty("rcon.port")));
            server.setRconPassword(properties.getProperty("rcon.password"));
        }

        return server;
    }

    private void setPvp(boolean pvp)
    {
        this.pvp = pvp;
    }

    private void setPort(int port)
    {
        this.port = port;
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

    public boolean getPvp()
    {
        return this.pvp;
    }

    public int getPort()
    {
        return port;
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

    public void start()
    {
        //TODO: Start new process by running the server start file.

        //TODO: Store the pid of the process in a file.

        this.isRunning = true;
    }

    public void stop()
    {
        //TODO: Read pid of the process from the file and try to stop it.

        this.isRunning = false;
    }

    public boolean isRunning()
    {
        return this.isRunning;
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

    public void setUser(User user)
    {
        this.user = user;
    }
}
