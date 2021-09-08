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
    private Integer id;

    @Column(name = "path", nullable = false, unique = true)
    private String path;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "platform", nullable = false)
    private String platform;

//    @ManyToMany(mappedBy = "servers", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
//    private final List<User> users = new ArrayList<>();
    @ElementCollection
    @CollectionTable(name = "user_server", joinColumns = {@JoinColumn(name = "server_id")})
    @Column(name = "user_id", nullable = false)
    private List<Integer> usersIds = new ArrayList<>();

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

    public void setId(Integer id)
    {
        this.id = id;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setUsersIds(List<Integer> usersIds)
    {
        this.usersIds = usersIds;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

//    public List<User> getUsers()
//    {
//        return this.users;
//    }

//    public void addUser(User user)
//    {
//        this.users.add(user);
//    }

//    public void addUsers(List<User> users)
//    {
//        this.users.addAll(users);
//    }

    public List<Integer> getUsersIds()
    {
        return usersIds;
    }

    public String getPlatform()
    {
        return platform;
    }

    public void setPlatform(String platform)
    {
        this.platform = platform;
    }
}
