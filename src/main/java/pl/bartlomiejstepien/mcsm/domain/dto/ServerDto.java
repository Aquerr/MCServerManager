package pl.bartlomiejstepien.mcsm.domain.dto;

import pl.bartlomiejstepien.mcsm.domain.model.ServerProperties;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ServerDto
{
    private int id;
    private String name;
    private String serverDir;

//    private final List<UserDto> userDtos = new ArrayList<>();
    private List<Integer> usersIds = new ArrayList<>();
    private final List<String> players = new LinkedList<>();
    private Path startFilePath;
    private String platform;
    private final ServerProperties serverProperties = new ServerProperties();

    private Integer javaId;

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

    public void setJavaId(Integer javaId)
    {
        this.javaId = javaId;
    }

    public Integer getJavaId()
    {
        return javaId;
    }

    //    public List<UserDto> getUsers()
//    {
//        return userDtos;
//    }

//    public void addUser(final UserDto userDto)
//    {
//        this.userDtos.add(userDto);
//    }

//    public void addUsers(List<UserDto> userDtos)
//    {
//        this.userDtos.addAll(userDtos);
//    }

//    public void removeUser(final UserDto userDto)
//    {
//        this.userDtos.remove(userDto);
//    }


    public void setName(String name)
    {
        this.name = name;
    }

    public void setServerDir(String serverDir)
    {
        this.serverDir = serverDir;
    }

    public List<Integer> getUsersIds()
    {
        return usersIds;
    }

    public void setUsersIds(List<Integer> usersIds)
    {
        this.usersIds = usersIds;
    }

    public Path getStartFilePath()
    {
        return startFilePath;
    }

    public ServerProperties getServerProperties()
    {
        return serverProperties;
    }

    public Path getLatestLogFilePath()
    {
        return Paths.get(this.serverDir).resolve("logs").resolve("latest.log");
    }

    public void setStartFilePath(Path startFilePath)
    {
        this.startFilePath = startFilePath;
    }

    public String getPlatform()
    {
        return platform;
    }

    public void setPlatform(String platform)
    {
        this.platform = platform;
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
