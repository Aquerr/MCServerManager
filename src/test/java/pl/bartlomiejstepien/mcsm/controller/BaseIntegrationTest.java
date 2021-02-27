package pl.bartlomiejstepien.mcsm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import pl.bartlomiejstepien.mcsm.SpringSecurityTestConfiguration;
import pl.bartlomiejstepien.mcsm.auth.AuthenticatedUser;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = SpringSecurityTestConfiguration.class)
@AutoConfigureMockMvc
public abstract class BaseIntegrationTest
{
    protected static final Integer USER_ID = SpringSecurityTestConfiguration.USER_ID;
    protected static final String USER_USERNAME = SpringSecurityTestConfiguration.USER_USERNAME;
    protected static final String USER_PASSWORD = SpringSecurityTestConfiguration.USER_PASSWORD;

    @Autowired
    @Qualifier("userDetailsServiceTest")
    protected UserDetailsService userDetailsService;

    @Autowired
    protected MockMvc mockMvc;

    protected AuthenticatedUser getTestUser()
    {
        return SpringSecurityTestConfiguration.TEST_USER;
    }
}
