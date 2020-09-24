package pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.repository.dto.ServerDto;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.repository.dto.UserDto;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

public class Server
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    private static final Map<Integer, Process> SERVER_PROCESSES = new HashMap<>();

    private int id;
    private String name;
    private String serverDir;

    private final List<User> users = new ArrayList<>();

    private final List<String> players = new LinkedList<>();

    private Path startFilePath;

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
        final Server server = new Server(serverDto.getId(), serverDto.getPath().substring(serverPath.lastIndexOf(File.separator)) + 1, serverPath);

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

    public Path getStartFilePath()
    {
        return startFilePath;
    }

    public void start()
    {
        //TODO: Start new process by running the server start file.

        //TODO: Store the pid of the process in a file.

        try
        {
            final String startFilePath = this.startFilePath.toAbsolutePath().toString();
            final String serverDir = Paths.get(this.serverDir).toAbsolutePath().toString();

            Process process = null;

            final String osName = System.getProperty("os.name");
            if (osName.contains("Win") || osName.contains("win"))
            {
                process = Runtime.getRuntime().exec(startFilePath, null, new File(serverDir));
//                final InputStream inputStream = process.getInputStream();
//                final ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", serverDir.substring(0, 2), "cd \"" + serverDir + "\" && " + this.startFilePath.getFileName().toString());
//                process = processBuilder.start();

//                ForkJoinPool.commonPool().execute(new Thread(() -> {
//                    final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
//                    String str;
//                    while (true)
//                    {
//                        try
//                        {
//                            if ((str = bufferedReader.readLine()) != null)
//                                System.out.println(str);
//                        }
//                        catch (IOException e)
//                        {
//                            e.printStackTrace();
//                        }
//                    }
//                }));
            }
            else
            {
                process = Runtime.getRuntime().exec("sh " + startFilePath, null, new File(serverDir));
            }
            SERVER_PROCESSES.put(this.id, process);
        }
        catch (IOException e)
        {
            LOGGER.error("Could not start server with id=" + this.id, e);
        }

        this.isRunning = true;
    }

    public void stop()
    {
        //TODO: Read pid of the process from the file and try to stop it.

        final Process process = SERVER_PROCESSES.get(this.id);


        if (process == null || !process.isAlive())
        {
            LOGGER.warn("Tried to stop server that is not running. Server id=" + this.id);
            return;
        }
        process.destroy();
        this.isRunning = false;
    }

    public boolean isRunning()
    {
        final Process serverProcess = SERVER_PROCESSES.get(this.id);
        if (serverProcess != null && serverProcess.isAlive())
            return true;

        //TODO: Check pid file...

        return this.isRunning;
    }

    public void saveProperties(Map<String, String> settings)
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
        LOGGER.info("Saved server properties for server id=" + this.id);
    }

    public void loadProperties()
    {
        if (Files.exists(Paths.get(serverDir).resolve("server.properties")))
        {
            final Properties properties = new Properties();
            try
            {
                final InputStream inputStream = Files.newInputStream(Paths.get(serverDir).resolve("server.properties"));
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
            final int rconPort = properties.getProperty("rcon.port") != null ? Integer.parseInt(properties.getProperty("rcon.port")) : 0;
            final String rconPassword = properties.getProperty("rcon.password") != null ? properties.getProperty("rcon.password") : "";

            setLevelName(levelName);
            setOnlineMode(onlineMode);
            setPort(port);
            setPvp(pvp);
            setRconPort(rconPort);
            setRconPassword(rconPassword);
        }
    }
}
