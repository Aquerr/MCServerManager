package pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model;

import com.github.t9t.minecraftrconclient.RconClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.exception.ServerNotRunningException;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.repository.dto.ServerDto;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.repository.dto.UserDto;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.util.ProcessUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Server
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    private static final Map<Integer, Process> SERVER_PROCESSES = new HashMap<>();

    private int id;
    private String name;
    private String serverDir;

    private final List<User> users = new ArrayList<>();

    private final List<String> players = new LinkedList<>();

    private Path startFilePath;

    // Properties
//    private String levelName;
//    private boolean onlineMode;
//    private int port;
//    private boolean pvp;

    private final ServerProperties serverProperties = new ServerProperties();

    //Query

    //Rcon
//    private int rconPort;
//    private String rconPassword;

    public static Server fromDto(final ServerDto serverDto)
    {
        //TODO: Load server information from server.properties.
        final String serverPath = serverDto.getPath();
//        final User user = serverDto.getUser().toUser();
        final Server server = new Server(serverDto.getId(), serverDto.getPath().substring(serverPath.lastIndexOf(File.separator) + 1), serverPath);

        for (final UserDto userDto : serverDto.getUsers())
        {
            final User user = new User(userDto.getId(), userDto.getUsername(), userDto.getPassword());
            user.addServer(server);
            server.addUser(user);
        }

        // Find start file. BATCH or SHELL depending on operating system.
        Path startFilePath = null;

        final String osName = System.getProperty("os.name");
        if (osName.contains("win") || osName.contains("Win"))
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
            server.setStartFilePath(startFilePath);


        //Load server.properties
        server.loadProperties();

        return server;
    }

    public void setStartFilePath(Path startFilePath)
    {
        this.startFilePath = startFilePath;
    }

    public Server()
    {

    }

    public Server(int id, String name, String serverDir)
    {
        this.id = id;
        this.name = name;
        this.serverDir = serverDir;
    }

    public void setId(int id)
    {
        this.id = id;
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

    public String getServerDir()
    {
        return this.serverDir;
    }

    public List<User> getUsers()
    {
        return users;
    }

    public void addUser(final User user)
    {
        this.users.add(user);
    }

    public void addUsers(List<User> users)
    {
        this.users.addAll(users);
    }

    public void removeUser(final User user)
    {
        this.users.remove(user);
    }

    public Path getStartFilePath()
    {
        return startFilePath;
    }

    public ServerProperties getServerProperties()
    {
        return serverProperties;
    }

    public void start()
    {
        try
        {
            final String serverDir = Paths.get(this.serverDir).toAbsolutePath().toString();

            Process process;

            final String osName = System.getProperty("os.name");
            if (osName.contains("Win") || osName.contains("win"))
            {
                process = Runtime.getRuntime().exec("cmd /c start " + this.startFilePath.getFileName().toString() + " title " + this.name, null, new File(serverDir));
//                process = Runtime.getRuntime().exec(new String[]{"powershell.exe", "(Start-Process .\\" + this.startFilePath.getFileName().toString() + "-passthru).Id"}, null, new File(serverDir));

            }
            else
            {
                process = Runtime.getRuntime().exec("sh " + this.startFilePath.getFileName().toString(), null, new File(serverDir));
            }
            SERVER_PROCESSES.put(this.id, process);
        }
        catch (IOException e)
        {
            LOGGER.error("Could not start server with id=" + this.id, e);
        }
    }

    public void stop()
    {
        try
        {
            postCommand("stop");
        }
        catch (ServerNotRunningException exception)
        {
            return;
        }

        SCHEDULED_EXECUTOR_SERVICE.schedule(() -> {
            final Process process = SERVER_PROCESSES.get(this.id);

            if (process != null)
            {
                if (process.isAlive())
                {
                    process.destroy();
                }
            }

            try
            {
                final long processId = ProcessUtil.getProcessID(this.name);
                Runtime.getRuntime().exec("taskkill /F /T /PID " + processId);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            SERVER_PROCESSES.remove(this.id);
        }, 20, TimeUnit.SECONDS);
    }

    public boolean isRunning()
    {
        final Process serverProcess = SERVER_PROCESSES.get(this.id);
        if (serverProcess != null && serverProcess.isAlive())
            return true;

        final long serverProcessId = ProcessUtil.getProcessID(this.name);
        return serverProcessId != -1;
    }

    public void saveProperties(ServerProperties serverProperties)
    {
        LOGGER.info("Saving server properties for server id=" + this.id);
        final Path propertiesFilePath = Paths.get(this.serverDir).resolve("server.properties");

        try
        {
            final Properties properties = new Properties();

            //Load
            try(final InputStream inputStream = Files.newInputStream(propertiesFilePath))
            {
                properties.load(inputStream);
            }

            //Add new values
            for (final Map.Entry<String, String> entry : serverProperties.toMap().entrySet())
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
        LOGGER.info("Saved server properties for server id=" + this.id);
    }

    public void loadProperties()
    {
        if (Files.exists(Paths.get(serverDir).resolve("server.properties")))
        {
            final Properties properties = new Properties();
            try(final InputStream inputStream = Files.newInputStream(Paths.get(serverDir).resolve("server.properties")))
            {
                properties.load(inputStream);
            }
            catch (IOException exception)
            {
                exception.printStackTrace();
            }
            final String levelName = properties.getProperty("level-name");
            final boolean onlineMode = Boolean.parseBoolean(properties.getProperty("online-mode"));
            final int port = Integer.parseInt(properties.getProperty("server-port"));
            final boolean pvp = Boolean.parseBoolean(properties.getProperty("pvp"));
            final int rconPort = properties.getProperty("rcon.port") != null ? Integer.parseInt(properties.getProperty("rcon.port")) : 0;
            final String rconPassword = properties.getProperty("rcon.password") != null ? properties.getProperty("rcon.password") : "";

            serverProperties.setLevelName(levelName);
            serverProperties.setOnlineMode(onlineMode);
            serverProperties.setPort(port);
            serverProperties.setPvp(pvp);
            serverProperties.setRconPort(rconPort);
            serverProperties.setRconPassword(rconPassword);
        }
    }

    public void postCommand(final String command) throws ServerNotRunningException
    {
        if (!this.isRunning())
            throw new ServerNotRunningException();

        try(final RconClient rconClient = RconClient.open("localhost", this.serverProperties.getRconPort(), this.serverProperties.getRconPassword()))
        {
            rconClient.sendCommand(command);
        }
        catch(final Exception exception)
        {
            System.out.println("Could not send a command to the server. Server id = " + getId() + " | Server name = " + getName());
        }
    }

}
