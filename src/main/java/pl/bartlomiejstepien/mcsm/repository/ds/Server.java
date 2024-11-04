package pl.bartlomiejstepien.mcsm.repository.ds;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;
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
