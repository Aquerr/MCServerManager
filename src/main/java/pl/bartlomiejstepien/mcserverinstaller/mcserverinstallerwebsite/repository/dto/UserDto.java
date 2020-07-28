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
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "user")
    private List<ServerDto> servers;

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
        final UserDto userDto = new UserDto(user.getId(), user.getUsername(), user.getPassword());
        for (final Server server : user.getServers())
        {
            final ServerDto serverDto = new ServerDto(server.getId(), server.getPath(), userDto);
            serverDto.setUser(userDto);
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
        if (this.servers == null)
            this.servers = new ArrayList<>();

        this.servers.add(serverDto);
        serverDto.setUser(this);
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
            server.setUser(user);
            user.addServer(server);
        }
        return user;
    }

    //    public List<Server> getServers()
//    {
//        return this.servers;
//    }
}
