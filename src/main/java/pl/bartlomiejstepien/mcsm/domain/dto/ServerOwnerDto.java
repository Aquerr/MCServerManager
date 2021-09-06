package pl.bartlomiejstepien.mcsm.domain.dto;

public class ServerOwnerDto
{
    private int id;
    private String username;
    private int serverId;

    public ServerOwnerDto()
    {

    }

    public ServerOwnerDto(int id, String username, int serverId)
    {
        this.id = id;
        this.username = username;
        this.serverId = serverId;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public int getServerId()
    {
        return serverId;
    }

    public void setServerId(int serverId)
    {
        this.serverId = serverId;
    }
}
