package pl.bartlomiejstepien.mcsm;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;
import pl.bartlomiejstepien.mcsm.auth.AuthenticatedUser;

import static org.mockito.Mockito.when;

@TestConfiguration
public class SpringSecurityTestConfiguration
{
    public static final Integer USER_ID = 1;
    public static final String USER_USERNAME = "mcsm";
    public static final String USER_PASSWORD = "test123";

    public static final AuthenticatedUser TEST_USER = new AuthenticatedUser(USER_ID, USER_USERNAME, USER_PASSWORD);

    @Bean("userDetailsServiceTest")
    @Primary
    public UserDetailsService userDetailsService()
    {
        UserDetailsService userDetailsService = Mockito.mock(UserDetailsService.class);
        when(userDetailsService.loadUserByUsername(USER_USERNAME)).thenReturn(TEST_USER);
        return userDetailsService;
    }
}
