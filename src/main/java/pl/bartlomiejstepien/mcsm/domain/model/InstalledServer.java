package pl.bartlomiejstepien.mcsm.domain.model;

import pl.bartlomiejstepien.mcsm.domain.dto.UserDto;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class InstalledServer
{
    private final int id;
    private final String name;
    private final Path serverDir;
    private final Path startFilePath;

    private final List<UserDto> userDtos = new ArrayList<>();

    private final List<String> players = new LinkedList<>();

    private String javaPath;

    public InstalledServer(int id, String name, Path serverDir, Path startFilePath)
    {
        this.id = id;
        this.name = name;
        this.serverDir = serverDir;
        this.startFilePath = startFilePath;
    }

    public void setJavaPath(String javaPath)
    {
        this.javaPath = javaPath;
    }

    public void addUser(UserDto userDto)
    {
        this.userDtos.add(userDto);
    }

    public void removeUser(UserDto userDto)
    {
        this.userDtos.remove(userDto);
    }

    public void addPlayer(String playerName)
    {
        this.players.add(playerName);
    }

    public void removePlayer(String playerName)
    {
        this.players.add(playerName);
    }

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public Path getServerDir()
    {
        return serverDir;
    }

    public Path getStartFilePath()
    {
        return startFilePath;
    }

    public List<UserDto> getUserDtos()
    {
        return userDtos;
    }

    public List<String> getPlayers()
    {
        return players;
    }

    public String getJavaPath()
    {
        return javaPath;
    }
}
