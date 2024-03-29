package pl.bartlomiejstepien.mcsm.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.bartlomiejstepien.mcsm.domain.model.Role;

import java.util.Collection;
import java.util.Collections;

public class AuthenticatedUser implements UserDetails
{
    private final Integer id;
    private final String username;
    private final String password;

    private final String remoteIpAddress;

    private Role role;

    public AuthenticatedUser(Integer id, String username, String password, String remoteIpAddress, Role role)
    {
        this.id = id;
        this.username = username;
        this.password = password;
        this.remoteIpAddress = remoteIpAddress;
        this.role = role;
    }

    public Integer getId()
    {
        return id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        return Collections.singletonList(new SimpleGrantedAuthority(role.getName()));
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

    public String getRemoteIpAddress()
    {
        return remoteIpAddress;
    }

    public Role getRole()
    {
        return role;
    }
}
