package pl.bartlomiejstepien.mcsm.domain.dto;

public class UserDto
{
    private int id;

    private String username;

    private String password;

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

    public int getId()
    {
        return id;
    }
}
