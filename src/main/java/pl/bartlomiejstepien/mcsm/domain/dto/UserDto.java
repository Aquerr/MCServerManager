package pl.bartlomiejstepien.mcsm.domain.dto;

import java.util.*;

public class UserDto
{
    private Integer id;
    private String username;
    private List<Integer> serverIds = new ArrayList<>();
    private Role role;

    public UserDto()
    {

    }

    public UserDto(Integer id, String username)
    {
        this.id = id;
        this.username = username;
    }

    public String getUsername()
    {
        return username;
    }

    public Integer getId()
    {
        return id;
    }

    public List<Integer> getServerIds()
    {
        return serverIds;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public void setServerIds(List<Integer> serverIds)
    {
        this.serverIds = serverIds;
    }

    public Role getRole()
    {
        return role;
    }

    public void setRole(Role role)
    {
        this.role = role;
    }
}
