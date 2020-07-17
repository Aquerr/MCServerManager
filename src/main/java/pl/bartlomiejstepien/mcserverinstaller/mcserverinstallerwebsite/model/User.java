package pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.repository.dto.ServerDto;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "user")
public class User implements UserDetails
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

    public User()
    {

    }

    public User(int id, String username, String password)
    {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        return Collections.emptyList();
    }

    @Override
    public String getPassword()
    {
        return this.password;
    }

    @Override
    public String getUsername()
    {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired()
    {
        return true;
    }

    @Override
    public boolean isAccountNonLocked()
    {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired()
    {
        return true;
    }

    @Override
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

    public Optional<Server> getServerById(final int id)
    {
        final Optional<ServerDto> optionalServerDto = this.servers.stream().filter(serverDto -> serverDto.getId() == id).findFirst();
        return optionalServerDto.map(Server::fromDto);
    }
    //    public List<Server> getServers()
//    {
//        return this.servers;
//    }
}
