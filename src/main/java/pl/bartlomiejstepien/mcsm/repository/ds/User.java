package pl.bartlomiejstepien.mcsm.repository.ds;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
@Table(name = "mcsm_user")
@Data
public class User
{
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;
//
    @ManyToMany(mappedBy = "users", cascade = {CascadeType.REMOVE})
//    @JoinTable(name = "user_server",
//            joinColumns = {@JoinColumn(name = "user_id")},
//            inverseJoinColumns = {@JoinColumn(name = "server_id")})
    private final List<Server> servers = new ArrayList<>();

//    @ElementCollection
//    @CollectionTable(name = "user_server", joinColumns = {@JoinColumn(name = "user_id")})
//    @Column(name = "server_id", nullable = false)
//    private List<Integer> serversIds = new ArrayList<>();

    @Column(name = "role_id")
    private Integer roleId;

    public User()
    {

    }

    public User(String username, String password, Integer roleId)
    {
        this.username = username;
        this.password = password;
        this.roleId = roleId;
    }

    public User(Integer id, String username, String password, Integer roleId)
    {
        this.id = id;
        this.username = username;
        this.password = password;
        this.roleId = roleId;
    }

    public boolean doesOwnServer(Integer serverId)
    {
        return this.getServers().stream()
                .anyMatch(server -> server.getId().equals(serverId));
    }
}
