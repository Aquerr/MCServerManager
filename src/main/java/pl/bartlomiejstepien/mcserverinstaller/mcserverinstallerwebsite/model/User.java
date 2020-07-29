package pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.repository.dto.ServerDto;

import javax.persistence.*;
import java.util.*;

public class User implements UserDetails
{
    private int id;

    private String username;

    private String password;

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

    public void addServer(final Server server)
    {
        this.servers.add(server);
    }

    public void removeServer(final Server server)
    {
        this.servers.remove(server);
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
    //    public List<Server> getServers()
//    {
//        return this.servers;
//    }
}
