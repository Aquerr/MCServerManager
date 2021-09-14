package pl.bartlomiejstepien.mcsm.domain.dto;

import java.util.*;

public class UserDto
{
    private Integer id;

    private String username;

    private String password;

//    private final List<ServerDto> serverDtos = new ArrayList<>();
    private List<Integer> serverIds = new ArrayList<>();

    private RoleEnum role;

    public UserDto()
    {

    }

    public UserDto(Integer id, String username, String password)
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

//    public void addServer(final ServerDto serverDto)
//    {
//        this.serverDtos.add(serverDto);
//    }

//    public void removeServer(final ServerDto serverDto)
//    {
//        this.serverDtos.remove(serverDto);
//    }

    public Integer getId()
    {
        return id;
    }

//    public List<ServerDto> getServers()
//    {
//        return serverDtos;
//    }

//    public Optional<ServerDto> getServerById(final int id)
//    {
//        return this.serverDtos.stream().filter(serverDto -> serverDto.getId() == id).findFirst();
//    }

    public List<Integer> getServerIds()
    {
        return serverIds;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public void setServerIds(List<Integer> serverIds)
    {
        this.serverIds = serverIds;
    }

    public RoleEnum getRole()
    {
        return role;
    }

    public void setRole(RoleEnum role)
    {
        this.role = role;
    }
}
