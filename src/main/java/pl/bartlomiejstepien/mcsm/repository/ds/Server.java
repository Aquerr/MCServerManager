package pl.bartlomiejstepien.mcsm.repository.ds;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "server")
@Data
public class Server
{
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "path", nullable = false, unique = true)
    private String path;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "platform", nullable = false)
    private String platform;

    @ManyToMany
    @JoinTable(name = "user_server",
            joinColumns = @JoinColumn(name = "server_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private final List<User> users = new ArrayList<>();

//    @ElementCollection
//    @CollectionTable(name = "user_server", joinColumns = {@JoinColumn(name = "server_id")})
//    @Column(name = "user_id", nullable = false)
//    private List<Integer> usersIds = new ArrayList<>();

    @Column(name = "java_id")
    private Integer javaId;

    public Server()
    {

    }

    public Server(int id, String name, String path)
    {
        this.id = id;
        this.name = name;
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
}
