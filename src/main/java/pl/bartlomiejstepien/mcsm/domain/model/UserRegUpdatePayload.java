package pl.bartlomiejstepien.mcsm.domain.model;

public class UserRegUpdatePayload
{
    private Integer id;
    private String username;
    private String password;
    private String role;

    public UserRegUpdatePayload()
    {

    }

    public UserRegUpdatePayload(Integer id, String username, String password, String role)
    {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public UserRegUpdatePayload(String username, String password, String role)
    {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
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

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getRole()
    {
        return role;
    }

    public void setRole(String role)
    {
        this.role = role;
    }
}
