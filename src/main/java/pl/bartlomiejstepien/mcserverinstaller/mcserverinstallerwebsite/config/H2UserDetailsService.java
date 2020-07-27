package pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.service.UserService;

public class H2UserDetailsService implements UserDetailsService
{
    private final UserService userService;

    @Autowired
    public H2UserDetailsService(final UserService userService)
    {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        //TODO: findByUsername should return User with Server (not ServerDto).
        final UserDetails userDetails = this.userService.findByUsername(username);

        if (userDetails == null)
            throw new UsernameNotFoundException("Could not find user with username = " + username);

        return userDetails;
    }
}
