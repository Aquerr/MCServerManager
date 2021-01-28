package pl.bartlomiejstepien.mcsm.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pl.bartlomiejstepien.mcsm.model.UserDto;
import pl.bartlomiejstepien.mcsm.service.UserService;

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
        final UserDto userDtoDetails = this.userService.findByUsername(username);

        if (userDtoDetails == null)
            throw new UsernameNotFoundException("Could not find user with username = " + username);

        return new AuthenticatedUser(userDtoDetails.getId(), userDtoDetails.getUsername(), userDtoDetails.getPassword());
    }
}
