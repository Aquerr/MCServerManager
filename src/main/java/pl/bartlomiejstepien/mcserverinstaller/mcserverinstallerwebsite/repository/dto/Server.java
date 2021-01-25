package pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.repository.dto;

import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model.ServerDto;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model.UserDto;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "server")
public class Server
{
    @Id
    @Column(name = "id", nullable = false, updatable = false, unique = true, insertable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "path")
    private String path;

    @ManyToMany(mappedBy = "servers", cascade = CascadeType.ALL)
    private final List<User> users = new ArrayList<>();

    public Server()
    {

    }

    public Server(int id, String path)
    {
        this.id = id;
        this.path = path;
    }

    public int getId()
    {
        return id;
    }

    public String getPath()
    {
        return path;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public List<User> getUsers()
    {
        return this.users;
    }

    public void addUser(User user)
    {
        this.users.add(user);
    }

    public void addUsers(List<User> users)
    {
        this.users.addAll(users);
    }

    public ServerDto toServer()
    {
        return ServerDto.fromServer(this);
    }
}
