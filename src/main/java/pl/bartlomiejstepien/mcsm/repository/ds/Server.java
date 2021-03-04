package pl.bartlomiejstepien.mcsm.repository.ds;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "server")
public class Server
{
    @Id
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "path", nullable = false, unique = true)
    private String path;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToMany(mappedBy = "servers", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    private final List<User> users = new ArrayList<>();

    public Server()
    {

    }

    public Server(int id, String name, String path)
    {
        this.id = id;
        this.name = name;
        this.path = path;
    }

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
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

//    public ServerDto toServer()
//    {
//        return ServerDto.fromServer(this);
//    }
}
