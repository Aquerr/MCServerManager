package pl.bartlomiejstepien.mcsm.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.bartlomiejstepien.mcsm.dto.UserDto;
import pl.bartlomiejstepien.mcsm.repository.UserRepository;
import pl.bartlomiejstepien.mcsm.repository.ds.User;
import pl.bartlomiejstepien.mcsm.service.UserService;

@Service
public class H2UserDetailsService implements UserDetailsService
{
    private final UserRepository userRepository;

    @Autowired
    public H2UserDetailsService(final UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        final User user = this.userRepository.findByUsername(username);

        if (user == null)
            throw new UsernameNotFoundException("Could not find user with username = " + username);

        return new AuthenticatedUser(user.getId(), user.getUsername(), user.getPassword());
    }
}
