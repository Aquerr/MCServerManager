package pl.bartlomiejstepien.mcsm.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.bartlomiejstepien.mcsm.domain.dto.RoleEnum;

import java.util.Collection;
import java.util.Collections;

public class AuthenticatedUser implements UserDetails
{
    private final int id;
    private final String username;
    private final String password;

    private final String remoteIpAddress;

    private RoleEnum role;

    public AuthenticatedUser(int id, String username, String password, String remoteIpAddress, RoleEnum role)
    {
        this.id = id;
        this.username = username;
        this.password = password;
        this.remoteIpAddress = remoteIpAddress;
        this.role = role;
    }

    public int getId()
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

    public RoleEnum getRole()
    {
        return role;
    }
}
