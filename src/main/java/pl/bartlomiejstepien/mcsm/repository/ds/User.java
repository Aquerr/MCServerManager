package pl.bartlomiejstepien.mcsm.repository.ds;

import org.springframework.security.core.GrantedAuthority;
import pl.bartlomiejstepien.mcsm.domain.dto.ServerDto;
import pl.bartlomiejstepien.mcsm.domain.dto.UserDto;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "mcsm_user")
public class User
{
    @Id
    @Column(name = "id", nullable = false, updatable = false, unique = true, insertable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;
//
//    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
//    @JoinTable(name = "user_server", joinColumns = {@JoinColumn(name = "user_id")}, inverseJoinColumns = {@JoinColumn(name = "server_id")})
//    private final List<Server> servers = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "user_server", joinColumns = {@JoinColumn(name = "user_id")})
    @Column(name = "server_id", nullable = false)
    private List<Integer> serversIds = new ArrayList<>();

    public User()
    {

    }

    public User(Integer id, String username, String password)
    {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        return Collections.emptyList();
    }

    public String getPassword()
    {
        return this.password;
    }

    public String getUsername()
    {
        return this.username;
    }

//    public void addServer(final Server server)
//    {
//        server.getUsers().add(this);
//        this.servers.add(server);
//    }

//    private void addServers(final List<Server> servers)
//    {
//        this.servers.addAll(servers);
//    }

    public int getId()
    {
        return id;
    }

    public List<Integer> getServersIds()
    {
        return serversIds;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public void setServersIds(List<Integer> serversIds)
    {
        this.serversIds = serversIds;
    }

    //    public List<Server> getServers()
//    {
//        return servers;
//    }
}
