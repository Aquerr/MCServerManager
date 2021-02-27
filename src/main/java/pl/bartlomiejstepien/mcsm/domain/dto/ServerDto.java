package pl.bartlomiejstepien.mcsm.domain.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.bartlomiejstepien.mcsm.domain.model.ServerProperties;
import pl.bartlomiejstepien.mcsm.repository.ds.Server;
import pl.bartlomiejstepien.mcsm.repository.ds.User;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ServerDto
{
    private int id;
    private String name;
    private String serverDir;

    private final List<UserDto> userDtos = new ArrayList<>();

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

    public static ServerDto fromServer(final Server server)
    {
        //TODO: Load server information from server.properties.
        final String serverPath = server.getPath();
//        final User user = serverDto.getUser().toUser();
        final ServerDto serverDto = new ServerDto(server.getId(), server.getPath().substring(serverPath.lastIndexOf(File.separator) + 1), serverPath);

        for (final User user : server.getUsers())
        {
            final UserDto userDto = new UserDto(user.getId(), user.getUsername(), user.getPassword());
            userDto.addServer(serverDto);
            serverDto.addUser(userDto);
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
            serverDto.setStartFilePath(startFilePath);


        //Load server.properties
        serverDto.loadProperties();

        return serverDto;
    }

    public void setStartFilePath(Path startFilePath)
    {
        this.startFilePath = startFilePath;
    }

    public ServerDto()
    {

    }

    public ServerDto(int id, String name, String serverDir)
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

    public List<UserDto> getUsers()
    {
        return userDtos;
    }

    public void addUser(final UserDto userDto)
    {
        this.userDtos.add(userDto);
    }

    public void addUsers(List<UserDto> userDtos)
    {
        this.userDtos.addAll(userDtos);
    }

    public void removeUser(final UserDto userDto)
    {
        this.userDtos.remove(userDto);
    }

    public Path getStartFilePath()
    {
        return startFilePath;
    }

    public ServerProperties getServerProperties()
    {
        return serverProperties;
    }

    public void loadProperties()
    {

    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerDto serverDto = (ServerDto) o;
        return id == serverDto.id &&
                serverDir.equals(serverDto.serverDir);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id, serverDir);
    }
}
