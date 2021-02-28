package pl.bartlomiejstepien.mcsm.repository.ds;

import org.springframework.security.core.GrantedAuthority;
import pl.bartlomiejstepien.mcsm.domain.dto.ServerDto;
import pl.bartlomiejstepien.mcsm.domain.dto.UserDto;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "user")
public class User
{
    @Id
    @Column(name = "id", nullable = false, updatable = false, unique = true, insertable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "user_server", joinColumns = {@JoinColumn(name = "user_id")}, inverseJoinColumns = {@JoinColumn(name = "server_id")})
    private final List<Server> servers = new ArrayList<>();

    public User()
    {

    }

    public User(int id, String username, String password)
    {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public static User fromUser(UserDto userDto)
    {
        if (userDto == null)
            throw new IllegalArgumentException("User cannot be null");

        final User user = new User(userDto.getId(), userDto.getUsername(), userDto.getPassword());
        for (final ServerDto serverDto : userDto.getServers())
        {
            final Server server = new Server(serverDto.getId(), serverDto.getName(), serverDto.getServerDir());
            server.addUser(user);
            user.addServer(server);
        }
        return user;
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

    public boolean isAccountNonExpired()
    {
        return true;
    }

    public boolean isAccountNonLocked()
    {
        return true;
    }

    public boolean isCredentialsNonExpired()
    {
        return true;
    }

    public boolean isEnabled()
    {
        return true;
    }

    public void addServer(final Server server)
    {
        this.servers.add(server);
    }

    private void addServers(final List<Server> servers)
    {
        this.servers.addAll(servers);
    }

    public int getId()
    {
        return id;
    }

    public List<Server> getServers()
    {
        return servers;
    }

    public Optional<Server> getServerById(final int id)
    {
        return this.servers.stream().filter(serverDto -> serverDto.getId() == id).findFirst();
    }

//    public UserDto toUser()
//    {
//        final UserDto userDto = new UserDto(this.id, this.username, this.password);
//        for (final Server server : this.servers)
//        {
//            final ServerDto serverDto = ServerDto.fromServer(server);
//            serverDto.addUser(userDto);
//            userDto.addServer(serverDto);
//        }
//        return userDto;
//    }

    //    public List<Server> getServers()
//    {
//        return this.servers;
//    }
}
