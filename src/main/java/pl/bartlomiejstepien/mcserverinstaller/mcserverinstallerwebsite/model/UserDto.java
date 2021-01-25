package pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

public class UserDto implements UserDetails
{
    private int id;

    private String username;

    private String password;

    private final List<ServerDto> serverDtos = new ArrayList<>();

    public UserDto()
    {

    }

    public UserDto(int id, String username, String password)
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
        this.serverDtos.add(serverDto);
    }

    public void removeServer(final ServerDto serverDto)
    {
        this.serverDtos.remove(serverDto);
    }

    public int getId()
    {
        return id;
    }

    public List<ServerDto> getServers()
    {
        return serverDtos;
    }

    public Optional<ServerDto> getServerById(final int id)
    {
        return this.serverDtos.stream().filter(serverDto -> serverDto.getId() == id).findFirst();
    }
    //    public List<Server> getServers()
//    {
//        return this.servers;
//    }
}
