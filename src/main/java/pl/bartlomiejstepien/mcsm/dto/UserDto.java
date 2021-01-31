package pl.bartlomiejstepien.mcsm.dto;

import pl.bartlomiejstepien.mcsm.dto.ServerDto;

import java.util.*;

public class UserDto
{
    private int id;

    private String username;

    private String password;

    private final List<ServerDto> serverDtos = new ArrayList<>();

    public UserDto()
    {

    }

    public UserDto(int id, String username, String password)
    {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public String getUsername()
    {
        return username;
    }

    public String getPassword()
    {
        return password;
    }

    public void addServer(final ServerDto serverDto)
    {
        this.serverDtos.add(serverDto);
    }

    public void removeServer(final ServerDto serverDto)
    {
        this.serverDtos.remove(serverDto);
    }

    public int getId()
    {
        return id;
    }

    public List<ServerDto> getServers()
    {
        return serverDtos;
    }

    public Optional<ServerDto> getServerById(final int id)
    {
        return this.serverDtos.stream().filter(serverDto -> serverDto.getId() == id).findFirst();
    }
}
