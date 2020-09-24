package pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.repository.dto;

import org.springframework.security.core.GrantedAuthority;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model.Server;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model.User;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "user")
public class UserDto
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
    private final List<ServerDto> servers = new ArrayList<>();

    public UserDto()
    {

    }

    public UserDto(int id, String username, String password)
    {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public static UserDto fromUser(User user)
    {
        if (user == null)
            throw new IllegalArgumentException("User cannot be null");

        final UserDto userDto = new UserDto(user.getId(), user.getUsername(), user.getPassword());
        for (final Server server : user.getServers())
        {
            final ServerDto serverDto = new ServerDto(server.getId(), server.getServerDir());
            serverDto.addUser(userDto);
            userDto.addServer(serverDto);
        }
        return userDto;
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

    public void addServer(final ServerDto serverDto)
    {
        this.servers.add(serverDto);
    }

    private void addServers(final List<ServerDto> serverDtos)
    {
        this.servers.addAll(serverDtos);
    }

    public int getId()
    {
        return id;
    }

    public List<ServerDto> getServers()
    {
        return servers;
    }

    public Optional<ServerDto> getServerById(final int id)
    {
        return this.servers.stream().filter(serverDto -> serverDto.getId() == id).findFirst();
    }

    public User toUser()
    {
        final User user = new User(this.id, this.username, this.password);
        for (final ServerDto serverDto : this.servers)
        {
            final Server server = Server.fromDto(serverDto);
            server.addUser(user);
            user.addServer(server);
        }
        return user;
    }

    //    public List<Server> getServers()
//    {
//        return this.servers;
//    }
}
